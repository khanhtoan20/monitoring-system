package utils;

import com.mashape.unirest.http.Unirest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;
import org.json.JSONObject;

public class Helper {

    public static String getRandomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void registerServerConfig() throws Exception {
        Socket socket = new Socket("thongtindaotao.sgu.edu.vn", 80);
        String publicIP = socket.getLocalAddress().toString().substring(1);
        String status = Unirest.put("https://api.jsonbin.io/v3/b/636f162a0e6a79321e472d99/")
            .header("X-Master-Key", Environment.API_KEY)
            .header("Content-Type","application/json")
            .body(new JSONObject()
//                .put("serverAddress", InetAddress.getLocalHost().getHostAddress())
                .put("serverAddress", publicIP)
                .put("serverPort", Environment.DEFAULT_SERVER_PORT))
            .asString()
            .getStatusText();
        switch (status) {
            case "OK": {
                console.info("Published the server IP {" + publicIP + "} successfully");
                break;
            }
            default: {
                console.error("Failure to publish the server IP");
            }
        }
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("thongtindaotao.sgu.edu.vn", 80);
        String localIP = socket.getLocalAddress().toString().substring(1);
        console.info(localIP);
    }
}
