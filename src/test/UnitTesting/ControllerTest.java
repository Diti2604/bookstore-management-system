package test.UnitTesting;

import Controller.AddBookController;
import Controller.CreateBillController;
import Model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class ControllerTest {
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private AddBookController controller;
    private CreateBillController createBillController;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        controller = new AddBookController(mockConnection);
        createBillController = new CreateBillController(mockConnection);
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
        assert(isbns.isEmpty());
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
        assert(isbns.size() == 6);
        assert(isbns.get(0).equals("9781-23-4567"));
        assert(isbns.get(5).equals("9781-50-3290"));
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
        when(mockResultSet.getString("ISBN")).thenReturn("9781-23-4567", "9780-45-1524", "9780-74-3273", "9781-50-3280", "9781-50-3290","9780-31-6769", "9780-06-0850", "9780-54-7928", "9780-34-5339", "1234-56-7890");
        List<String> isbns = createBillController.getAllISBNsOrderedByStock();
        assert(isbns.size() == 10);
    }

    @Test
    void testGetAllISBNsOrderedByStockSQLException() throws SQLException {
        // Tested for a database connectivity error
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
        List<String> isbns = createBillController.getAllISBNsOrderedByStock();
        assert(isbns.isEmpty());
    }
}
