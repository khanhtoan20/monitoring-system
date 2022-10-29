package utils;

public class console {
    private static String RESET = "\033[0m";
    private static String INFO = "\033[1;92m";
    private static String ERROR = "\033[1;91m";
    private static String LOG = "\033[1;37m";
    private static String WARNING = "\033[1;93m";

    public static void info(String message) {
        System.out.println(INFO + "[INFO] " + message + RESET);
    }
    public static void error(String message) {
        System.out.println(ERROR + "[ERROR] " + message + RESET);
    }
    public static void warn(String message) {
        System.out.println(WARNING + "[WARN] " + message + RESET);
    }
    public static void log(String message) {
        System.out.println("[LOG] " + message);
    }
}
