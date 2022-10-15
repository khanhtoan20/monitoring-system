package controllers;

import org.json.JSONObject;
import utils.Database;
import utils.Executable;

import java.util.HashMap;
import java.util.Map;

import static utils.Environment.COMMAND_LOGIN;
import static utils.Helper.getMd5;

public class Controller {
    public static Map<String, Executable> controller = new HashMap();

    public static Executable get(String key) {
        return controller.get(key);
    }

    public static Executable put(String key, Executable value) {
        return controller.put(key, value);
    }

    public static void init() {
        controller.put(COMMAND_LOGIN, Controller::isAuthorized);
    }

    private static String isAuthorized(Object input) {
        JSONObject temp = (JSONObject) input;
        String username = temp.get("username").toString();
        String password = getMd5(temp.get("password").toString());
        Integer count = Database.count("administrators", "username=" + username + " AND password=" +password);

        if (count == null || count == 0 ) {
            return "login failed";
        }

        return "login successfully";
    }

}
