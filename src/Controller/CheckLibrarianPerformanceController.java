package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CheckLibrarianPerformanceController {
    private Connection conn;

    public CheckLibrarianPerformanceController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bookstore";
            String user = System.getenv("root");
            System.out.println(user);
            String password = System.getenv("IndritFerati2604!");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookstore", "root", "IndritFerati2604!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("No name for class com.mysql.cj.jdbc.Driver, or connection with db failed");
        }
    }

    public CheckLibrarianPerformanceController(Connection mockConnection) {
        this.conn = mockConnection;
    }


    public ObservableList<String> fetchLibrariansFromDatabase() {
        ObservableList<String> librarianUsernames = FXCollections.observableArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT username FROM users WHERE role = 'Librarian'";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                librarianUsernames.add(rs.getString("username"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching librarians:");
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.out.println("Error fetching librarians:");
            }
        }

        return librarianUsernames;
    }
    public List<String> fetchLibrarianSalesData(String librarianUsername, LocalDate startDate, LocalDate endDate) {
        // Initial null check
        if (librarianUsername == null && startDate == null && endDate == null) {
            throw new IllegalArgumentException("Inputs cannot be null");
        }

        if(librarianUsername == null){
            throw new IllegalArgumentException("Librarian cannot be null");
        }

        // Date null checks
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        // Date range validation
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        List<String> salesData = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT book_title, quantity, total_price, created_at " +
                    "FROM bills " +
                    "WHERE librarian_username = ? " +
                    "AND created_at BETWEEN ? AND ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, librarianUsername);
            pstmt.setDate(2, java.sql.Date.valueOf(startDate));
            pstmt.setDate(3, java.sql.Date.valueOf(endDate));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String bookTitle = rs.getString("book_title");
                int quantity = rs.getInt("quantity");
                double totalPrice = rs.getDouble("total_price");
                LocalDate createdAt = rs.getDate("created_at").toLocalDate();

                String salesRecord = String.format("Book Title: %s, Quantity Sold: %d, Total Price: $%.2f, Sale Date: %s",
                        bookTitle, quantity, totalPrice, createdAt);
                salesData.add(salesRecord);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sales data: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return salesData;
    }

}
