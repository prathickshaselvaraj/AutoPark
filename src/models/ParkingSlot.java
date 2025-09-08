package models;

public class ParkingSlot {
    private int slotId;
    private boolean isOccupied;
    private Vehicle parkedVehicle;

    public ParkingSlot(int slotId) {
        this.slotId = slotId;
        this.isOccupied = false;
    }

    // Getters & Setters
    public int getSlotId() { return slotId; }
    public boolean isOccupied() { return isOccupied; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    public void parkVehicle(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
        this.isOccupied = true;
    }

    public void removeVehicle() {
        this.parkedVehicle = null;
        this.isOccupied = false;
    }
}
