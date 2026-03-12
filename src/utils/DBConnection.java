package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());

    static {
        try {
            // Explicitly load MySQL driver — required when using plain javac + java with -cp
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found! Make sure mysql-connector-j.jar is on the classpath.", e);
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Properties props = new Properties();

            // Load properties file — use relative path so project is portable
            String configPath = "config/db.properties";
            try (FileInputStream input = new FileInputStream(configPath)) {
                props.load(input);
            }

            String url      = props.getProperty("db.url");
            String user     = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            // Resolve environment variable placeholder if present
            if (password != null && password.contains("${DB_PASSWORD}")) {
                String envPassword = System.getenv("DB_PASSWORD");
                if (envPassword != null) {
                    password = envPassword;
                } else {
                    logger.warning("Environment variable DB_PASSWORD is not set! Using literal value from properties.");
                }
            }

            // Append required MySQL 8+ connection options if not already present
            if (url != null && !url.contains("useSSL")) {
                url += (url.contains("?") ? "&" : "?")
                        + "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            }

            conn = DriverManager.getConnection(url, user, password);
            logger.info("Database connection established successfully.");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to connect to database", e);
        }

        return conn;
    }
}