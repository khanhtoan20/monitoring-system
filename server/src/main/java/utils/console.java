package utils;

public class console {
    private static final String RESET = "\033[0m";
    private static final String INFO = "\033[1;92m";
    private static final String ERROR = "\033[1;91m";
    private static final String LOG = "\033[1;97m";
    private static final String WARNING = "\033[1;93m";

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
        System.out.println(LOG + "[LOG] " + message + RESET);
    }
}
