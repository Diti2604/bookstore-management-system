package test.UnitTesting;

import Controller.AddBookController;
import Controller.BookStatisticsController;
import Controller.CheckLibrarianPerformanceController;
import Controller.CreateBillController;
import Model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerTest {
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private AddBookController addBookController;
    private CreateBillController createBillController;
    private CheckLibrarianPerformanceController checkLibrarianPerformanceController;
    private BookStatisticsController bookStatisticsController;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        addBookController = new AddBookController(mockConnection);
        createBillController = new CreateBillController(mockConnection);
        checkLibrarianPerformanceController = new CheckLibrarianPerformanceController((mockConnection));
        bookStatisticsController = new BookStatisticsController(mockConnection);
    }


    /*

    THESE METHODS ARE METHODS OF THE CREATE BILL CONTROLLER CLASS

    */
    @Test
    void testUpdateBookStockWithValidInput() throws SQLException {
        //The book chosen for this test was 1984, this book has a stock number of 151 when this method was being created
        String isbn = "9780-45-1524";
        int newStock = 75;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        createBillController.updateBookStock(isbn, newStock);

        verify(mockPreparedStatement).setInt(1, newStock);
        verify(mockPreparedStatement).setString(2, isbn);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testUpdateBookStockWithZeroStock() throws SQLException {
        String isbn = "9780-45-1524";
        int newStock = 0;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);


        createBillController.updateBookStock(isbn, newStock);


        verify(mockPreparedStatement).setInt(1, newStock);
        verify(mockPreparedStatement).setString(2, isbn);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testUpdateBookStockWithMaxStock() throws SQLException {
        String isbn = "9780-45-1524";
        int newStock = 151;  // Max int value

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        createBillController.updateBookStock(isbn, newStock);

        verify(mockPreparedStatement).setInt(1, newStock);
        verify(mockPreparedStatement).setString(2, isbn);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testSaveBillToDatabaseValidInputs() throws SQLException {
        String librarianUsername = "librarian1";
        String title = "1984";
        int quantity = 5;
        double totalPrice = 100.0;

        createBillController.saveBillToDatabase(librarianUsername, title, quantity, totalPrice);

        verify(mockConnection).prepareStatement("INSERT INTO bills (librarian_username, book_title, quantity, total_price, created_at) VALUES (?, ?, ?, ?, NOW())");

        verify(mockPreparedStatement).setString(1, librarianUsername);
        verify(mockPreparedStatement).setString(2, title);
        verify(mockPreparedStatement).setInt(3, quantity);
        verify(mockPreparedStatement).setDouble(4, totalPrice);

        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testSaveBillToDatabaseInvalidInputs() throws SQLException {
        //Invalid librarian username
        String librarianUsername = "librarian1";
        String title = "1984";
        int quantity = -5;
        double totalPrice = 100.0;

        try {
            createBillController.saveBillToDatabase(librarianUsername, title, quantity, totalPrice);
            fail("Expected IllegalArgumentException for invalid quantity");
        } catch (IllegalArgumentException e) {
            assertEquals("Quantity must be greater than 0", e.getMessage());
        }

        verify(mockConnection, never()).prepareStatement(anyString());
        verify(mockPreparedStatement, never()).setString(anyInt(), anyString());
        verify(mockPreparedStatement, never()).setInt(anyInt(), anyInt());
        verify(mockPreparedStatement, never()).setDouble(anyInt(), anyDouble());
        verify(mockPreparedStatement, never()).executeUpdate();
    }


    @Test
    void testGetAllISBNsOrderedByStockZeroISBN() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        List<String> isbns = createBillController.getAllISBNsOrderedByStock();
        assert (isbns.isEmpty());
    }

    @Test
    void testGetAllISBNsOrderedByStockNominalCase() throws SQLException {
        //5 ISBN in the database are used as the nominal value here
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true)
                .thenReturn(true).thenReturn(true).thenReturn(true)
                .thenReturn(false);
        when(mockResultSet.getString("ISBN")).thenReturn("9781-23-4567", "9780-45-1524", "9780-74-3273", "9781-50-3280", "9781-50-3290");

        List<String> isbns = createBillController.getAllISBNsOrderedByStock();
        assert (isbns.size() == 6);
        assert (isbns.get(0).equals("9781-23-4567"));
        assert (isbns.get(5).equals("9781-50-3290"));
    }

    @Test
    void testGetAllISBNsOrderedByStockMaxBound() throws SQLException {
        //This test was done with 10 ISBNs in the database
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true)
                .thenReturn(true).thenReturn(true).thenReturn(true)
                .thenReturn(true).thenReturn(true).thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(mockResultSet.getString("ISBN")).thenReturn("9781-23-4567", "9780-45-1524", "9780-74-3273", "9781-50-3280", "9781-50-3290", "9780-31-6769", "9780-06-0850", "9780-54-7928", "9780-34-5339", "1234-56-7890");
        List<String> isbns = createBillController.getAllISBNsOrderedByStock();
        assert (isbns.size() == 10);
    }

    @Test
    void testGetAllISBNsOrderedByStockSQLException() throws SQLException {
        // Tested for a database connectivity error
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
        List<String> isbns = createBillController.getAllISBNsOrderedByStock();
        assert (isbns.isEmpty());
    }
    /*

    THE TESTS HERE ARE PART OF THE ADD BOOK CONTROLLER CLASS

     */


    @Test
    void testBookExistsByNameWithExistingBook() throws SQLException {
        String title = "1984";

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        boolean exists = addBookController.bookExistsByName(title);
        assertTrue(exists);

        verify(mockPreparedStatement).setString(1, title);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testBookExistsByNameWithNonExistingBook() throws SQLException {
        String title = "1994";

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        boolean exists = addBookController.bookExistsByName(title);
        assertFalse(exists);

        verify(mockPreparedStatement).setString(1, title);
        verify(mockPreparedStatement).executeQuery();
    }


    @Test
    void testBookExistsByNameWithEmptyTitle() throws SQLException {
        String title = "";

        boolean exists = addBookController.bookExistsByName(title);
        assertFalse(exists);

        verify(mockPreparedStatement).setString(1, title);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetBookByNameWhenBookExists() throws SQLException {
        String title = "1984";
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("isbn")).thenReturn("9780-45-1524");
        when(mockResultSet.getString("category")).thenReturn("Fiction");
        when(mockResultSet.getString("author")).thenReturn("George Orwell");
        when(mockResultSet.getDouble("selling_price")).thenReturn(15.99);

        Book book = addBookController.getBookByName(title);
        assertNotNull(book);
        assertEquals("9780-45-1524", book.getISBN());
        assertEquals("Fiction", book.getCategory());
        assertEquals("George Orwell", book.getAuthor());
        assertEquals(15.99, book.getSellingPrice());
        verify(mockPreparedStatement).setString(1, title);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetBookByNameWhenBookDoesNotExist() throws SQLException {
        String title = "1984";
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        Book book = addBookController.getBookByName(title);
        assertNull(book);
        verify(mockPreparedStatement).setString(1, title);
        verify(mockPreparedStatement).executeQuery();
    }


    @Test
    void testGetBookByNameWhenTitleIsEmpty() throws SQLException {
        String title = "";
        Book book = addBookController.getBookByName(title);
        assertNull(book);
        verify(mockPreparedStatement).setString(1, title);
        verify(mockPreparedStatement).executeQuery();
    }

    /*

    THESE TESTS ARE FOR THE CHECK LIBRARIAN PERFORMANCE CONTROLLER CLASS

     */

    @Test
    void testFetchLibrariansFromDatabase() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("username"))
                .thenReturn("librarian1", "librarian2");
        ObservableList<String> result = checkLibrarianPerformanceController.fetchLibrariansFromDatabase();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(FXCollections.observableArrayList("librarian1", "librarian2")));
        verify(mockConnection).prepareStatement("SELECT username FROM users WHERE role = 'Librarian'");
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, times(3)).next();
        verify(mockResultSet, times(2)).getString("username");
    }

    @Test
    void testFetchLibrariansFromDatabaseWithNoLibrarians() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);
        ObservableList<String> result = checkLibrarianPerformanceController.fetchLibrariansFromDatabase();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockConnection).prepareStatement("SELECT username FROM users WHERE role = 'Librarian'");
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockResultSet, never()).getString("username");
    }

    @Test
    void testFetchLibrariansFromDatabaseSQLException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
        ObservableList<String> result = checkLibrarianPerformanceController.fetchLibrariansFromDatabase();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockConnection).prepareStatement("SELECT username FROM users WHERE role = 'Librarian'");
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, never()).next();
    }

    @Test
    void testGetDailyStatisticsValidData() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("book_title")).thenReturn("1984", "Harry Potter");
        when(mockResultSet.getInt("total_quantity")).thenReturn(5, 10);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0);

        ObservableList<Book> result = bookStatisticsController.getDailyStatistics();

        assertNotNull(result);
        assertEquals(2, result.size());

        Book book1 = result.get(0);
        assertEquals("1984", book1.getTitle());
        assertEquals(5, book1.getStock());
        assertEquals(100.0, book1.getSellingPrice());

        Book book2 = result.get(1);
        assertEquals("Harry Potter", book2.getTitle());
        assertEquals(10, book2.getStock());
        assertEquals(200.0, book2.getSellingPrice());

        verify(mockConnection).prepareStatement("SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price " +
                "FROM bills WHERE DATE(created_at) = CURDATE() GROUP BY book_title");
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, times(3)).next();
    }

    @Test
    void testGetDailyStatisticsNoData() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        ObservableList<Book> result = bookStatisticsController.getDailyStatistics();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mockConnection).prepareStatement("SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price " +
                "FROM bills WHERE DATE(created_at) = CURDATE() GROUP BY book_title");
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
    }

    @Test
    void testGetDailyStatisticsSQLException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        ObservableList<Book> result = bookStatisticsController.getDailyStatistics();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockConnection).prepareStatement("SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price " +
                "FROM bills WHERE DATE(created_at) = CURDATE() GROUP BY book_title");
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, never()).next();
    }

    @Test
    void testGetMonthlyStatisticsValidData() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("book_title")).thenReturn("1984", "Harry Potter");
        when(mockResultSet.getInt("total_quantity")).thenReturn(5, 10);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0);
        ObservableList<Book> result = bookStatisticsController.getMonthlyStatistics();
        assertNotNull(result);
        assertEquals(2, result.size());
        Book book1 = result.get(0);
        assertEquals("1984", book1.getTitle());
        assertEquals(5, book1.getStock());
        assertEquals(100.0, book1.getSellingPrice());
        Book book2 = result.get(1);
        assertEquals("Harry Potter", book2.getTitle());
        assertEquals(10, book2.getStock());
        assertEquals(200.0, book2.getSellingPrice());
        verify(mockConnection).prepareStatement("SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price " +
                "FROM bills WHERE MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE()) " +
                "GROUP BY book_title");
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, times(3)).next();
    }

    @Test
    void testGetMonthlyStatisticsNoData() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);
        ObservableList<Book> result = bookStatisticsController.getMonthlyStatistics();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockConnection).prepareStatement("SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price " +
                "FROM bills WHERE MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE()) " +
                "GROUP BY book_title");
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
    }

    @Test
    void testGetMonthlyStatisticsSQLException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
        ObservableList<Book> result = bookStatisticsController.getMonthlyStatistics();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockConnection).prepareStatement("SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price " +
                "FROM bills WHERE MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE()) " +
                "GROUP BY book_title");
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, never()).next();
    }

    @Test
    void testGetTotalStatisticsValidData() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("book_title")).thenReturn("1984", "Harry Potter");
        when(mockResultSet.getInt("total_quantity")).thenReturn(5, 10);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0);
        ObservableList<Book> result = bookStatisticsController.getTotalStatistics();
        assertNotNull(result);
        assertEquals(2, result.size());
        Book book1 = result.get(0);
        assertEquals("1984", book1.getTitle());
        assertEquals(5, book1.getStock());
        assertEquals(100.0, book1.getSellingPrice());
        Book book2 = result.get(1);
        assertEquals("Harry Potter", book2.getTitle());
        assertEquals(10, book2.getStock());
        assertEquals(200.0, book2.getSellingPrice());
        verify(mockConnection).prepareStatement("SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price FROM bills GROUP BY book_title");
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, times(3)).next();
    }

    @Test
    void testGetTotalStatisticsNoData() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);
        ObservableList<Book> result = bookStatisticsController.getTotalStatistics();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockConnection).prepareStatement("SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price FROM bills GROUP BY book_title");
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
    }

    @Test
    void testGetTotalStatisticsSQLException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
        ObservableList<Book> result = bookStatisticsController.getTotalStatistics();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockConnection).prepareStatement("SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price FROM bills GROUP BY book_title");
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, never()).next();
    }


    @Test
    void testGetStatisticsValidData() throws SQLException {
        String query = "SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price FROM bills GROUP BY book_title";

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("book_title")).thenReturn("1984", "Harry Potter");
        when(mockResultSet.getInt("total_quantity")).thenReturn(5, 10);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0);

        ObservableList<Book> result = bookStatisticsController.getStatistics(query);

        assertNotNull(result);
        assertEquals(2, result.size());

        Book book1 = result.get(0);
        assertEquals("1984", book1.getTitle());
        assertEquals(5, book1.getStock());
        assertEquals(100.0, book1.getSellingPrice());

        Book book2 = result.get(1);
        assertEquals("Harry Potter", book2.getTitle());
        assertEquals(10, book2.getStock());
        assertEquals(200.0, book2.getSellingPrice());

        verify(mockConnection).prepareStatement(query);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, times(3)).next();
    }

    @Test
    void testGetStatisticsNoData() throws SQLException {
        String query = "SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price FROM bills GROUP BY book_title";

        when(mockResultSet.next()).thenReturn(false);

        ObservableList<Book> result = bookStatisticsController.getStatistics(query);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mockConnection).prepareStatement(query);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
    }

    @Test
    void testGetStatisticsSQLException() throws SQLException {
        String query = "SELECT book_title, SUM(quantity) as total_quantity, SUM(total_price) as total_price FROM bills GROUP BY book_title";

        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        ObservableList<Book> result = bookStatisticsController.getStatistics(query);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mockConnection).prepareStatement(query);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, never()).next();
    }


}
