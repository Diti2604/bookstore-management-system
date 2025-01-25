package test.Integrationtesting.Controller;

import Controller.CreateBillController;
import Model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CreateBillControllerIT {

    private CreateBillController controller;
    private Connection mockConnection;

    @BeforeEach
    public void setUp() {
        mockConnection = mock(Connection.class);
        controller = new CreateBillController(mockConnection);
    }

    @Test
    public void testGetBookByISBN_ValidISBN() throws SQLException {
        // Arrange
        String isbn = "1234-56-7890";
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("title")).thenReturn("Test Book");
        when(mockRs.getString("category")).thenReturn("Fiction");
        when(mockRs.getDouble("selling_price")).thenReturn(20.0);
        when(mockRs.getString("author")).thenReturn("John Doe");
        when(mockRs.getInt("stock")).thenReturn(10);

        // Act
        Book book = controller.getBookByISBN(isbn);

        // Assert
        assertNotNull(book);
        assertEquals("Test Book", book.getTitle());
        assertEquals("Fiction", book.getCategory());
        assertEquals(20.0, book.getSellingPrice());
        assertEquals("John Doe", book.getAuthor());
        assertEquals(10, book.getStock());
    }

    @Test
    public void testGetBookByISBN_InvalidISBN() throws SQLException {
        // Arrange
        String isbn = "9999-99-9999";
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);

        // Act
        Book book = controller.getBookByISBN(isbn);

        // Assert
        assertNull(book);
    }

    @Test
    public void testUpdateBookStock() throws SQLException {
        // Arrange
        String isbn = "1234-56-7890";
        int newStock = 5;
        PreparedStatement mockStmt = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);

        // Act
        controller.updateBookStock(isbn, newStock);

        // Assert
        verify(mockStmt, times(1)).setInt(1, newStock);
        verify(mockStmt, times(1)).setString(2, isbn);
        verify(mockStmt, times(1)).executeUpdate();
    }

    @Test
    public void testSaveBillToDatabase() throws SQLException {
        // Arrange
        String librarianUsername = "Librarian1";
        String title = "Test Book";
        int quantity = 2;
        double totalPrice = 40.0;
        PreparedStatement mockStmt = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);

        // Act
        controller.saveBillToDatabase(librarianUsername, title, quantity, totalPrice);

        // Assert
        verify(mockStmt, times(1)).setString(1, librarianUsername);
        verify(mockStmt, times(1)).setString(2, title);
        verify(mockStmt, times(1)).setInt(3, quantity);
        verify(mockStmt, times(1)).setDouble(4, totalPrice);
        verify(mockStmt, times(1)).executeUpdate();
    }

    @Test
    public void testGetAllISBNsOrderedByStock() throws SQLException {
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        List<String> isbns = List.of("1234-56-7890", "9876-54-3210");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, true, false);
        when(mockRs.getString("ISBN")).thenReturn("1234-56-7890", "9876-54-3210");
        List<String> result = controller.getAllISBNsOrderedByStock();
        assertEquals(isbns, result);
    }
}
