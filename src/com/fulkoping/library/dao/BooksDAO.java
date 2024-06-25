package com.fulkoping.library.dao;

import com.fulkoping.library.Database;
import com.fulkoping.library.model.Books;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BooksDAO {

    public List<Books> searchBooks(String keyword) throws SQLException {
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                List<Books> books = new ArrayList<>();
                while (rs.next()) {
                    Books book = new Books(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getBoolean("available"));
                    books.add(book);
                }
                return books;
            }
        }
    }

    public Books getBookById(int id) throws SQLException {
        String query = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Books(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getBoolean("available"));
                }
                return null;
            }
        }
    }

    public void updateBookAvailability(int bookId, boolean available) throws SQLException {
        String query = "UPDATE books SET available = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, available);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }
}
