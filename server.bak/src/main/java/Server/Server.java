package Server;

import models.ClientSocketModel;
import models.SocketModel;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static utils.Environment.DEFAULT_SERVER_PORT;

public class Server {
    private static Map<String, SocketModel> clientConnections = new ConcurrentHashMap<>();

    public static Map<String, SocketModel> getClientConnections() {
        return clientConnections;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_SERVER_PORT)) {
            System.out.println("Server is listening on port " + DEFAULT_SERVER_PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("CLIENT JOINED");
                ClientSocketModel newUser = new ClientSocketModel(socket);
                clientConnections.put(newUser.getUUID(), newUser);
                new Thread(newUser).start();
            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server s = new Server();
        s.start();
    }
}