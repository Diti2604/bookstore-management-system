package test.Bori;

import Controller.AddBookController;
import Model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.mockito.Mockito.*;


public class SaveBookBVT {
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private AddBookController addBookController;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        addBookController = new AddBookController(mockConnection);
    }

    @Test
    void testSellingPriceAtLowerBoundary() throws SQLException {

        when(mockResultSet.next()).thenReturn(false);

        addBookController.saveBook(new Book("12","foo","idk",1.0D,"Nim Janim",4,"book1.jpg"));

        // Assert: Verify that the method proceeds with saving the book
        verify(mockPreparedStatement, times(1)).setString(1, "foo"); // CheckBook query
        verify(mockPreparedStatement, times(1)).executeUpdate(); // InsertBook query
    }

    @Test
    void testSellingPriceAtUpperBoundary() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        addBookController.saveBook(new Book("12","foo","idk",100000.0D,"Nim Janim",4,"book1.jpg"));

        verify(mockPreparedStatement, times(1)).setString(1, "foo"); // CheckBook query
        verify(mockPreparedStatement, times(1)).executeUpdate(); // InsertBook query
    }

    @Test
    void testSellingPriceBelowLowerBoundary() throws SQLException {

        addBookController.saveBook(new Book("12","foo","idk",-1.0D,"Nim Janim",4,"book1.jpg"));

        verify(mockPreparedStatement, never()).executeUpdate(); // No DB action should occur
    }

    @Test
    void testSellingPriceAboveUpperBoundary() throws SQLException {


        addBookController.saveBook(new Book("12","foo","idk",100001.0D,"Nim Janim",4,"book1.jpg"));

        verify(mockPreparedStatement, never()).executeUpdate(); // No DB action should occur
    }

}
