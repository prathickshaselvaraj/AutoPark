import models.Vehicle;
import utils.DBConnection;
import utils.DBInitializer;

public class App {
    public static void main(String[] args) {
        System.out.println("Welcome to AutoPark 🚗");
        DBInitializer.initializeSlots(10);
        if (DBConnection.getConnection() != null) {
            System.out.println("✅ Database Connected!");
        } else {
            System.out.println("❌ Database Connection Failed!");
        }
    }
}
