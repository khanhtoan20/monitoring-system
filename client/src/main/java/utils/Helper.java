package utils;

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
}
