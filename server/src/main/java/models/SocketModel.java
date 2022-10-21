package models;

import utils.Helper;
import utils.console;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public abstract class SocketModel implements Runnable {
    protected String uuid;
    protected Socket socket;
    protected BufferedReader inputStream;
    protected DataOutputStream outputStream;

    public void initInputStream() {
        try {
            this.inputStream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (Exception e) {
            console.error(this.getStackTrace(e));
        }
    }

    public void initOutputStream() {
        try {
            this.outputStream = new DataOutputStream(this.socket.getOutputStream());
        } catch (Exception e) {
            console.error(this.getStackTrace(e));
        }
    }

    @Override
    public void run() {
        onStart();
    }

    public abstract void onStart();

    public abstract void onStop();

    public abstract void onSend(String message);

    public abstract void onHandle(String message);

    public abstract String onConsume();

    public String getUUID() {
        return this.uuid;
    }

    protected String getStackTrace(Exception exception) {
        return Helper.getStackTrace(this.getClass()) + exception.getMessage();
    }
}
