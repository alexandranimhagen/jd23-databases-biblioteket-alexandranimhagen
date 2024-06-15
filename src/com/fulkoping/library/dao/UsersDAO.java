package com.fulkoping.library.dao;

import com.fulkoping.library.Database;
import com.fulkoping.library.model.Users;
import com.fulkoping.library.utils.Hashing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersDAO {
    public void addUser(Users user) throws SQLException {
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, Hashing.encrypt(user.getPassword()));
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();
        }
    }

    public Users getUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Users user = new Users(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getTimestamp("created_at")
                    );
                    return user;
                }
                return null;
            }
        }
    }
}
