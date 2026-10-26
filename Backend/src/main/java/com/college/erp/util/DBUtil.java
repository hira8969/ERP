package com.college.erp.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Standard JDBC Connection Utility for MySQL Database.
 * Reads connection properties from db.properties and manages database connections.
 * Follows core Java principles suitable for undergraduate BCA syllabus.
 */
public final class DBUtil {

    private static String url;
    private static String username;
    private static String password;
    private static String driver;

    static {
        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            if (input != null) {
                props.load(input);
                driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/college_erp?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                username = props.getProperty("db.username", "root");
                password = props.getProperty("db.password", "root");
            } else {
                driver = "com.mysql.cj.jdbc.Driver";
                url = "jdbc:mysql://localhost:3306/college_erp?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                username = "root";
                password = "";
            }

            // Ensure allowPublicKeyRetrieval=true for MySQL 8 caching_sha2_password authentication
            if (url != null && !url.contains("allowPublicKeyRetrieval")) {
                url += (url.contains("?") ? "&" : "?") + "allowPublicKeyRetrieval=true";
            }

            Class.forName(driver);
        } catch (Exception e) {
            System.err.println("DBUtil initialization error: " + e.getMessage());
        }
    }

    private DBUtil() {
    }

    /**
     * Returns a new JDBC Connection to MySQL database.
     * Callers should use try-with-resources so connections are automatically closed.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
