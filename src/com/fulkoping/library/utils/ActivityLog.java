package com.fulkoping.library.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityLog {

    // Metod för att skriva in aktiviteter från användarna
    public static void log(Connection conn, String usersId, String message) {
        String query = "INSERT INTO ActivityLog (usersId, message) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, usersId);
            ps.setString(2, message);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Metod att ta ut userId från email
    public static int getUsersIdFromEmail(Connection connection, String email) throws SQLException {
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
