package services;

import models.Vehicle;
import dao.ParkingSlotDAO;
import dao.VehicleDAO;

public class ParkingService {

    public Integer parkVehicle(Vehicle vehicle) {
        // First, add vehicle to database
        if (VehicleDAO.addVehicle(vehicle)) {
            // Then find available slot
            Integer availableSlot = ParkingSlotDAO.findAvailableSlot();

            if (availableSlot != null) {
                // Allocate the slot to this vehicle
                if (ParkingSlotDAO.allocateSlot(availableSlot, vehicle.getId())) {
                    System.out.println(" Vehicle " + vehicle.getLicensePlate() +
                            " parked at Slot " + availableSlot);
                    return availableSlot;
                }
            } else {
                System.out.println(" No available slots!");
            }
        }
        return null;
    }

    public boolean removeVehicle(String licensePlate) {
        Vehicle vehicle = VehicleDAO.getVehicleByLicensePlate(licensePlate);

        if (vehicle != null) {
            // In real scenario, we would track which slot the vehicle is in
            // For now, we'll implement a method to find slot by vehicle
            if (VehicleDAO.updateVehicleExit(licensePlate)) {
                System.out.println(" Vehicle " + licensePlate + " has exited.");
                return true;
            }
        } else {
            System.out.println(" Vehicle not found or already exited!");
        }
        return false;
    }

    public void displayParkingStatus() {
        int totalSlots = 10; // This should come from database
        int occupied = ParkingSlotDAO.getOccupiedSlotsCount();
        int available = totalSlots - occupied;

        System.out.println("\n--- Parking Status ---");
        System.out.println("Total Slots: " + totalSlots);
        System.out.println("Occupied: " + occupied);
        System.out.println("Available: " + available);
        System.out.println("Utilization: " + (occupied * 100 / totalSlots) + "%");
    }
}