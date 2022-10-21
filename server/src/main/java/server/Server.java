package server;

import controllers.Controller;
import models.ClientSocketModel;
import utils.Helper;
import utils.console;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static utils.Environment.DEFAULT_SERVER_PORT;

public class Server {
    private static ClientSocketModel host;
    private static Map<String, ClientSocketModel> clientConnections = new ConcurrentHashMap<>();

    public Server() {
        Controller.init();
//        AdminController.init();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_SERVER_PORT)) {
            console.info("Server is listening on port: " + DEFAULT_SERVER_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientSocketModel newUser = new ClientSocketModel(socket);
                clientConnections.put(newUser.getUUID(), newUser);
                console.info("Client[" + newUser.getUUID() + "] has joined");
                new Thread(newUser).start();
            }
        } catch (IOException ex) {
            console.error(this.getStackTrace(ex));
        }
    }

    public static void setHost(String uuid) {
        host = clientConnections.get(uuid);
        clientConnections.remove(uuid);
    }

    public static ClientSocketModel getHost() {
        return host;
    }

    public static Map<String, ClientSocketModel> getClientConnections() {
        return clientConnections;
    }

    private String getStackTrace(Exception exception) {
        return Helper.getStackTrace(this.getClass()) + exception.getMessage();
    }

    public static void main(String[] args) {
        Server s = new Server();
        s.start();
    }
}