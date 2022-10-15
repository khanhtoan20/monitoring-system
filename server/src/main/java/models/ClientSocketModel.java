package models;

import controllers.Controller;
import org.json.JSONObject;
import server.Server;
import utils.Database;
import utils.Executable;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import static utils.Helper.getMd5;

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
        this.getInputStream();
        this.getOutputStream();

        String input;

        while (!Thread.currentThread().isInterrupted()) {
            while ((input = this.onConsume()) != null) {
                this.onHandle(input);
            }
            this.onStop();
        }
    }

    @Override
    public String onConsume() {
        try {
            String temp = this.inputStream.readLine();
            println(temp);
            return temp;
        } catch (Exception e) {
//            e.printStackTrace();
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
            this.outputStream.writeBytes(message + "\n");
        } catch (IOException e) {
            println("Failure to send, exception =" + e.getMessage());
        }
    }

    @Override
    public void onStop() {
        try {
            println("Connection is closing...., uuid = " + this.uuid);
            // we might need to submit a logout demand before closing a connection.
            this.inputStream.close();
            this.outputStream.close();
            this.socket.close();
            Server.getClientConnections().remove(this.getUUID());
            Thread.currentThread().stop();
        } catch (Exception e) {
            println("Failure to close connection uuid = " + this.uuid + ", exception = " + e.getMessage());
        }
    }

    @Override
    public void println(String message) {
        System.out.println("[" + Thread.currentThread().getName() + "][ClientSocketModel] " + message);
    }

    private String isAuthorized(Object input) {
        JSONObject temp = (JSONObject) input;
        String username = temp.get("username").toString();
        String password = getMd5(temp.get("password").toString());
        Integer count = Database.count("administrators", "username=" + username + " AND password=" +password);

        if (count == null || count == 0 ) {
            return "login failed";
        }

        return "login successfully";
    }

    private void onHandle(String input) {
        JSONObject json;
        String command;
        try {
            json = new JSONObject(input);
            command = json.get("command").toString();
            Executable executor = Controller.get(command);
            this.onSend(executor.execute(json));
        } catch (Exception e) {
            this.onSend(e.getMessage());
        }
    }
}
