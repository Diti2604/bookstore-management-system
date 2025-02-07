package Controller;

import Model.Book;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CreateBillController {
    private Connection conn;

    public CreateBillController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bookstore";
            String user = System.getenv("root");
            System.out.println(user);
            String password = System.getenv("IndritFerati2604!");
            conn = DriverManager.getConnection(url, "root", "IndritFerati2604!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("No name for class com.mysql.cj.jdbc.Driver, or connection with db failed");
        }
    }
    public CreateBillController(Connection conn) {
        this.conn = conn;
    }

    public Book getBookByISBN(String isbn) {
        try {
            String query = "SELECT * FROM books WHERE ISBN = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                String category = rs.getString("category");
                double sellingPrice = rs.getDouble("selling_price");
                String author = rs.getString("author");
                int stock = rs.getInt("stock");
                Blob coverImageBlob = rs.getBlob("cover_image_path");

                Image coverImage = null;
                if (coverImageBlob != null) {
                    byte[] imageData = coverImageBlob.getBytes(1, (int) coverImageBlob.length());
                    coverImage = new Image(new ByteArrayInputStream(imageData));
                }

                return new Book(isbn, title, category, sellingPrice, author, stock, coverImage);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching book by isbn from database with isbn: " + isbn);
        }
        return null;
    }

    public void updateBookStock(String isbn, int newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        try {
            String query = "UPDATE books SET stock = ? where ISBN = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, newStock);
            pstmt.setString(2, isbn);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while updating stock for books");
        }
    }
    public void saveBillToDatabase(String librarianUsername, String title, int quantity, double totalPrice) {
        if (librarianUsername == null || librarianUsername.isEmpty()) {
            throw new IllegalArgumentException("Librarian username cannot be null or empty");
        }
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (totalPrice <= 0) {
            throw new IllegalArgumentException("Total price must be greater than 0");
        }
        try {
            String query = "INSERT INTO bills (librarian_username, book_title, quantity, total_price, created_at) VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, librarianUsername);
            pstmt.setString(2, title);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, totalPrice);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while saving bill to the database");
        }
    }

    public List<String> getAllISBNsOrderedByStock() {
        List<String> isbns = new ArrayList<>();
        try {
            String query = "SELECT ISBN FROM books ORDER BY stock DESC";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                isbns.add(rs.getString("ISBN"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching ISBNs in stock");
        }
        return isbns;
    }
}



