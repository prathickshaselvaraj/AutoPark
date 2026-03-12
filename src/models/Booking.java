package models;

import java.time.LocalDateTime;

public class Booking {
    private int           id;
    private String        licensePlate;
    private String        vehicleType;
    private long          userId;
    private LocalDateTime bookingTime;
    private LocalDateTime scheduledEntry;
    private String        status;   // PENDING | CONFIRMED | CANCELLED | ARRIVED
    private String        notes;

    public Booking() {}

    public Booking(String licensePlate, String vehicleType, long userId, LocalDateTime scheduledEntry) {
        this.licensePlate   = licensePlate;
        this.vehicleType    = vehicleType;
        this.userId         = userId;
        this.scheduledEntry = scheduledEntry;
        this.bookingTime    = LocalDateTime.now();
        this.status         = "PENDING";
    }

    public int           getId()             { return id; }
    public void          setId(int id)       { this.id = id; }
    public String        getLicensePlate()   { return licensePlate; }
    public void          setLicensePlate(String v) { this.licensePlate = v; }
    public String        getVehicleType()    { return vehicleType; }
    public void          setVehicleType(String v)  { this.vehicleType = v; }
    public long          getUserId()         { return userId; }
    public void          setUserId(long v)   { this.userId = v; }
    public LocalDateTime getBookingTime()    { return bookingTime; }
    public void          setBookingTime(LocalDateTime v) { this.bookingTime = v; }
    public LocalDateTime getScheduledEntry() { return scheduledEntry; }
    public void          setScheduledEntry(LocalDateTime v) { this.scheduledEntry = v; }
    public String        getStatus()         { return status; }
    public void          setStatus(String v) { this.status = v; }
    public String        getNotes()          { return notes; }
    public void          setNotes(String v)  { this.notes = v; }
}