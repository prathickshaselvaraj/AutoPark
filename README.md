# 🚗 AutoPark — Automated Parking Management System

> A MySQL-powered parking management system with a complete relational database design — covering slot allocation, vehicle entry/exit tracking, billing, and reporting using advanced SQL queries.

---

## 📌 What This Project Does

AutoPark is a database-driven system that manages the full lifecycle of a parking facility:

- 🅿️ **Slot Management** — track available, occupied, and reserved parking slots
- 🚘 **Vehicle Entry & Exit** — log timestamps, assign slots, calculate duration
- 💰 **Billing Engine** — auto-calculate charges based on parking duration and vehicle type
- 📋 **Reports & Analytics** — revenue summaries, occupancy rates, peak hour analysis using SQL

---

## 🗄️ Database Design

### Entity Relationship Overview

```
Vehicles ──────< ParkingRecords >────── Slots
                      │
                  Payments
                      │
                 RateCards (by vehicle type)
```

### Core Tables

| Table | Description |
|---|---|
| `vehicles` | Vehicle registration details (number plate, type, owner) |
| `slots` | Parking slot inventory (slot number, floor, type, status) |
| `parking_records` | Entry/exit log with timestamps and assigned slot |
| `payments` | Billing records linked to parking records |
| `rate_cards` | Hourly rates by vehicle type (2-wheeler, 4-wheeler, etc.) |

---

## 🛠️ Tech Stack

| Layer | Tools |
|---|---|
| Database | MySQL |
| Query Language | SQL (DDL + DML + Advanced Queries) |
| Concepts | Normalization (3NF), Joins, Subqueries, Window Functions, Triggers, Stored Procedures |

---

## 📁 Project Structure

```
AutoPark/
├── schema/
│   ├── create_tables.sql       # DDL — table definitions with constraints
│   └── insert_data.sql         # Sample data for testing
├── queries/
│   ├── slot_management.sql     # Available slots, slot allocation logic
│   ├── billing.sql             # Duration calculation + charge computation
│   ├── reports.sql             # Revenue, occupancy, peak hours
│   └── advanced.sql            # Window functions, CTEs, subqueries
├── procedures/
│   ├── vehicle_entry.sql       # Stored procedure: log entry + assign slot
│   └── vehicle_exit.sql        # Stored procedure: log exit + generate bill
├── triggers/
│   └── slot_status_update.sql  # Trigger: auto-update slot status on entry/exit
└── README.md
```

---

## 🚀 Getting Started

### 1. Clone the repo
```bash
git clone https://github.com/prathickshaselvaraj/AutoPark.git
cd AutoPark
```

### 2. Set up the database
```bash
mysql -u root -p
```
```sql
CREATE DATABASE autopark;
USE autopark;
SOURCE schema/create_tables.sql;
SOURCE schema/insert_data.sql;
```

### 3. Run queries
```bash
SOURCE queries/reports.sql;
```

---

## 🔍 SQL Highlights

### Find currently occupied slots
```sql
SELECT s.slot_number, s.floor, v.vehicle_number, pr.entry_time
FROM slots s
JOIN parking_records pr ON s.slot_id = pr.slot_id
JOIN vehicles v ON pr.vehicle_id = v.vehicle_id
WHERE pr.exit_time IS NULL;
```

### Calculate revenue per day
```sql
SELECT DATE(exit_time) AS date,
       SUM(amount) AS daily_revenue,
       COUNT(*) AS total_vehicles
FROM payments p
JOIN parking_records pr ON p.record_id = pr.record_id
WHERE exit_time IS NOT NULL
GROUP BY DATE(exit_time)
ORDER BY date DESC;
```

### Peak hour analysis using window functions
```sql
SELECT HOUR(entry_time) AS hour,
       COUNT(*) AS vehicles_entered,
       RANK() OVER (ORDER BY COUNT(*) DESC) AS peak_rank
FROM parking_records
GROUP BY HOUR(entry_time);
```

### Find vehicles parked for more than 3 hours (unpaid)
```sql
SELECT v.vehicle_number, pr.entry_time,
       TIMESTAMPDIFF(HOUR, pr.entry_time, NOW()) AS hours_parked
FROM parking_records pr
JOIN vehicles v ON pr.vehicle_id = v.vehicle_id
LEFT JOIN payments p ON pr.record_id = p.record_id
WHERE pr.exit_time IS NULL
  AND TIMESTAMPDIFF(HOUR, pr.entry_time, NOW()) > 3
  AND p.payment_id IS NULL;
```

---

## 📊 Key SQL Concepts Demonstrated

| Concept | Where Used |
|---|---|
| `JOIN` (INNER, LEFT) | Linking vehicles → records → payments |
| `GROUP BY` + `HAVING` | Revenue by date, filtering by threshold |
| Window Functions (`RANK`, `ROW_NUMBER`) | Peak hour analysis, top earners |
| `TIMESTAMPDIFF` | Duration-based billing |
| Subqueries | Finding unoccupied slots, validating availability |
| CTEs (`WITH`) | Multi-step revenue calculations |
| Triggers | Auto-updating slot status on entry/exit |
| Stored Procedures | Encapsulating entry/exit business logic |

---

## 🔮 Future Improvements

- [ ] Add a Python Flask backend to expose REST APIs over the database
- [ ] Build a simple web dashboard for real-time slot visualization
- [ ] Add user authentication and admin vs attendant roles
- [ ] Integrate with a payment gateway simulation

---

## 👩‍💻 Author

**Prathicksha S**  
M.Sc. Decision and Computing Sciences, CIT Coimbatore  
📧 prathicksha.selvaraj@gmail.com  
🔗 [GitHub](https://github.com/prathickshaselvaraj) · [LinkedIn](https://linkedin.com/in/prathickshaselvaraj)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
