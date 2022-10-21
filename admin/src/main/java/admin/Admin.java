package admin;

import controllers.ConsumeController;
import controllers.ProduceController;
import controllers.ProduceExecutable;
import models.SystemInfo;
import utils.Helper;
import utils.JSON;
import utils.console;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import static utils.Command.*;
import static utils.Environment.*;

public class Admin {
    private Socket connection;
    private BufferedReader scanner;
    private BufferedReader inputStream;
    private DataOutputStream outputStream;
    private static SystemInfo systemInfo = new SystemInfo();

    public Admin() {
        ProduceController.init();
        ConsumeController.init();
    }

    public BufferedReader getScanner() {
        return scanner == null ? scanner = new BufferedReader(new InputStreamReader(System.in)) : scanner;
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
            console.info("LOGGED IN, WAITING FOR INPUT...");

            this.getOutputStream();
            this.getInputStream();

            this.onSend(ProduceController.get(COMMAND_CLIENT_SYSTEM_INFO).execute(null));
            new Thread(this::onConsume).start();
            new Thread(this::onProduce).start();
            new Thread(this::fetch).start();
        } catch (Exception e) {
            console.error(this.getStackTrace(e));
        }
    }

    public void fetch() {
        while (true) {
            try {
                this.onSend(ProduceController.get(COMMAND_GET_ALL_CLIENTS).execute(null));
                Thread.currentThread().sleep(2000);
            } catch (Exception e) {
                console.error(this.getStackTrace(e));
            }
        }
    }

    public void onConsume() {
        while (true) {
            try {
                String response = this.inputStream.readLine();
                JSON json = new JSON(response);
                ConsumeController.get(json.get(COMMAND)).execute(json, this.connection);
            } catch (Exception e) {
                console.error(this.getStackTrace(e));;
                this.onStop();
            }
        }
    }

    public void onProduce() {
        String output;
        try {
            while (!(output = this.getScanner().readLine()).equals("x")) {
                this.onHandle(output);
            }
            this.onStop();
        } catch (Exception e) {
            console.error(this.getStackTrace(e));
            this.onStop();
        }
    }

    public void onHandle(String message) {
        try {
            ProduceExecutable executor = ProduceController.get(message);
            this.onSend(executor.execute(null));
        } catch (Exception e) {
            console.error(this.getStackTrace(e));
        }
    }

    public void onSend(String message) throws Exception {
        this.outputStream.writeBytes(message + "\n");
    }

    public void onStop() {
        try {
            console.warn("Connection is closing...");
            this.outputStream.close();
            this.inputStream.close();
            this.connection.close();
            System.exit(1);
        } catch (Exception e) {
            console.error(this.getStackTrace(e));
        }
    }

    public static SystemInfo getSystemInfo() {
        return systemInfo;
    }

    protected String getStackTrace(Exception exception) {
        return Helper.getStackTrace(this.getClass()) + exception.getMessage();
    }

    public static void main(String[] args) {
        new Admin().start();
//        new main().start();
    }


}
