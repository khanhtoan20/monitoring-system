package client;

import controllers.Controller;
import controllers.Executable;
import utils.Helper;
import utils.JSON;
import utils.console;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static utils.Environment.DEFAULT_SERVER_HOST;
import static utils.Environment.DEFAULT_SERVER_PORT;

public class Client {
    private Socket connection;
    private BufferedReader inputStream;
    private DataOutputStream outputStream;

    public Client() {
        Controller.init();
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

            while ((input = this.inputStream.readLine()) != null) {
                this.onHandle(input);
            }

        } catch (Exception e) {
            console.error(this.getStackTrace(e));
        }
    }

    public void onHandle(String message) {
        try {
            JSON json = new JSON(message);
            Executable executor = Controller.get(json.get("command"));
            this.onSend(executor.execute(json));
        } catch (Exception e) {
            console.error(this.getStackTrace(e));
        }
    }

    public void onSend(String message) {
        try {
            this.outputStream.writeBytes(message + "\n");
        } catch (IOException e) {
            console.error(this.getStackTrace(e));
        }
    }

    protected String getStackTrace(Exception exception) {
        return Helper.getStackTrace(this.getClass()) + exception.getMessage();
    }

    public static void main(String[] args) {
        Client c = new Client();
        c.start();
    }
}
