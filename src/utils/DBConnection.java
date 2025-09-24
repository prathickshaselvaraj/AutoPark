package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Properties props = new Properties();

            // Load properties using absolute path
            FileInputStream input = new FileInputStream("E:/Projects/AutoPark/config/db.properties");
            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            // Replace ${DB_PASSWORD} with actual environment variable
            if (password != null && password.contains("${DB_PASSWORD}")) {
                String envPassword = System.getenv("DB_PASSWORD");
                if (envPassword != null) {
                    password = envPassword;
                } else {
                    logger.warning("Environment variable DB_PASSWORD is not set! Using literal value from properties.");
                }
            }

            // Append SSL/Key retrieval options for MySQL 8+
            if (!url.contains("?")) {
                url += "?useSSL=false&allowPublicKeyRetrieval=true";
            } else {
                url += "&useSSL=false&allowPublicKeyRetrieval=true";
            }

            conn = DriverManager.getConnection(url, user, password);
            logger.info("Database connection established successfully.");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to connect to database", e);
        }

        return conn;
    }
}
