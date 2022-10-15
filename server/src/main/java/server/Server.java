package server;

import controllers.Controller;
import models.ClientSocketModel;
import models.SocketModel;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static utils.Environment.DEFAULT_SERVER_PORT;

public class Server {
    private static ClientSocketModel host;
    private static Map<String, SocketModel> clientConnections = new ConcurrentHashMap<>();

    public Server() {
        Controller.init();
    }
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_SERVER_PORT)) {
            println("Server is listening on port " + DEFAULT_SERVER_PORT);
            println("Waiting for host...");

            while (true) {
                Socket socket = serverSocket.accept();
                println("New client joined");
                ClientSocketModel newUser = new ClientSocketModel(socket);
                clientConnections.put(newUser.getUUID(), newUser);
                new Thread(newUser).start();
            }
        } catch (IOException ex) {
            println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static Map<String, SocketModel> getClientConnections() {
        return clientConnections;
    }

    public void println(String message) {
        System.out.println("[SERVER] " + message);
    }

    public static void main(String[] args) {
        Server s = new Server();
        s.start();
    }
}