-- Create database
CREATE DATABASE IF NOT EXISTS parking_lot;
USE parking_lot;

-- Vehicles table
CREATE TABLE vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL,
    type VARCHAR(10) NOT NULL,        -- Car, Bike, etc.
    entry_time DATETIME NOT NULL,
    exit_time DATETIME
);

-- Slots table
CREATE TABLE slots (
    id INT AUTO_INCREMENT PRIMARY KEY,
    slot_number INT UNIQUE NOT NULL,
    is_occupied BOOLEAN DEFAULT FALSE,
    vehicle_id INT,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Transactions table
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    transaction_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Add to parking_lot.sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password_hash, full_name, role) VALUES
('admin', 'admin@autopark.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'System Administrator', 'ADMIN');

-- Add user_id to vehicles table for tracking
ALTER TABLE vehicles ADD COLUMN user_id BIGINT;
ALTER TABLE vehicles ADD FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;

-- Update transactions table
ALTER TABLE transactions ADD COLUMN payment_method ENUM('CASH', 'UPI', 'CARD') DEFAULT 'CASH';
ALTER TABLE transactions ADD COLUMN payment_status ENUM('PENDING', 'PAID', 'FAILED') DEFAULT 'PENDING';


