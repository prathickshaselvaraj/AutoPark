package models;

import java.time.LocalDateTime;

public class AdminUser {
    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private UserRole role;
    private boolean active;
    private LocalDateTime createdAt;

    public enum UserRole {
        ADMIN, OPERATOR
    }

    // Constructors
    public AdminUser() {}

    public AdminUser(String username, String passwordHash, String fullName, UserRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    @Override
    public String toString() {
        return "AdminUser{id=" + id + ", username='" + username + "', role=" + role + "}";
    }
}