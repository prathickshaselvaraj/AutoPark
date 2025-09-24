package dao;

import models.Vehicle;
import utils.DBConnection;
import java.sql.*;

public class VehicleDAO {

    public static boolean addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (license_plate, type, entry_time, user_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, vehicle.getLicensePlate());
            ps.setString(2, vehicle.getType());
            ps.setTimestamp(3, Timestamp.valueOf(vehicle.getEntryTime()));
            ps.setLong(4, vehicle.getUserId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    vehicle.setId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error adding vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static Vehicle getVehicleByLicensePlate(String licensePlate) {
        String sql = "SELECT * FROM vehicles WHERE license_plate = ? AND exit_time IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, licensePlate);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Vehicle vehicle = new Vehicle(
                        rs.getString("license_plate"),
                        rs.getString("type")
                );
                vehicle.setId(rs.getInt("id"));
                vehicle.setEntryTime(rs.getTimestamp("entry_time").toLocalDateTime());

                Timestamp exitTime = rs.getTimestamp("exit_time");
                if (exitTime != null) {
                    vehicle.setExitTime(exitTime.toLocalDateTime());
                }

                vehicle.setUserId(rs.getLong("user_id"));
                return vehicle;
            }

        } catch (SQLException e) {
            System.err.println("Error getting vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateVehicleExit(String licensePlate) {
        String sql = "UPDATE vehicles SET exit_time = CURRENT_TIMESTAMP WHERE license_plate = ? AND exit_time IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, licensePlate);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating vehicle exit: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}