package dao;

import utils.DBConnection;
import java.sql.*;

public class ParkingSlotDAO {

    public static boolean allocateSlot(int slotNumber, int vehicleId) {
        String sql = "UPDATE slots SET is_occupied = TRUE, vehicle_id = ? WHERE slot_number = ? AND is_occupied = FALSE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            ps.setInt(2, slotNumber);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean freeSlot(int slotNumber) {
        String sql = "UPDATE slots SET is_occupied = FALSE, vehicle_id = NULL WHERE slot_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, slotNumber);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Integer findAvailableSlot() {
        String sql = "SELECT slot_number FROM slots WHERE is_occupied = FALSE ORDER BY slot_number LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("slot_number");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns all slots with their current occupancy status and
     * the license plate of the parked vehicle (if any).
     * Used by the admin parking-lot grid view.
     */
    public static java.util.List<models.SlotInfo> getAllSlotsInfo() {
        String sql =
                "SELECT s.slot_number, s.is_occupied, v.license_plate " +
                        "FROM slots s " +
                        "LEFT JOIN vehicles v ON s.vehicle_id = v.id AND v.exit_time IS NULL " +
                        "ORDER BY s.slot_number";

        java.util.List<models.SlotInfo> list = new java.util.ArrayList<>();

        try (java.sql.Connection conn = utils.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new models.SlotInfo(
                        rs.getInt("slot_number"),
                        rs.getBoolean("is_occupied"),
                        rs.getString("license_plate")   // null if free
                ));
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error fetching all slots: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns total number of slots in DB.
     */
    public static int getTotalSlotsCount() {
        String sql = "SELECT COUNT(*) FROM slots";
        try (java.sql.Connection conn = utils.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (java.sql.SQLException e) {
            System.err.println("Error counting slots: " + e.getMessage());
        }
        return 0;
    }

    public static int getOccupiedSlotsCount() {
        String sql = "SELECT COUNT(*) as count FROM slots WHERE is_occupied = TRUE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}