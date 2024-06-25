package com.fulkoping.library.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityLog {


    public static void log(Connection conn, String userId, String action) {
        String query = "INSERT INTO activitylog (user_id, action) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, userId);
            ps.setString(2, action);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static int getUserIdFromEmail(Connection connection, String email) throws SQLException {
        String query = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    return result.getInt("id");
                } else {
                    throw new SQLException("Ingen användare hittad med: " + email);
                }
            }
        }
    }
}
