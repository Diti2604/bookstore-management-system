package Controller;

import Model.Book;
import View.AddBookView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.sql.*;

public class AddBookController {
    private Connection conn;
    private String url;
    private String user;
    private String password;
    public AddBookController(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bookstore";
            String user = System.getenv("root");
            System.out.println(user);
            String password = System.getenv("DitiHost2604");
            conn = DriverManager.getConnection(url, "root", "DitiHost2604");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    public void saveBook(Book book) {
        System.out.println("Saving book to database: " + book.getTitle());

        try {
            String checkBookSQL = "SELECT stock FROM books WHERE title = ?";
            PreparedStatement checkStatement = conn.prepareStatement(checkBookSQL);
            checkStatement.setString(1, book.getTitle());
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                int currentStock = resultSet.getInt("stock");
                int newStock = currentStock + 1;

                String updateStockSQL = "UPDATE books SET stock = ? WHERE title = ?";
                PreparedStatement updateStatement = conn.prepareStatement(updateStockSQL);
                updateStatement.setInt(1, newStock);
                updateStatement.setString(2, book.getTitle());
                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Stock updated successfully!");
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("Stock Updated");
                        alert.setContentText("Stock for the book has been updated successfully.");
                        alert.showAndWait();
                    });
                } else {
                    System.out.println("Failed to update the stock.");
                }

            } else {
                String insertBookSQL = "INSERT INTO books (isbn, title, category, selling_price, author, stock, cover_image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStatement = conn.prepareStatement(insertBookSQL);
                insertStatement.setString(1, book.getISBN());
                insertStatement.setString(2, book.getTitle());
                insertStatement.setString(3, book.getCategory());
                insertStatement.setDouble(4, book.getSellingPrice());
                insertStatement.setString(5, book.getAuthor());
                insertStatement.setInt(6, 1);
                insertStatement.setString(7, book.getUrl());

                int rowsAffected = insertStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Book saved successfully!");
                } else {
                    System.out.println("Failed to save the book.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while saving the book: " + e.getMessage());
        }
    }

    public boolean bookExistsByName(String title) {
        try {
            String checkBookSQL = "SELECT * FROM books WHERE title = ?";
            PreparedStatement checkStatement = conn.prepareStatement(checkBookSQL);
            checkStatement.setString(1, title);
            ResultSet resultSet = checkStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Error checking book existence: " + e.getMessage());
            return false;
        }
    }

    public Book getBookByName(String title) {
        try {
            String getBookSQL = "SELECT * FROM books WHERE title = ?";
            PreparedStatement getStatement = conn.prepareStatement(getBookSQL);
            getStatement.setString(1, title);
            ResultSet resultSet = getStatement.executeQuery();

            if (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                String category = resultSet.getString("category");
                String author = resultSet.getString("author");
                double sellingPrice = resultSet.getDouble("selling_price");
                String imageUrl = resultSet.getString("cover_image_path");

                return new Book(imageUrl, title, category, isbn, author, sellingPrice);
            }
        } catch (SQLException e) {
            System.out.println("Error getting book details: " + e.getMessage());
        }
        return null;
    }

}
