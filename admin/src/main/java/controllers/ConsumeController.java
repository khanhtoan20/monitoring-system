package controllers;

import GUI.DashboardGUI;
import org.json.JSONObject;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static utils.Command.COMMAND_GET_ALL_CLIENTS;

public class ConsumeController {

    private static Map<String, ConsumeExecutable<JSONObject>> controller = new HashMap();

    public static void init() {
        put(COMMAND_GET_ALL_CLIENTS, ConsumeController::getAllClients);
    }

    private static void getAllClients(JSONObject input, Socket socket) {
        DashboardGUI.add(input);
    }

    public static ConsumeExecutable get(String key) {
        return controller.getOrDefault(key, ConsumeController::getNotFoundController);
    }

    private static void getNotFoundController(JSONObject jsonObject, Socket socket) throws Exception {
        throw new Exception("Invalid Command");
    }

    public static ConsumeExecutable put(String key, ConsumeExecutable<JSONObject> value) {
        return controller.put(key, value);
    }
}
