import models.Vehicle;
import models.User;
import services.ParkingService;
import services.BillingService;
import utils.DBConnection;
import utils.DBInitializer;
import dao.UserDAO;

public class App {
    public static void main(String[] args) {
        System.out.println("Welcome to AutoPark - Parking Management System");

        // Initialize database connection
        if (DBConnection.getConnection() != null) {
            System.out.println(" Database Connected Successfully!");
        } else {
            System.out.println("Database Connection Failed!");
            return;
        }

        // Initialize slots in database
        DBInitializer.initializeSlots(10);
        System.out.println("Parking slots initialized");

        // Create services
        ParkingService parkingService = new ParkingService();
        BillingService billingService = new BillingService(50.0); // ₹50 per hour

        // Display initial parking status
        parkingService.displayParkingStatus();

        // Test: Park a vehicle
        System.out.println("\n--- Testing Vehicle Entry ---");
        Vehicle vehicle1 = new Vehicle("TN-22-AB-1234", "Car");
        vehicle1.setUserId(1); // Assuming user ID 1 exists

        Integer allocatedSlot = parkingService.parkVehicle(vehicle1);
        if (allocatedSlot != null) {
            System.out.println("Vehicle allocated to slot: " + allocatedSlot);
        } else {
            System.out.println("Failed to park vehicle");
        }

        // Display updated status
        parkingService.displayParkingStatus();

        // Test: Park another vehicle
        System.out.println("\n--- Testing Second Vehicle Entry ---");
        Vehicle vehicle2 = new Vehicle("TN-22-CD-5678", "Bike");
        vehicle2.setUserId(1);

        Integer allocatedSlot2 = parkingService.parkVehicle(vehicle2);
        if (allocatedSlot2 != null) {
            System.out.println("Vehicle allocated to slot: " + allocatedSlot2);
        }

        // Display final status
        parkingService.displayParkingStatus();

        // Test: Vehicle exit
        System.out.println("\n--- Testing Vehicle Exit ---");
        boolean exitSuccess = parkingService.removeVehicle("TN-22-AB-1234");
        if (exitSuccess) {
            System.out.println("Vehicle exited successfully");
            // Generate bill
            // billingService.generateBill(vehicle1); // We'll fix this later
        }

        // Final status
        parkingService.displayParkingStatus();

        System.out.println("\n--- AutoPark System Ready ---");

        // Test user authentication
        testUserAuthentication();
    }

    private static void testUserAuthentication() {
        System.out.println("\n--- Testing User Authentication ---");

        // Check if admin user exists
        User admin = UserDAO.getUserByUsername("admin");
        if (admin != null) {
            System.out.println("Admin user found: " + admin.getFullName());
        } else {
            System.out.println("Admin user not found");
        }

        // Test user validation
        boolean isValid = UserDAO.validateUser("admin", "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi");
        System.out.println("User validation test: " + (isValid ? "PASS" : "FAIL"));
    }
}