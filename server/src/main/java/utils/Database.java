package utils;

import java.sql.*;

import static utils.Environment.*;

public class Database {
    private static Connection conn = getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);;
    private static Statement stmt;

    static {
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * count
     * @param tableName
     */
    public static Integer count(String tableName, String where) {
        try {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `"+ tableName +"` WHERE ");
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            println(e.getMessage());
            return null;
        }
    }

    /**
     * create connection
     *
     * @author viettuts.vn
     * @param dbURL: database's url
     * @param userName: username is used to login
     * @param password: password is used to login
     * @return connection
     */
    public static Connection getConnection(String dbURL, String userName, String password) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, userName, password);
            System.out.println("connect successfully!");
        } catch (Exception ex) {
            System.out.println("connect failure!");
            ex.printStackTrace();
            return null;
        }
        return conn;
    }

    public static void println(String message) {
        System.out.println("[DATABASE] " + message);
    }
}
