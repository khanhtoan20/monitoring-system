package controllers;

import models.ClientSocketModel;
import models.MessageModel;
import models.SocketModel;
import server.Server;
import utils.JSON;
import utils.console;

import java.util.HashMap;
import java.util.Map;

import static utils.Command.*;
import static utils.Environment.*;

public class Controller {
    public static Map<String, Executable<JSON>> controller = new HashMap();

    public static void init() {
        put(COMMAND_LOGIN, Controller::getLogin);
        put(COMMAND_BROADCAST, Controller::getBroadcast);
        put(COMMAND_GET_ALL_CLIENTS, Controller::getAllClients);
        put(COMMAND_CLIENT_SYSTEM_INFO, Controller::getClientSystemInfo);
    }

    private static void getClientSystemInfo(JSON json, SocketModel model) {
        String uuid = model.getUUID();
        ClientSocketModel temp = Server.getClientConnections().get(uuid);
        temp.setCpu(json.get("cpu"));
        temp.setRam(json.get("ram"));
        temp.setDisk(json.get("disk"));
        console.warn("Client[" + uuid + "] updated system info!");
    }

    private static void getLogin(JSON json, SocketModel model) {
        String uuid = model.getUUID();
        Server.setHost(uuid);
        console.warn("Client[" + uuid + "] is now a Administrator");
        model.onSend(new MessageModel(DEFAULT_SERVER_HOST, uuid, COMMAND_RESPONSE).put(MESSAGE, RESPONSE_SUCCESS).json());
    }

    private static void getAllClients(JSON json, SocketModel model) {
        String temp = new MessageModel(DEFAULT_SERVER_HOST, model.getUUID(), COMMAND_GET_ALL_CLIENTS).put("clients", Server.getClientConnections().values()).json();
        model.onSend(temp);
    }

    private static void getBroadcast(JSON json, SocketModel model) {
        String uuid = model.getUUID();
        String message = json.get(MESSAGE);
        String response = new MessageModel(DEFAULT_SERVER_HOST, uuid, COMMAND_RESPONSE).put(MESSAGE, message).json();
        Server.getClientConnections().values().forEach(client -> {
            client.onSend(response);
        });
    }

    private static boolean isAuthorized(String uuid) {
        try {
            return uuid == Server.getHost().getUUID();
        } catch (NullPointerException e) {
            return false;
        }
    }

    private static String getNotFoundController(JSON json, SocketModel model) {
        return new MessageModel(DEFAULT_SERVER_HOST, model.getUUID(), NOT_FOUND_CONTROLLER).json();
    }

    public static Executable get(String key) {
        return controller.getOrDefault(key, Controller::getNotFoundController);
    }

    public static Executable put(String key, Executable<JSON> value) {
        return controller.put(key, value);
    }
}
