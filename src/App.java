import models.Vehicle;
import services.ParkingService;
import utils.DBConnection;
import utils.DBInitializer;

public class App {
    public static void main(String[] args) {
        System.out.println("Welcome to AutoPark ");

        // Initialize slots in DB
        DBInitializer.initializeSlots(10);

        // Check DB connection
        if (DBConnection.getConnection() != null) {
            System.out.println(" Database Connected!");
        } else {
            System.out.println(" Database Connection Failed!");
        }

        // Use ParkingService
        ParkingService service = new ParkingService(10);

        // Display current slot status
        service.displaySlots();

        //  New feature: allocate nearest available slot to a vehicle
        Vehicle v1 = new Vehicle("TN-22-AB-1234", "Car");
        service.allocateNearestSlot(v1);

        // Display updated slot status
        service.displaySlots();

        // Allocate another vehicle
        Vehicle v2 = new Vehicle("TN-22-CD-5678", "Car");
        service.allocateNearestSlot(v2);

        // Display final slot status
        service.displaySlots();
    }
}
