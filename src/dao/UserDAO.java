package dao;
import models.User;
import utils.DBConnection;
import java.sql.*;

public class UserDAO {
    public static boolean createUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, full_name, phone, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername()); ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash()); ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhone()); ps.setString(6, user.getRole().name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    public static User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username); ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getLong("id")); u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email")); u.setPasswordHash(rs.getString("password_hash"));
                u.setFullName(rs.getString("full_name")); u.setPhone(rs.getString("phone"));
                u.setRole(User.UserRole.valueOf(rs.getString("role")));
                u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                u.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                u.setActive(rs.getBoolean("active"));
                return u;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    public static boolean validateUser(String username, String passwordHash) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ? AND active = TRUE";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username); ps.setString(2, passwordHash);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}