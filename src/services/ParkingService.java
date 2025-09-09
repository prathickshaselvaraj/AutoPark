package services;

import models.ParkingSlot;
import models.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class ParkingService {
    private List<ParkingSlot> slots;

    public ParkingService(int totalSlots) {
        slots = new ArrayList<>();
        for (int i = 1; i <= totalSlots; i++) {
            slots.add(new ParkingSlot(i));
        }
    }

    // Allocate slot
    public ParkingSlot parkVehicle(Vehicle vehicle) {
        for (ParkingSlot slot : slots) {
            if (!slot.isOccupied()) {
                slot.parkVehicle(vehicle);
                System.out.println(" Vehicle " + vehicle.getLicensePlate() +
                        " parked at Slot " + slot.getSlotId());
                return slot;
            }
        }
        System.out.println(" No available slots!");
        return null;
    }

    // Release slot (vehicle exits)
    public Vehicle removeVehicle(String licensePlate) {
        for (ParkingSlot slot : slots) {
            if (slot.isOccupied() &&
                    slot.getParkedVehicle().getLicensePlate().equals(licensePlate)) {

                Vehicle vehicle = slot.getParkedVehicle();
                slot.removeVehicle();
                System.out.println(" Vehicle " + licensePlate +
                        " removed from Slot " + slot.getSlotId());
                return vehicle;
            }
        }
        System.out.println(" Vehicle not found!");
        return null;
    }

    // Show slots
    // Show all slots with a summary
    public void displaySlots() {
        int occupiedCount = 0;
        int freeCount = 0;

        System.out.println("\n--- Parking Slots Status ---");
        for (ParkingSlot slot : slots) {
            String status;
            if (slot.isOccupied()) {
                status = "Occupied (" + slot.getParkedVehicle().getLicensePlate() + ")";
                occupiedCount++;
            } else {
                status = "Free";
                freeCount++;
            }
            System.out.println("Slot " + slot.getSlotId() + ": " + status);
        }

        System.out.println("\n--- Summary ---");
        System.out.println("Total Slots: " + slots.size());
        System.out.println("Occupied: " + occupiedCount);
        System.out.println("Free: " + freeCount);
    }

    // Allocate the nearest available slot automatically
    public ParkingSlot allocateNearestSlot(Vehicle vehicle) {
        for (ParkingSlot slot : slots) {
            if (!slot.isOccupied()) {
                slot.parkVehicle(vehicle);
                System.out.println(" Vehicle " + vehicle.getLicensePlate() +
                        " allocated to Nearest Slot " + slot.getSlotId());
                return slot;
            }
        }
        System.out.println(" No available slots to allocate!");
        return null;
    }

}
