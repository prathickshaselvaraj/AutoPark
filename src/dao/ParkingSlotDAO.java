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