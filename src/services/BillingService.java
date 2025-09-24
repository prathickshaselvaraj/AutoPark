package services;

import models.Vehicle;
import dao.VehicleDAO;

import java.time.Duration;
import java.time.LocalDateTime;

public class BillingService {
    private double ratePerHour;

    public BillingService(double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public double calculateParkingFee(String licensePlate) {
        Vehicle vehicle = VehicleDAO.getVehicleByLicensePlate(licensePlate);

        if (vehicle != null && vehicle.getExitTime() != null) {
            long hours = Duration.between(vehicle.getEntryTime(), vehicle.getExitTime()).toHours();
            // Minimum charge for 1 hour
            hours = Math.max(1, hours);
            return hours * ratePerHour;
        }
        return 0.0;
    }

    public void generateBill(String licensePlate) {
        double amount = calculateParkingFee(licensePlate);

        if (amount > 0) {
            System.out.println("Bill for " + licensePlate + ": ₹" + amount);
            System.out.println("Payment method: CASH");
            System.out.println("Marking as paid...");

            // In a real system, you'd save this to transactions table
            // TransactionDAO.createTransaction(vehicleId, amount, "CASH");

        } else {
            System.out.println(" No parking record found or vehicle still parked");
        }
    }
}