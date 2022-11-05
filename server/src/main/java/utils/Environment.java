package utils;

public class Environment {
    /**
     * ServerSocket config
     */
    public static final int DEFAULT_SERVER_PORT = 9999;
    public static final String DEFAULT_SERVER_HOST = "localhost";

    /**
     * Database config
     */
    public static final String DB_URL = "jdbc:mysql://localhost:3306/monitoring-system";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "";

    /**
     * Utils config
     */
    public static final String MESSAGE = "message";
    public static final String SOCKET_MODEL_ID = "uuid";
    public static final String NOT_FOUND_CONTROLLER = "NOT_FOUND_CONTROLLER";

    /**
     * Message Model config
     */
    public static final String FORM = "form";
    public static final String TO = "to";

    /**
     * SystemInfo Model config
     */
    public static final String OS = "os";
    public static final String IP = "ip";
    public static final String RAM = "ram";
    public static final String CPU = "cpu";
    public static final String DISK = "disk";
    public static final String MAC_ADDRESS = "MAC_address";
    public static final String HOSTNAME = "hostName";
}
