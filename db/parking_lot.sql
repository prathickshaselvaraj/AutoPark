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


