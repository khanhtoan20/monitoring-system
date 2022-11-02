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
    public String ram;
    public String cpu;
    public String disk;
    public String os;
    public String ip;
    public String MAC_address;
    public String hostName;

    public ClientSocketModel(Socket socket) {
        this.uuid = Helper.getRandomUUID();
        this.socket = socket;
    }

    @Override
    public String onConsume() {
        try {
            return this.inputStream.readLine();
        } catch (Exception e) {
            if (e.getMessage().contains("Connection reset")) return null;
            e.printStackTrace();
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
            e.printStackTrace();
            this.onSend(new MessageModel(DEFAULT_SERVER_HOST, uuid, e.getMessage()).json());
        }
    }

    @Override
    public synchronized void onSend(String message) {
        try {
            this.outputStream.writeBytes(message + "\n");
        } catch (IOException e) {
            if (e.getMessage().contains("reset|closed")) return;
            e.printStackTrace();
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

    public String getIp() {
        return ip;
    }

    public String getOs() {
        return os;
    }

    public String getMAC_address() {
        return MAC_address;
    }

    public String getHostName() {
        return hostName;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMAC_address(String MAC_address) {
        this.MAC_address = MAC_address;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
