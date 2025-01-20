package Controller;

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
            String user = System.getenv("root");
            String password = System.getenv("IndritFerati2604!");
            conn = DriverManager.getConnection(url, "root","IndritFerati2604!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("No name for class com.mysql.cj.jdbc.Driver, or connection with db failed");
        }
    }
    public TotalCostController(Connection conn) {
        this.conn = conn;
    }

    public double calculateSalaryByRoleAndTimeframe(String role, String timeframe) {
        double totalSalary = 0.0;
        try {
            String sql = "SELECT salary FROM users WHERE role = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                totalSalary += rs.getDouble("salary");
            }
        } catch (SQLException e) {
            System.err.println("SQLException: Error fetching salary from user with given role " + e.getMessage());
        }

        double annualSalary = totalSalary;
        switch (timeframe.toLowerCase()) {
            case "daily":
                annualSalary /= 365;
                break;
            case "weekly":
                annualSalary /= 52;
                break;
            case "monthly":
                annualSalary /= 12;
                break;
            case "yearly":
                break;
            default:
                throw new IllegalArgumentException("Invalid timeframe specified");
        }

        return annualSalary;
    }

    public double calculateTotalBillCostWithTaxByTimeframe(String timeframe) {
        List<Double> billsTotalCosts;
        LocalDate today = LocalDate.now();
        switch (timeframe.toLowerCase()) {
            case "daily":
                billsTotalCosts = getTotalBillCostsAddedOnDate(today);
                break;
            case "weekly":
                LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
                LocalDate endOfWeek = startOfWeek.plusDays(6);
                billsTotalCosts = getTotalBillCostsAddedInRange(startOfWeek, endOfWeek);
                break;
            case "monthly":
                LocalDate startOfMonth = today.withDayOfMonth(1);
                LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
                billsTotalCosts = getTotalBillCostsAddedInRange(startOfMonth, endOfMonth);
                break;
            case "yearly":
                LocalDate startOfYear = today.withDayOfYear(1);
                LocalDate endOfYear = startOfYear.plusYears(1).minusDays(1);
                billsTotalCosts = getTotalBillCostsAddedInRange(startOfYear, endOfYear);
                break;
            default:
                throw new IllegalArgumentException("Invalid timeframe specified");
        }

        double totalBillCost = 0.0;
        for (Double cost : billsTotalCosts) {
            totalBillCost += cost;
        }

        double taxRate = 0.2;
        double totalBillCostWithTax = totalBillCost * (1 + taxRate);
        return totalBillCostWithTax - totalBillCost; // Only return tax amount
    }

    public List<Double> getTotalBillCostsAddedOnDate(LocalDate date) {
        List<Double> totalCosts = new ArrayList<>();
        String sql = "SELECT total_price FROM bills WHERE DATE(created_at) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                double price = rs.getDouble("total_price");
                totalCosts.add(price);
            }
        } catch (SQLException e) {
            System.err.println("SQLException: Error fetching total price from DB");
        }
        return totalCosts;
    }

    public List<Double> getTotalBillCostsAddedInRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate cannot be after endDate.");
        }
        List<Double> totalCosts = new ArrayList<>();
        String sql = "SELECT total_price FROM bills WHERE DATE(created_at) BETWEEN ? AND ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                double price = rs.getDouble("total_price");
                totalCosts.add(price);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching total price from bill");
        }
        return totalCosts;
    }
}
