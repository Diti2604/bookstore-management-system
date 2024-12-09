package Controller;

import Model.Book;
import View.*;
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
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }
        return null;
    }

    public void updateBookStock(String isbn, int newStock) {
        try {
            String query = "UPDATE books SET stock = ? where ISBN = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, newStock);
            pstmt.setString(2, isbn);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void saveBillToDatabase(String librarianUsername, String title, int quantity, double totalPrice) {
        try {
            String query = "INSERT INTO bills (librarian_username, book_title, quantity, total_price, created_at) VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, librarianUsername);
            pstmt.setString(2, title);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, totalPrice);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return isbns;
    }
}



