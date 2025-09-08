package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DBInitializer {

    // Call this once to insert slots dynamically
    public static void initializeSlots(int count) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO slots (slot_number, is_occupied) VALUES (?, FALSE)";
            PreparedStatement ps = conn.prepareStatement(sql);

            for (int i = 1; i <= count; i++) {
                ps.setInt(1, i);
                ps.executeUpdate();
            }

            System.out.println(count + " slots initialized successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
