import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDBConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/parking_lot";
        String user = "root";
        String password = "<prathi#selvaraj>1009";

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connection successful!");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Connection failed!");
            e.printStackTrace();
        }
    }
}
