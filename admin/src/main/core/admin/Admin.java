package admin;

import controllers.ConsumeController;
import controllers.ProduceController;
import controllers.base.ProduceExecutable;
import models.SystemInfoModel;
import models.Worker;
import swing.index;
import utils.JSON;
import utils.console;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import static utils.Command.*;
import static utils.Environment.DEFAULT_SERVER_HOST;
import static utils.Environment.DEFAULT_SERVER_PORT;

public class Admin {
    private static final String CONSUMER_WORKER = "consumer_worker";
    private static final String FETCH_WORKER = "admin_fetch_worker";

    private HashMap clients;
    public Socket connection;
    private BufferedReader scanner;
    private BufferedReader inputStream;
    private DataOutputStream outputStream;
    private static index gui;

    public Admin(index gui) {
        Admin.gui = gui;
        ProduceController.init();
        ConsumeController.init();
        clients = new HashMap<String, SystemInfoModel>();
        index.workers.put(FETCH_WORKER, new Worker(this::fetch));
        index.workers.put(CONSUMER_WORKER, new Worker(this::onConsume));

        Thread handleStreamingServer = new Thread(() -> {
            try {
                while (true) {

                    System.out.println("Start Server");
                    ServerSocket soc = new ServerSocket(8888);
                    Socket so = soc.accept();
                    System.out.println(so.getInetAddress().getAddress());
                    BufferedImage img = ImageIO.read(so.getInputStream());
                    ImageIcon imageIcon = new ImageIcon(img);
                    Admin.getGui().fetchClientMonitor(imageIcon);

                    System.out.println("Start Server");
                    soc.close();
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                    }


                }
            } catch (Exception e) {
            }
        });
        handleStreamingServer.setDaemon(true);
        handleStreamingServer.start();
    }

    public void resetClients() {
        this.clients = new HashMap<String, SystemInfoModel>();
    }

    public HashMap<String, SystemInfoModel> getClients() {
        return clients;
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
            this.onSend(ProduceController.get(COMMAND_GET_HOST_PRIVILEGE).execute(null));

            index.workers.get(FETCH_WORKER).start();
            index.workers.get(CONSUMER_WORKER).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetch() {
        while (true) {
            try {
                this.onSend(ProduceController.get(COMMAND_GET_CLIENTS).execute(null));
                Thread.currentThread().sleep(10000);
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
                ConsumeController.get(json.get(COMMAND)).execute(json, this);
            } catch (Exception e) {
                e.printStackTrace();
                this.onStop();
            }
        }
    }

    public void onHandle(String message) {
        try {
            ProduceExecutable executor = ProduceController.get(message);
            this.onSend(executor.execute(this));
        } catch (Exception e) {
            if (e instanceof SocketException) return;
            e.printStackTrace();
        }
    }

    public synchronized void onSend(String message) throws Exception {
        this.outputStream.writeBytes(message + "\n");
    }

    public void onStop() {
        try {
            console.warn("Connection is closing...");
            this.outputStream.close();
            this.inputStream.close();
            this.connection.close();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static index getGui() {
        return gui;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new index().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        });
    }
}
