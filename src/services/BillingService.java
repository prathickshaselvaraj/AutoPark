package services;

import models.Transaction;
import models.Vehicle;

import java.time.LocalDateTime;

public class BillingService {
    private double ratePerHour;

    public BillingService(double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    // Generate bill when vehicle exits
    public Transaction generateBill(Vehicle vehicle) {
        vehicle.setExitTime(LocalDateTime.now()); // mark exit
        Transaction transaction = new Transaction(vehicle, ratePerHour);
        System.out.println("💰 Bill for " + vehicle.getLicensePlate() +
                ": " + transaction.getAmount() + " INR");
        return transaction;
    }
}
