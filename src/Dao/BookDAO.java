//package Dao;
//
//import Model.Book;
//
//import java.math.BigDecimal;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//class DatabaseConnection {
//    private static final String URL = "localhost:3306/bookstore"; // Adjust URL, port, and DB name
//    private static final String USER = "";
//    private static final String PASSWORD = "";
//
//    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(URL, USER, PASSWORD);
//    }
//}
//public class BookDAO {
//    public void addBook(Book book) throws SQLException {
//        String sql = "INSERT INTO books (isbn, title, category, supplier, purchased_date, purchased_price, original_price, selling_price, author, stock, cover_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, book.getISBN());
//            stmt.setString(2, book.getTitle());
//            stmt.setString(3, book.getCategory());
//            stmt.setBigDecimal(8, BigDecimal.valueOf(book.getSellingPrice()));
//            stmt.setString(9, book.getAuthor());
//            stmt.setInt(10, book.getStock());
//            stmt.setBlob(11, (Blob) book.getCoverImage());
//            stmt.executeUpdate();
//        }
//    }
//
//    public void updateBook(Book book) throws SQLException {
//        String sql = "UPDATE books SET title = ?, category = ?, supplier = ?, purchased_date = ?, purchased_price = ?, original_price = ?, selling_price = ?, author = ?, stock = ?, cover_image = ? WHERE isbn = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, book.getTitle());
//            stmt.setString(2, book.getCategory());
//            stmt.setBigDecimal(7, book.getSellingPrice());
//            stmt.setString(8, book.getAuthor());
//            stmt.setInt(9, book.getStock());
//            stmt.setBlob(10, (Blob) book.getCoverImage());
//            stmt.setString(11, book.getISBN());
//            stmt.executeUpdate();
//        }
//    }
//
//    public void deleteBook(String isbn) throws SQLException {
//        String sql = "DELETE FROM books WHERE isbn = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, isbn);
//            stmt.executeUpdate();
//        }
//    }
//
//    public Book getBook(String isbn) throws SQLException {
//        String sql = "SELECT * FROM books WHERE isbn = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, isbn);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    Book book = new Book();
//                    book.setISBN(rs.getString("isbn"));
//                    book.setTitle(rs.getString("title"));
//                    book.setCategory(rs.getString("category"));
//                    book.setSellingPrice(rs.getDouble("selling_price"));
//                    book.setAuthor(rs.getString("author"));
//                    book.setStock(rs.getInt("stock"));
//                    book.setCoverImage(rs.getBlob("cover_image"));
//                    return book;
//                }
//            }
//        }
//        return null;
//    }
//
//    public List<Book> getAllBooks() throws SQLException {
//        List<Book> books = new ArrayList<>();
//        String sql = "SELECT * FROM books";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql);
//             ResultSet rs = stmt.executeQuery()) {
//            while (rs.next()) {
//                Book book = new Book();
//                book.setISBN(rs.getString("isbn"));
//                book.setTitle(rs.getString("title"));
//                book.setCategory(rs.getString("category"));
//                book.setSellingPrice(rs.getDouble("selling_price"));
//                book.setAuthor(rs.getString("author"));
//                book.setStock(rs.getInt("stock"));
//                book.setCoverImage(rs.getBlob("cover_image"));
//                books.add(book);
//            }
//        }
//        return books;
//    }
//
//    public List<Book> getLowStockBooks() throws SQLException {
//        List<Book> books = new ArrayList<>();
//        String sql = "SELECT * FROM books WHERE stock < 5";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql);
//             ResultSet rs = stmt.executeQuery()) {
//            while (rs.next()) {
//                Book book = new Book();
//                book.setISBN(rs.getString("isbn"));
//                book.setTitle(rs.getString("title"));
//                book.setCategory(rs.getString("category"));
//                book.setSellingPrice(rs.getDouble("selling_price"));
//                book.setAuthor(rs.getString("author"));
//                book.setStock(rs.getInt("stock"));
//                book.setCoverImage(rs.getBlob("cover_image"));
//                books.add(book);
//            }
//        }
//        return books;
//    }
//
//    public List<Book> searchBooks(String keyword) throws SQLException {
//        List<Book> books = new ArrayList<>();
//        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, "%" + keyword + "%");
//            stmt.setString(2, "%" + keyword + "%");
//            stmt.setString(3, "%" + keyword + "%");
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    Book book = new Book();
//                    book.setISBN(rs.getString("isbn"));
//                    book.setTitle(rs.getString("title"));
//                    book.setCategory(rs.getString("category"));
//                    book.setSellingPrice(rs.getDouble("selling_price"));
//                    book.setAuthor(rs.getString("author"));
//                    book.setStock(rs.getInt("stock"));
//                    book.setCoverImage(rs.getBlob("cover_image"));
//                    books.add(book);
//                }
//            }
//        }
//        return books;
//    }
//}