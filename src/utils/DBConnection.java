package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/parking_lot",
                    "root",
                    "<prathi#selvaraj>1009"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
