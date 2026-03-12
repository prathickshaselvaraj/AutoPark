package dao;

import models.Booking;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public static boolean createBooking(Booking b) {
        String sql = "INSERT INTO bookings (license_plate, vehicle_type, user_id, scheduled_entry, status) VALUES (?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getLicensePlate());
            ps.setString(2, b.getVehicleType());
            ps.setObject(3, b.getUserId() == 0 ? null : b.getUserId(), Types.BIGINT);
            ps.setObject(4, b.getScheduledEntry());
            ps.setString(5, "PENDING");
            int rows = ps.executeUpdate();
            if (rows > 0) { ResultSet k = ps.getGeneratedKeys(); if (k.next()) b.setId(k.getInt(1)); }
            return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY booking_time DESC";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Booking> getBookingsByUserId(long userId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_time DESC";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static boolean updateStatus(int id, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status); ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private static Booking map(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setLicensePlate(rs.getString("license_plate"));
        b.setVehicleType(rs.getString("vehicle_type"));
        b.setUserId(rs.getLong("user_id"));
        b.setStatus(rs.getString("status"));
        Timestamp bt = rs.getTimestamp("booking_time");
        if (bt != null) b.setBookingTime(bt.toLocalDateTime());
        Timestamp se = rs.getTimestamp("scheduled_entry");
        if (se != null) b.setScheduledEntry(se.toLocalDateTime());
        return b;
    }
}