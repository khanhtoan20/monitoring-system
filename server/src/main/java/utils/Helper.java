package utils;

import java.util.UUID;

public class Helper {
    public static String getRandomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
