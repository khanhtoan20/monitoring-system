package models;

import java.io.*;
import java.net.Socket;

public abstract class SocketModel implements Runnable {
    protected String uuid;
    protected Socket socket;
    protected BufferedReader inputStream;
    protected DataOutputStream outputStream;

    public BufferedReader getInputStream() {
        try {
            return this.inputStream == null ? this.inputStream = new BufferedReader(new InputStreamReader(this.socket.getInputStream())) : this.inputStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DataOutputStream getOutputStream() {
        try {
            return this.outputStream == null ? this.outputStream = new DataOutputStream(this.socket.getOutputStream()) : this.outputStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void run() {
        onStart();
    }

    /**
     * Each concrete model might have different behavior, so I decide to use the factory pattern method to get more flexible
     */
    public abstract void onStart();
    public abstract void onSend(String message);
    public abstract String onConsume();

    public void onStop() {
        try {
            println("Connection is closing...., uuid = " + this.uuid);
            // we might need to submit a logout demand before closing a connection.
            this.inputStream.close();
            this.outputStream.close();
            this.socket.close();
        } catch (Exception e) {
            println("Failure to close connection uuid = " + this.uuid + ", exception = " + e.getMessage());
        }
    }
    public abstract String getUUID();
    public abstract void println(String message);
}
