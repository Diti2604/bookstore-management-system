package Controller;

import Model.User;
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
            String password = System.getenv("DitiHost2604");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookstore", "root", "DitiHost2604");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
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
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return librarianUsernames;
    }

    public List<String> fetchLibrarianSalesData(String librarianUsername, LocalDate startDate, LocalDate endDate) {
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

                String salesRecord = "Book Title: " + bookTitle + ", Quantity Sold: " + quantity +
                        ", Total Price: $" + totalPrice + ", Sale Date: " + createdAt;
                salesData.add(salesRecord);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return salesData;
    }

}
