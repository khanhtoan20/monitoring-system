package admin;

import controllers.ConsumeController;
import controllers.ProduceController;
import controllers.base.ProduceExecutable;
import models.ClientModel;
import swing.DashboardGUI;
import utils.Helper;
import utils.JSON;
import utils.console;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

import static utils.Command.*;
import static utils.Environment.DEFAULT_SERVER_HOST;
import static utils.Environment.DEFAULT_SERVER_PORT;

public class Admin {
    private Socket connection;
    private BufferedReader scanner;
    private BufferedReader inputStream;
    private DataOutputStream outputStream;
    private HashMap clients;
    private static DashboardGUI gui;

    public Admin(DashboardGUI gui) {
        ProduceController.init();
        ConsumeController.init();
        clients = new HashMap<String, ClientModel>();
        Admin.gui = gui;
    }

    public HashMap<String, ClientModel> getClients() {
        return clients;
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

            this.onSend(ProduceController.get(COMMAND_LOGIN).execute(null));

            new Thread(this::onConsume).start();
            new Thread(this::onProduce).start();
            new Thread(this::fetch).start();
        } catch (Exception e) {

        }
    }

    public void fetch() {
        while (true) {
            try {
                console.warn("[Admin][fetch]");
                this.onSend(ProduceController.get(COMMAND_GET_ALL_CLIENTS).execute(null));
                Thread.currentThread().sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onConsume() {
        while (true) {
            try {
                String response = this.inputStream.readLine();
                JSON json = new JSON(response);
                console.log("debug" + response);
                ConsumeController.get(json.get(COMMAND)).execute(json, this);
            } catch (Exception e) {
                e.printStackTrace();
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
            e.printStackTrace();
            this.onStop();
        }
    }

    public void onHandle(String message) {
        try {
            ProduceExecutable executor = ProduceController.get(message);
            this.onSend(executor.execute(this));
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }


    public static DashboardGUI getGui() {
        return gui;
    }
}
