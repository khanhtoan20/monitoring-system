package utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class Helper {
    private static final String EMPTY_STRING = "";
    private static final String DEFAULT_CPU_PERCENTAGE = "0";
    private static final String COMMAND_PROMPT_REGEX = "[a-zA-Z\\s]";

    public static String CommandPrompt(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            java.util.Scanner s = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A");
            return s.hasNext() ? s.next().replaceAll(COMMAND_PROMPT_REGEX, EMPTY_STRING) : DEFAULT_CPU_PERCENTAGE;
        } catch (IOException e) {
            return DEFAULT_CPU_PERCENTAGE;
        }
    }

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
}
