package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/parking_lot", // ✅ match your DB name
                    "root",                                   // ✅ MySQL username
                    "<prathi#selvaraj>1009"                    // ✅ replace with real password, or "" if none
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
