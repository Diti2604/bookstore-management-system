package Controller;

import Model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class BookStatisticsController {
    private Connection conn;

    public BookStatisticsController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bookstore";
            String user = System.getenv("root");
            String password = System.getenv("DitiHost2604");
            conn = DriverManager.getConnection(url, "root", "DitiHost2604");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Book> getDailyStatistics() {
        String query = "SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price " +
                "FROM bills WHERE DATE(created_at) = CURDATE() GROUP BY book_title";
        return getStatistics(query);
    }

    public ObservableList<Book> getMonthlyStatistics() {
        String query = "SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price " +
                "FROM bills WHERE MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE()) " +
                "GROUP BY book_title";
        return getStatistics(query);
    }

    public ObservableList<Book> getTotalStatistics() {
        String query = "SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price " +
                "FROM bills GROUP BY book_title";
        return getStatistics(query);
    }

    private ObservableList<Book> getStatistics(String query) {
        ObservableList<Book> statistics = FXCollections.observableArrayList();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String bookTitle = rs.getString("book_title");
                int totalQuantity = rs.getInt("total_quantity");
                double totalPrice = rs.getDouble("total_price");

                statistics.add(new Book(bookTitle, totalQuantity, totalPrice));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statistics;
    }
}
