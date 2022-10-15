import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class C1 {
    private Socket connection;
    private BufferedReader scanner;
    private BufferedReader inputStream;
    private DataOutputStream outputStream;

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
            connection = new Socket("localhost", 9999);
            System.out.println("ADMIN LOGGED IN");
            String output;
            String input;

            this.getInputStream();
            this.getOutputStream();

            while (!(output = this.getScanner().readLine()).equals("x")) {
                JSONObject temp = new JSONObject();
                temp.put("command", output);
                temp.put("username", "admin");
                temp.put("password", "1234567");
                outputStream.writeBytes(temp + "\n");
                input = inputStream.readLine();
                System.out.println("RESPONSE: "+ input);
            }

        } catch (Exception ex) {
            System.out.println("Server not found: " + ex.getMessage());
            ex.printStackTrace();

        }
    }

    public static void main(String[] args) {
        C1 c = new C1();
        c.execute();
    }
}
