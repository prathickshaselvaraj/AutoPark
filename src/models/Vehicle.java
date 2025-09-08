package models;

import java.time.LocalDateTime;

public class Vehicle {
    private String licensePlate;
    private String type; // Car, Bike, etc.
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Vehicle(String licensePlate, String type) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.entryTime = LocalDateTime.now();
    }

    // Getters & Setters
    public String getLicensePlate() { return licensePlate; }
    public String getType() { return type; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
}
