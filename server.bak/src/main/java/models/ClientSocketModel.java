package models;

import Server.Server;
import jdk.nashorn.internal.runtime.JSONListAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ClientSocketModel extends SocketModel {
    public ClientSocketModel(Socket socket) {
        try {
            this.uuid = UUID.randomUUID().toString();
            this.socket = socket;
        } catch (Exception e) {
            println("Failure to initialize, exception =" + e.getMessage());
        }
    }

    @Override
    public void onStart() {
        String input;
        String output;
        this.getInputStream();
        this.getOutputStream();
        while(!Thread.currentThread().isInterrupted()) {
            while (!((input = this.onConsume()) == null)) {
                output = input + "\n";
//                JSONObject temp = new JSONObject();
                temp.put("clients", new JSONArray(new ArrayList<>(Server.getClientConnections().keySet())));
                this.onSend(temp + "\n");
            }
        }
    }

    /**
     * consuming data method
     */
    @Override
    public String onConsume() {
        try {
            return this.inputStream.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getUUID() {
        return this.uuid;
    }

    @Override
    public void onSend(String message) {
        try {
            this.outputStream.writeBytes(message);
            println("Message sent");
        } catch (IOException e) {
            println("Failure to send, exception =" + e.getMessage());
        }
    }

    @Override
    public void println(String message) {
        System.out.println("["+Thread.currentThread().getName()+"][ClientSocketModel] "+ message);
    }
}
