import org.json.JSONArray;
import org.json.JSONObject;
import utils.Executable;
import utils.Handler;

import java.io.*;
import java.net.Socket;

import static utils.Environment.DEFAULT_SERVER_HOST;
import static utils.Environment.DEFAULT_SERVER_PORT;

public class Client {
    private Socket connection;
    private BufferedReader scanner;
    private BufferedReader inputStream;
    private DataOutputStream outputStream;
    private Handler handler;

    public Client() {
        this.handler = new Handler();
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

    public void execute() {
        try {
            connection = new Socket(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
            System.out.println("LOGGED IN");
//            String output;
            String input;

            this.getInputStream();
            this.getOutputStream();

            while (!(this.getScanner().readLine()).equals("x")) {
                input = inputStream.readLine();
                JSONObject temp = new JSONObject(input);
                Executable task = handler.get(temp.get("command") + "");
                outputStream.writeBytes(task.execute("")+"\n");
            }
        } catch (Exception ex) {
            System.out.println("Server not found: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client c = new Client();
        c.execute();
    }
}
