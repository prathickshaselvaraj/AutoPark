package services;

import models.Vehicle;
import dao.ParkingSlotDAO;
import dao.VehicleDAO;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
            // First free the slot occupied by this vehicle
            if (freeSlotByVehicleId(vehicle.getId())) {
                // Then update vehicle exit time
                if (VehicleDAO.updateVehicleExit(licensePlate)) {
                    System.out.println(" Vehicle " + licensePlate + " has exited and slot freed.");
                    return true;
                } else {
                    System.out.println(" Error updating vehicle exit time!");
                }
            } else {
                System.out.println(" Error freeing the parking slot!");
            }
        } else {
            System.out.println(" Vehicle not found or already exited!");
        }
        return false;
    }

    // New helper method to free slot by vehicle ID
    private boolean freeSlotByVehicleId(int vehicleId) {
        String sql = "UPDATE slots SET is_occupied = FALSE, vehicle_id = NULL WHERE vehicle_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Error freeing slot for vehicle ID " + vehicleId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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