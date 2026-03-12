package dao;
import models.AdminUser;
import utils.DBConnection;
import java.sql.*;

public class AdminUserDAO {
    public static AdminUser authenticate(String username, String passwordHash) {
        String sql = "SELECT * FROM admin_users WHERE username = ? AND password_hash = ? AND active = TRUE";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username); ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                AdminUser u = new AdminUser();
                u.setId(rs.getInt("id")); u.setUsername(rs.getString("username"));
                u.setPasswordHash(rs.getString("password_hash")); u.setFullName(rs.getString("full_name"));
                u.setRole(AdminUser.UserRole.valueOf(rs.getString("role")));
                u.setActive(rs.getBoolean("active"));
                u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return u;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    public static void logActivity(int adminId, String action) {
        String sql = "INSERT INTO audit_log (admin_id, action_description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId); ps.setString(2, action); ps.executeUpdate();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }
}