import Dao.BookDAO;
import Model.Book;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        BookDAO bookDAO = new BookDAO();

        Book book = new Book();
        book.setISBN("978-3-16-148410-0");
        book.setTitle("Effective Java");
        book.setCategory("Programming");
        book.setSellingPrice(new Double("70.00"));
        book.setAuthor("Joshua Bloch");
        book.setStock(10);
        book.setCoverImage(null); // Assuming no cover image for simplicity

        try {
            bookDAO.addBook(book);
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add book.");
        }
    }
}
