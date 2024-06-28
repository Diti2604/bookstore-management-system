package Controller;

import Model.Book;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TotalCostController {
    private Connection conn;

    public TotalCostController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bookstore";
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public double calculateSalaryByRoleAndTimeframe(String role, String timeframe) {
        double salary = 0.0;
        try {
            String sql = "SELECT salary FROM users WHERE role = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double yearlySalary = rs.getDouble("salary");
                switch (timeframe.toLowerCase()) {
                    case "daily":
                        salary = yearlySalary / 365;
                        break;
                    case "weekly":
                        salary = yearlySalary / 52;
                        break;
                    case "monthly":
                        salary = yearlySalary / 12;
                        break;
                    case "yearly":
                        salary = yearlySalary;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid timeframe specified");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salary;
    }

    public double calculateTotalBookCostWithTaxByTimeframe(String timeframe) {
        List<Book> books;
        LocalDate today = LocalDate.now();
        switch (timeframe.toLowerCase()) {
            case "daily":
                books = getBooksAddedOnDate(today);
                break;
            case "weekly":
                LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
                LocalDate endOfWeek = startOfWeek.plusDays(6);
                books = getBooksAddedInRange(startOfWeek, endOfWeek);
                break;
            case "monthly":
                LocalDate startOfMonth = today.withDayOfMonth(1);
                LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
                books = getBooksAddedInRange(startOfMonth, endOfMonth);
                break;
            case "yearly":
                LocalDate startOfYear = today.withDayOfYear(1);
                LocalDate endOfYear = startOfYear.plusYears(1).minusDays(1);
                books = getBooksAddedInRange(startOfYear, endOfYear);
                break;
            default:
                throw new IllegalArgumentException("Invalid timeframe specified");
        }
        return calculateTotalBookCostWithTax(books);
    }

    private List<Book> getBooksAddedOnDate(LocalDate date) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE DATE(date_created) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("title");
                String author = rs.getString("author");
                double price = rs.getDouble("selling_price");

                Book book = new Book(isbn, title, author, price);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    private List<Book> getBooksAddedInRange(LocalDate startDate, LocalDate endDate) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE DATE(date_created) BETWEEN ? AND ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("title");
                String author = rs.getString("author");
                double price = rs.getDouble("selling_price");

                Book book = new Book(isbn, title, author, price);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    private double calculateTotalBookCostWithTax(List<Book> books) {
        double totalBookCost = 0.0;
        for (Book book : books) {
            totalBookCost += book.getSellingPrice();
        }

        double taxRate = 0.2;
        double totalBookCostWithTax = totalBookCost * (1 + taxRate);
        return totalBookCostWithTax;
    }
}
