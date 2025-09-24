package models;

import java.time.LocalDateTime;

public class Vehicle {
    private int id;
    private String licensePlate;
    private String type;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private long userId;

    public Vehicle(String licensePlate, String type) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.entryTime = LocalDateTime.now();
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getLicensePlate() { return licensePlate; }
    public String getType() { return type; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; } // <-- add this

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
}