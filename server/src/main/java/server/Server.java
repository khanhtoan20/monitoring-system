package server;

import controllers.Controller;
import models.ClientSocketModel;
import utils.console;

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
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_SERVER_PORT)) {
            utils.Helper.registerServerConfig();
            console.info("Server is listening on port: " + DEFAULT_SERVER_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientSocketModel newUser = new ClientSocketModel(socket);
                console.log("Client[" + newUser.getUUID() + "] has joined");
                clientConnections.put(newUser.getUUID(), newUser);
                new Thread(newUser).start();
            }
        } catch (Exception ex) {
            console.error(ex.getMessage());
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

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}