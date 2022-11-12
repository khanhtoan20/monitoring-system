package utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.net.InetAddress;
import org.json.JSONObject;

public class Helper {
  public static void getConfigFromNetwork() throws UnirestException {
    HttpResponse<JsonNode> response = Unirest.get("https://api.jsonbin.io/v3/b/636f162a0e6a79321e472d99/")
        .header("X-Master-Key", Environment.API_KEY)
        .header("X-Bin-Meta","false")
        .header("Content-Type","application/json")
        .asJson();

    switch (response.getStatusText()) {
      case "OK": {
        console.info("Get the server config successfully "+ response.getBody().toString());
        Environment.DEFAULT_SERVER_HOST = response.getBody().getObject().getString("serverAddress");
        Environment.DEFAULT_SERVER_PORT = response.getBody().getObject().getInt("serverPort");
        break;
      }
      default: {
        console.error("Failure to get the server config, prepare to scan all ip in local with PORT" + Environment.DEFAULT_SERVER_PORT);
      }
    }
  }

  public static void main(String[] args) throws UnirestException {
    getConfigFromNetwork();
  }
}
