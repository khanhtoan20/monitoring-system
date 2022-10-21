package models;

import controllers.Controller;
import server.Server;
import utils.Helper;
import utils.JSON;
import utils.console;

import java.io.IOException;
import java.net.Socket;

import static utils.Command.COMMAND;
import static utils.Environment.DEFAULT_SERVER_HOST;
import static utils.Environment.SOCKET_MODEL_ID;

public class ClientSocketModel extends SocketModel {
    private String ram;
    private String cpu;
    private String disk;

    public ClientSocketModel(Socket socket) {
        this.uuid = Helper.getRandomUUID();
        this.socket = socket;
    }

    @Override
    public String onConsume() {
        try {
            return this.inputStream.readLine();
        } catch (Exception e) {
            console.error(this.getStackTrace(e));
            return null;
        }
    }

    @Override
    public void onStart() {
        this.initInputStream();
        this.initOutputStream();

        String input;

        while (!Thread.currentThread().isInterrupted()) {
            while ((input = this.onConsume()) != null) {
                this.onHandle(input);
            }
            this.onStop();
        }
    }

    @Override
    public void onHandle(String message) {
        try {
            JSON json = new JSON(message).put(SOCKET_MODEL_ID, this.uuid);
            Controller.get(json.get(COMMAND)).execute(json, this);
        } catch (Exception e) {
            console.error(this.getStackTrace(e));
            this.onSend(new MessageModel(DEFAULT_SERVER_HOST, uuid, e.getMessage()).json());
        }
    }

    @Override
    public void onSend(String message) {
        try {
            this.outputStream.writeBytes(message + "\n");
        } catch (IOException e) {
            console.error(this.getStackTrace(e));
        }
    }

    @Override
    public void onStop() {
        try {
            console.log("Connection is closing...., UUID: " + this.uuid);
            Server.getClientConnections().remove(this.getUUID());
            this.inputStream.close();
            this.outputStream.close();
            this.socket.close();
            Thread.currentThread().stop();
        } catch (Exception e) {
            console.error("Failure to close connection uuid: " + this.uuid + ", exception: " + e.getMessage());
        }
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    public String getRam() {
        return ram;
    }

    public String getCpu() {
        return cpu;
    }

    public String getDisk() {
        return disk;
    }
}
