package models;

import java.time.Duration;
import java.time.LocalDateTime;

public class Transaction {
    private Vehicle vehicle;
    private double amount;
    private LocalDateTime transactionTime;

    public Transaction(Vehicle vehicle, double ratePerHour) {
        this.vehicle = vehicle;
        this.transactionTime = LocalDateTime.now();

        if (vehicle.getExitTime() != null) {
            long hours = Duration.between(vehicle.getEntryTime(), vehicle.getExitTime()).toHours();
            this.amount = hours * ratePerHour;
        } else {
            this.amount = 0.0;
        }
    }

    // Getters
    public Vehicle getVehicle() { return vehicle; }
    public double getAmount() { return amount; }
    public LocalDateTime getTransactionTime() { return transactionTime; }
}
