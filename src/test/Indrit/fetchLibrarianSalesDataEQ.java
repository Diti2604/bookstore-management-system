package test.Indrit;

import View.MainDashboardView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Controller.CheckLibrarianPerformanceController;

import java.sql.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class fetchLibrarianSalesDataEQ {
//    INVALID                       VALID                                 INVALID
//    NOT A CORRECT LIBRARIAN      CORRECT NAME, CORRECT DATE FORMAT   WRONG FORMAT OF DATE INPUT/OUTPUT (START > END)

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private CheckLibrarianPerformanceController controller;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        controller = new CheckLibrarianPerformanceController(mockConnection);
    }


    @Test
    void testValidInputs() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("book_title")).thenReturn("Test Book");
        when(mockResultSet.getInt("quantity")).thenReturn(10);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0);
        when(mockResultSet.getDate("created_at")).thenReturn(Date.valueOf(LocalDate.of(2024, 1, 15)));

        String librarianName = "librarian1";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        var result = controller.fetchLibrarianSalesData(librarianName, startDate, endDate);

        assertFalse(result.isEmpty(), "Result should not be empty for valid inputs.");
        assertEquals("Book Title: Test Book, Quantity Sold: 10, Total Price: $100.0, Sale Date: 2024-01-15", result.get(0), "Expected performance data mismatch.");
        verify(mockPreparedStatement, times(1)).executeQuery();
    }


    @Test
    void testInvalidLibrarianName() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        String librarianName = "nonExistent";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        var result = controller.fetchLibrarianSalesData(librarianName, startDate, endDate);

        assertTrue(result.isEmpty(), "Result should be empty for a non-existent librarian.");
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testInvalidDateRange() throws SQLException {
        String librarianName = "librarian1";
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            controller.fetchLibrarianSalesData(librarianName, startDate, endDate);
        });

        assertEquals("Start date cannot be after end date", exception.getMessage());
        verify(mockPreparedStatement, never()).executeQuery();
    }

    @Test
    void testInvalidDateFormat() throws SQLException {
        String librarianName = "librarian1";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            controller.fetchLibrarianSalesData(librarianName, null, null);
        });

        assertEquals("Dates cannot be null", exception.getMessage());
        verify(mockPreparedStatement, never()).executeQuery();
    }

    @Test
    void testNullInputs() throws SQLException {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            controller.fetchLibrarianSalesData(null, null, null);
        });

        assertEquals("Inputs cannot be null", exception.getMessage());
        verify(mockPreparedStatement, never()).executeQuery();
    }
}


