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
        try {
            Properties props = new Properties();

            // Load properties using absolute path
            FileInputStream input = new FileInputStream("E:/Projects/AutoPark/config/db.properties");
            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            return DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to connect to database", e);
            return null;
        }
    }
}
