package models;

/**
 * Lightweight DTO representing one parking slot's current state.
 * Used by the admin parking-lot grid view.
 */
public class SlotInfo {
    private final int     slotNumber;
    private final boolean occupied;
    private final String  licensePlate; // null if free

    public SlotInfo(int slotNumber, boolean occupied, String licensePlate) {
        this.slotNumber   = slotNumber;
        this.occupied     = occupied;
        this.licensePlate = licensePlate;
    }

    public int     getSlotNumber()   { return slotNumber; }
    public boolean isOccupied()      { return occupied; }
    public String  getLicensePlate() { return licensePlate; }
}