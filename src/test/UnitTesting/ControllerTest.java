package test.UnitTesting;

import Controller.*;
import Model.Book;
import Model.User;
import View.MainDashboardView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
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
    private ManageEmployeesController manageEmployeesController;
    private TotalCostController totalCostController;
    private LoginController loginController;
    private Stage mockStage;
    private User mockUser;
    private MainDashboardView mockDashboardView;

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
        manageEmployeesController = new ManageEmployeesController(mockConnection);
        totalCostController = new TotalCostController(mockConnection);
        loginController = new LoginController(mockConnection);
        mockStage = mock(Stage.class);           // Mocking Stage (JavaFX Stage)
        mockUser = mock(User.class);             // Mocking User object
        mockDashboardView = mock(MainDashboardView.class);

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


    /*

    THESE METHODS ARE FOR METHODS IN BOOK STATISTICS CONTROLLER

     */

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

    /*

    THESE TESTS ARE FOR MANAGE EMPLOYEES CONTROLLER CLASS

     */

    @Test
    void testRegisterEmployee_ValidUser() throws SQLException {
        User validUser = new User("David", "david123", "David", LocalDate.now().minusYears(30), "0688567890", "David@example.com", 50000, "Librarian");
        when(manageEmployeesController.isUsernameExists(validUser.getUsername())).thenReturn(false);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        boolean result = manageEmployeesController.registerEmployee(validUser);
        assertTrue(result, "Valid user should be registered successfully.");
        verify(mockPreparedStatement).setString(1, validUser.getUsername());
        verify(mockPreparedStatement).setDate(4, Date.valueOf(validUser.getBirthday()));
        verify(mockPreparedStatement).executeUpdate();
    }


    @Test
    void testRegisterEmployee_InvalidAgeTooYoung() throws SQLException {
        User underageUser = new User("Ben", "ben123", "Ben", LocalDate.now().minusYears(16), "0694567890", "ben@example.com", 40000, "Manager");
        boolean result = manageEmployeesController.registerEmployee(underageUser);
        assertFalse(result, "User under 18 should not be registered.");
        verify(mockConnection, never()).prepareStatement(anyString());
    }

    @Test
    void testRegisterEmployee_InvalidAgeTooOld() throws SQLException {
        User seniorUser = new User("Tom", "tom123", "Tom", LocalDate.now().minusYears(70), "0684567890", "tom@example.com", 30000, "Admin");
        boolean result = manageEmployeesController.registerEmployee(seniorUser);
        assertFalse(result, "User over 65 should not be registered.");
        verify(mockConnection, never()).prepareStatement(anyString());
    }

    @Test
    void testRegisterEmployee_DuplicateUsername() throws SQLException {
        User duplicateUser = new User("David", "david123", "david", LocalDate.now().minusYears(35), "0684567890", "david@example.com", 45000, "Librarian");
        when(manageEmployeesController.isUsernameExists(duplicateUser.getUsername())).thenReturn(true);
        boolean result = manageEmployeesController.registerEmployee(duplicateUser);
        assertFalse(result, "Duplicate username should not allow registration.");
        verify(mockConnection, never()).prepareStatement(anyString());
    }

    @Test
    void testRegisterEmployee_DatabaseError() throws SQLException {
        User validUser = new User("Brandon", "brandon123", "Brandon", LocalDate.now().minusYears(30), "0674567890", "brandon@example.com", 50000, "Admin");
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        boolean result = manageEmployeesController.registerEmployee(validUser);
        assertFalse(result, "Database error should result in registration failure.");
    }


    @Test
    void testDeleteUserSuccess() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        boolean result = manageEmployeesController.deleteUser("David");
        assertTrue(result, "The user should be deleted successfully.");
        verify(mockConnection).prepareStatement("DELETE FROM users WHERE username = ?");
        verify(mockPreparedStatement).setString(1, "David");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testDeleteUserFailure() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);
        boolean result = manageEmployeesController.deleteUser("Adam");
        assertFalse(result, "The user should not be deleted because they do not exist.");
        verify(mockConnection).prepareStatement("DELETE FROM users WHERE username = ?");
        verify(mockPreparedStatement).setString(1, "Adam");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testDeleteUser_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        boolean result = manageEmployeesController.deleteUser("David");
        assertFalse(result, "The user should not be deleted due to an exception.");
        verify(mockConnection).prepareStatement("DELETE FROM users WHERE username = ?");
    }


    @Test
    void testGetUsersByRole() throws SQLException {
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        when(mockResultSet.getString("username")).thenReturn("user1");
        when(mockResultSet.getString("password")).thenReturn("pass1");
        when(mockResultSet.getString("name")).thenReturn("User1");
        when(mockResultSet.getDate("birthday")).thenReturn(Date.valueOf("1990-02-02"));
        when(mockResultSet.getString("phone")).thenReturn("0687654321");
        when(mockResultSet.getString("email")).thenReturn("user2@example.com");
        when(mockResultSet.getDouble("salary")).thenReturn(55000.0);

        when(mockResultSet.getString("username")).thenReturn("user2");
        when(mockResultSet.getString("password")).thenReturn("pass2");
        when(mockResultSet.getString("name")).thenReturn("User2");
        when(mockResultSet.getDate("birthday")).thenReturn(Date.valueOf("1990-02-02"));
        when(mockResultSet.getString("phone")).thenReturn("0687654321");
        when(mockResultSet.getString("email")).thenReturn("user2@example.com");
        when(mockResultSet.getDouble("salary")).thenReturn(55000.0);

        List<User> users = manageEmployeesController.getUsersByRole("librarian");

        assertNotNull(users, "The list of users should not be null.");
        assertEquals(2, users.size(), "The list should contain 2 users.");
        assertEquals("user1", users.get(0).getUsername(), "The first user's username should be 'user1'.");
        assertEquals("user2", users.get(1).getUsername(), "The second user's username should be 'user2'.");

        verify(mockConnection).prepareStatement("SELECT * FROM users WHERE role = ?");
        verify(mockPreparedStatement).setString(1, "librarian");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetUsersByRoleNoUsers() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);
        List<User> users = manageEmployeesController.getUsersByRole("librarian");
        assertNotNull(users, "The list of users should not be null.");
        assertTrue(users.isEmpty(), "The list of users should be empty.");
        verify(mockConnection).prepareStatement("SELECT * FROM users WHERE role = ?");
        verify(mockPreparedStatement).setString(1, "librarian");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetUsersByRoleSQLException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
        List<User> users = manageEmployeesController.getUsersByRole("Employee");
        assertNotNull(users, "The list of users should not be null.");
        assertTrue(users.isEmpty(), "The list of users should be empty due to an exception.");
        verify(mockConnection).prepareStatement("SELECT * FROM users WHERE role = ?");
        verify(mockPreparedStatement).setString(1, "Employee");
        verify(mockPreparedStatement).executeQuery();
    }


    @Test
    void testUpdateUserWithValidInput() throws SQLException {

        User user = new User("newUser", "password123", "John Doe", LocalDate.of(1990, 5, 15), "1234567890", "email@example.com", 5000.0, "Librarian");
        String currentUsername = "oldUser";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        boolean result = manageEmployeesController.updateUser(user, currentUsername);
        assertTrue(result);
        verify(mockPreparedStatement).setString(1, user.getUsername());
        verify(mockPreparedStatement).setString(2, user.getPassword());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testUpdateUserWithInvalidInput() throws SQLException {
        User user = new User("newUser", "", "John Doe", LocalDate.of(1990, 5, 15), "1234567890", "invalid_email", 5000.0, "Librarian");
        String currentUsername = "oldUser";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);
        boolean result = manageEmployeesController.updateUser(user, currentUsername);
        assertFalse(result);
    }

    @Test
    void testUpdateUserWithNullFields() throws SQLException {
        User user = new User("newUser", "password123", "John Doe", null, null, null, 5000.0, "Librarian");
        String currentUsername = "oldUser";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        boolean result = manageEmployeesController.updateUser(user, currentUsername);
        assertTrue(result);
    }

    @Test
    void testUpdateUserWithSQLException() throws SQLException {
        User user = new User("newUser", "password123", "John Doe", LocalDate.of(1990, 5, 15), "1234567890", "email@example.com", 5000.0, "Librarian");
        String currentUsername = "oldUser";
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        boolean result = manageEmployeesController.updateUser(user, currentUsername);
        assertFalse(result);
    }


    @Test
    void testIsUsernameExistsWhenItDoesExist() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("count")).thenReturn(1);
        boolean result = manageEmployeesController.isUsernameExists("librarian1");
        assertTrue(result, "Username should exist.");
        verify(mockConnection).prepareStatement("SELECT COUNT(*) AS count FROM users WHERE username = ?");
        verify(mockPreparedStatement).setString(1, "librarian1");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testIsUsernameExistWhenItDoesNotExist() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("count")).thenReturn(0);
        boolean result = manageEmployeesController.isUsernameExists("librarian50");
        assertFalse(result, "Username should not exist.");
        verify(mockConnection).prepareStatement("SELECT COUNT(*) AS count FROM users WHERE username = ?");
        verify(mockPreparedStatement).setString(1, "librarian50");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testIsUsernameExistsSQLException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
        boolean result = manageEmployeesController.isUsernameExists("David");
        assertFalse(result, "Method should return false when an exception occurs.");
        verify(mockConnection).prepareStatement("SELECT COUNT(*) AS count FROM users WHERE username = ?");
        verify(mockPreparedStatement).setString(1, "David");
        verify(mockPreparedStatement).executeQuery();
    }


    /*


    THESE TESTS ARE FOR THE METHODS IN TOTAL COST CONTROLLER

     */
    @Test
    void testCalculateSalaryByRoleAndTimeframeYearly() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("salary")).thenReturn(50000.0, 60000.0);

        double result = totalCostController.calculateSalaryByRoleAndTimeframe("Manager", "yearly");

        assertEquals(110000.0, result, 0.01, "Total yearly salary should match the sum of salaries.");

        verify(mockConnection).prepareStatement("SELECT salary FROM users WHERE role = ?");
        verify(mockPreparedStatement).setString(1, "Manager");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testCalculateSalaryByRoleAndTimeframeMonthly() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("salary")).thenReturn(60000.0, 72000.0);

        double result = totalCostController.calculateSalaryByRoleAndTimeframe("Manager", "monthly");

        assertEquals(11000.0, result, 0.01, "Monthly salary should be total divided by 12.");
        verify(mockConnection).prepareStatement("SELECT salary FROM users WHERE role = ?");
        verify(mockPreparedStatement).setString(1, "Manager");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testCalculateSalaryByRoleAndTimeframeWeekly() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("salary")).thenReturn(52000.0, 78000.0);

        double result = totalCostController.calculateSalaryByRoleAndTimeframe("Manager", "weekly");

        assertEquals(2500.0, result, 0.01, "Weekly salary should be total divided by 52.");

        verify(mockConnection).prepareStatement("SELECT salary FROM users WHERE role = ?");
        verify(mockPreparedStatement).setString(1, "Manager");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testCalculateSalaryByRoleAndTimeframeDaily() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("salary")).thenReturn(36500.0, 36500.0);

        double result = totalCostController.calculateSalaryByRoleAndTimeframe("Manager", "daily");

        assertEquals(200.0, result, 0.01, "Daily salary should be the total divided by 365.");

        verify(mockConnection).prepareStatement("SELECT salary FROM users WHERE role = ?");
        verify(mockPreparedStatement).setString(1, "Manager");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testCalculateSalaryByRoleAndTimeframeInvalidTimeframe() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                totalCostController.calculateSalaryByRoleAndTimeframe("Admin", "hourly"));

        assertEquals("Invalid timeframe specified", exception.getMessage());
    }

    @Test
    void testCalculateSalaryByRoleAndTimeframeSQLException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
        double result = totalCostController.calculateSalaryByRoleAndTimeframe("Admin", "yearly");
        assertEquals(0.0, result, "Should return 0.0 when a SQLException occurs.");
        verify(mockConnection).prepareStatement("SELECT salary FROM users WHERE role = ?");
        verify(mockPreparedStatement).setString(1, "Admin");
        verify(mockPreparedStatement).executeQuery();
    }


    @Test
    void testGetTotalBillCostsAddedOnDateNormalCase() throws SQLException {
        LocalDate date = LocalDate.of(2025, 1, 18);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(150.0, 200.0);
        List<Double> result = totalCostController.getTotalBillCostsAddedOnDate(date);
        assertEquals(2, result.size(), "List should contain two total prices.");
        assertTrue(result.contains(150.0), "List should contain 150.0.");
        assertTrue(result.contains(200.0), "List should contain 200.0.");
        verify(mockPreparedStatement).setDate(1, Date.valueOf(date));
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, times(3)).next();
        verify(mockResultSet, times(2)).getDouble("total_price");
    }

    @Test
    void testGetTotalBillCostsAddedOnDateEmptyResultSet() throws SQLException {
        LocalDate date = LocalDate.of(2025, 1, 20);
        when(mockResultSet.next()).thenReturn(false);
        List<Double> result = totalCostController.getTotalBillCostsAddedOnDate(date);
        assertTrue(result.isEmpty(), "List should be empty when no data is found.");
        verify(mockPreparedStatement).setDate(1, Date.valueOf(date));
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
    }

    @Test
    void testGetTotalBillCostsAddedOnDateSQLException() throws SQLException {
        LocalDate date = LocalDate.of(2025, 1, 20);
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Mock SQL error"));
        List<Double> result = totalCostController.getTotalBillCostsAddedOnDate(date);
        assertTrue(result.isEmpty(), "List should be empty when SQLException occurs.");
        verify(mockConnection).prepareStatement(anyString());
    }

    @Test
    void testGetTotalBillCostsAddedInRangeValidRangeWithResults() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0);
        List<Double> result = totalCostController.getTotalBillCostsAddedInRange(startDate, endDate);
        assertEquals(2, result.size(), "List should contain two total prices.");
        assertTrue(result.contains(100.0), "List should contain 100.0.");
        assertTrue(result.contains(200.0), "List should contain 200.0.");
        verify(mockPreparedStatement).setDate(1, Date.valueOf(startDate));
        verify(mockPreparedStatement).setDate(2, Date.valueOf(endDate));
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, times(3)).next();
        verify(mockResultSet, times(2)).getDouble("total_price");
    }

    @Test
    void testGetTotalBillCostsAddedInRangeValidRangeNoResults() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        when(mockResultSet.next()).thenReturn(false);
        List<Double> result = totalCostController.getTotalBillCostsAddedInRange(startDate, endDate);
        assertTrue(result.isEmpty(), "List should be empty when no data is found.");
        verify(mockPreparedStatement).setDate(1, Date.valueOf(startDate));
        verify(mockPreparedStatement).setDate(2, Date.valueOf(endDate));
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
    }

    @Test
    void testGetTotalBillCostsAddedInRangeSingleDayRange() throws SQLException {
        LocalDate singleDate = LocalDate.of(2025, 1, 15);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(250.0);
        List<Double> result = totalCostController.getTotalBillCostsAddedInRange(singleDate, singleDate);
        assertEquals(1, result.size(), "List should contain one total price.");
        assertTrue(result.contains(250.0), "List should contain 250.0.");
        verify(mockPreparedStatement).setDate(1, Date.valueOf(singleDate));
        verify(mockPreparedStatement).setDate(2, Date.valueOf(singleDate));
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, times(2)).next();
        verify(mockResultSet).getDouble("total_price");
    }

    @Test
    void testGetTotalBillCostsAddedInRangeInvalidDateRange() {
        LocalDate startDate = LocalDate.of(2025, 1, 31);
        LocalDate endDate = LocalDate.of(2025, 1, 1);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> totalCostController.getTotalBillCostsAddedInRange(startDate, endDate));
        assertEquals("startDate cannot be after endDate.", exception.getMessage());
    }

    @Test
    void testGetTotalBillCostsAddedInRangeSQLException() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Mock SQL error"));
        List<Double> result = totalCostController.getTotalBillCostsAddedInRange(startDate, endDate);
        assertTrue(result.isEmpty(), "List should be empty when SQLException occurs.");
        verify(mockConnection).prepareStatement(anyString());
    }

    /*

    THESE TESTS ARE FOR METHODS IN THE LOGIN CONTROLLER CLASS

     */

    @Test
    void testValidateCredentialsValidCredentials() throws SQLException {
        String username = "librarian1";
        String enteredPassword = "librarian1";
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn("correctPassword");
        when(mockResultSet.getString("role")).thenReturn("Librarian");
        User result = loginController.validateCredentials(username, enteredPassword);
        assertNotNull(result, "User should be returned for valid credentials.");
        assertEquals("Librarian", result.getRole(), "User role should be Librarian.");
        assertEquals(username, result.getUsername(), "Username should match.");
        assertEquals(enteredPassword, result.getPassword(), "Password should match.");
    }

    @Test
    void testValidateCredentialsInvalidPassword() throws SQLException {
        String username = "librarian1";
        String enteredPassword = "librarian";
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn("Librarian1");
        User result = loginController.validateCredentials(username, enteredPassword);
        assertNull(result, "User should not be returned for invalid password.");
    }

    @Test
    void testValidateCredentialsNonExistentUser() throws SQLException {
        String username = "librarian50";
        String enteredPassword = "librarian100";
        when(mockResultSet.next()).thenReturn(false);
        User result = loginController.validateCredentials(username, enteredPassword);
        assertNull(result, "User should not be returned if username doesn't exist.");
    }

    @Test
    void testValidateCredentialsSQLException() throws SQLException {
        String username = "librarian1";
        String enteredPassword = "librarian1";
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Test SQL Exception"));
        User result = loginController.validateCredentials(username, enteredPassword);
        assertNull(result, "User should not be returned if an SQL exception occurs.");
    }

    @Test
    void testLoginSuccessful() {
        String username = "admin1";
        String password = "admin1";
        when(loginController.validateCredentials(username, password)).thenReturn(mockUser);
        when(mockUser.getRole()).thenReturn("admin");
        doNothing().when(mockDashboardView).start(mockStage);
        loginController.handleLogin(mockStage, username, password);
        verify(mockDashboardView, times(1)).start(mockStage);
        verify(mockUser, times(1)).getRole();
        assertTrue(true);
    }

    @Test
    void testLoginFailedInvalidCredentials() {
        String username = "admin100";
        String password = "ad";
        when(loginController.validateCredentials(username, password)).thenReturn(null);
        loginController.handleLogin(mockStage, username, password);
        verify(mockDashboardView, never()).start(mockStage);
    }
}
