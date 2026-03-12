package dao;

import models.Vehicle;
import utils.DBConnection;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static boolean addVehicle(Vehicle v) {
        String sql = "INSERT INTO vehicles (license_plate, vehicle_type, entry_time, user_id) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getLicensePlate());
            ps.setString(2, v.getType());
            ps.setObject(3, v.getEntryTime());
            ps.setObject(4, v.getUserId() == 0 ? null : v.getUserId(), Types.INTEGER);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) v.setId(keys.getInt(1));
                return true;
            }
            return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static Vehicle getVehicleByLicensePlate(String plate) {
        String sql = "SELECT * FROM vehicles WHERE license_plate = ? ORDER BY entry_time DESC LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, plate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static boolean updateVehicleExit(String plate) {
        String sql = "UPDATE vehicles SET exit_time = ? WHERE license_plate = ? AND exit_time IS NULL";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, LocalDateTime.now()); ps.setString(2, plate);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Returns rows for the full vehicle table: Plate, Type, Status, Entry, Exit, Duration, Bill */
    public static List<Object[]> getAllVehiclesForTable() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT license_plate, vehicle_type, entry_time, exit_time FROM vehicles ORDER BY entry_time DESC";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String plate = rs.getString("license_plate");
                String type  = null;
                try { type = rs.getString("vehicle_type"); } catch (SQLException ignored) {}
                if (type == null) type = "CAR";
                Timestamp entryTs = rs.getTimestamp("entry_time");
                Timestamp exitTs  = rs.getTimestamp("exit_time");
                LocalDateTime entry = entryTs != null ? entryTs.toLocalDateTime() : null;
                LocalDateTime exit  = exitTs  != null ? exitTs.toLocalDateTime()  : null;
                boolean parked = exit == null;
                String status   = parked ? "PARKED" : "EXITED";
                String entryStr = entry != null ? entry.format(FMT) : "-";
                String exitStr  = exit  != null ? exit.format(FMT)  : "Still Parked";
                long mins = 0;
                if (entry != null) mins = Duration.between(entry, parked ? LocalDateTime.now() : exit).toMinutes();
                long bil  = Math.max(30, mins);
                double fee = (bil / 60.0) * 50.0;
                String dur  = (mins / 60) + "h " + (mins % 60) + "m";
                String bill = String.format("Rs %.2f%s", fee, parked ? " (est)" : "");
                rows.add(new Object[]{plate, type, status, entryStr, exitStr, dur, bill});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    private static Vehicle map(ResultSet rs) throws SQLException {
        String type = null;
        try { type = rs.getString("vehicle_type"); } catch (SQLException ignored) {}
        if (type == null) try { type = rs.getString("type"); } catch (SQLException ignored) {}
        if (type == null) type = "CAR";
        Vehicle v = new Vehicle(rs.getString("license_plate"), type);
        v.setId(rs.getInt("id")); v.setUserId(rs.getLong("user_id"));
        Timestamp entry = rs.getTimestamp("entry_time"); if (entry != null) v.setEntryTime(entry.toLocalDateTime());
        Timestamp exit  = rs.getTimestamp("exit_time");  if (exit  != null) v.setExitTime(exit.toLocalDateTime());
        return v;
    }
}