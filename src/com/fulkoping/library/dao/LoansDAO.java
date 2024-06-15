package com.fulkoping.library.dao;

import com.fulkoping.library.model.Loans;
import com.fulkoping.library.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoansDAO {

    public void loanBook(int userId, int bookId) throws SQLException {
        String query = "INSERT INTO Loans (user_id, book_id, start_date, end_date, loan_date) VALUES (?, ?, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY), CURRENT_TIMESTAMP)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }

    public void returnBook(int userId, int bookId) throws SQLException {
        String query = "UPDATE Loans SET returned = true, return_date = CURRENT_DATE WHERE user_id = ? AND book_id = ? AND returned = false";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }

    public List<Loans> getUserLoans(int userId) throws SQLException {
        String query = "SELECT * FROM Loans WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Loans> loans = new ArrayList<>();
                while (rs.next()) {
                    Loans loan = new Loans(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("book_id"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getTimestamp("loan_date"),
                            rs.getBoolean("returned"),
                            rs.getDate("return_date")
                    );
                    loans.add(loan);
                }
                return loans;
            }
        }
    }
}
