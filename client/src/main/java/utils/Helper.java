package utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class Helper {
    private static final String STACK_TRACE_FORMAT = "[%s][%s]:";

    public static String getRandomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    public static String getStackTrace(Class cl) {
        return String.format(STACK_TRACE_FORMAT, cl.getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    /**
     * Ref: https://www.geeksforgeeks.org/md5-hash-in-java/
     */
    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
