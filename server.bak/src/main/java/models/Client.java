package models;

import java.io.*;
import java.net.Socket;

public class Client {
    private static Socket connection;
    private static BufferedReader scanner;
    private static ObjectInputStream inputStream;
    private static ObjectOutputStream outputStream;

    public BufferedReader getScanner() {
        return scanner == null ? scanner = new BufferedReader(new InputStreamReader(System.in)) : scanner;
    }

    public ObjectInputStream getInputStream() throws Exception {
        return inputStream == null ? inputStream = new ObjectInputStream(connection.getInputStream()) : inputStream;
    }

    public ObjectOutputStream getOutputStream() throws Exception {
        return outputStream == null ? outputStream = new ObjectOutputStream(connection.getOutputStream()) : outputStream;
    }

    public void execute() {
        try {
            connection = new Socket("localhost", 9999);
            System.out.println("LOGGED IN");
            String output;
            String input;

            this.getInputStream();
            this.getOutputStream();

            while (!(output = this.getScanner().readLine()).equals("x")) {

                outputStream.writeObject(new Test("admin","123456"));
                input = (String) inputStream.readObject();
                System.out.println("RESPONSE: "+ input);
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
