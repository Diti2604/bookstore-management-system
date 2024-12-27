package test.Indrit;

import Controller.CheckLibrarianPerformanceController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class fetchLibrarianSalesData_MCDC {
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
    void testStatementCoverage() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        String username = "librarian1";

        //All statements
        List<String> result = controller.fetchLibrarianSalesData(username, startDate, endDate);
    }

    @Test
    void testBranchCoverage() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        String username = "librarian1";

        //Success path
        List<String> result1 = controller.fetchLibrarianSalesData(username, startDate, endDate);

        //Null input path
        assertThrows(IllegalArgumentException.class, () ->
                controller.fetchLibrarianSalesData(null, startDate, endDate));

        //Invalid date range path
        assertThrows(IllegalArgumentException.class, () ->
                controller.fetchLibrarianSalesData(username, endDate, startDate));
    }


    @Test
    void testConditionCoverage() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        String username = "librarian1";

        //Independent condition checks
        controller.fetchLibrarianSalesData(username, startDate, endDate); // All true
        assertThrows(IllegalArgumentException.class, () ->
                controller.fetchLibrarianSalesData(null, startDate, endDate)); // username null
        assertThrows(IllegalArgumentException.class, () ->
                controller.fetchLibrarianSalesData(username, null, endDate)); // startDate null
        assertThrows(IllegalArgumentException.class, () ->
                controller.fetchLibrarianSalesData(username, startDate, null)); // endDate null
    }

    @Test
    void testMCDC() {
        // Base case - all conditions true
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        String username = "librarian1";

        //Everything valid (T, T, T)
        List<String> result1 = controller.fetchLibrarianSalesData(username, startDate, endDate);

        //Username null (F, T, T)
        assertThrows(IllegalArgumentException.class, () ->
                controller.fetchLibrarianSalesData(null, startDate, endDate));

        //Start date null (T, F, T)
        assertThrows(IllegalArgumentException.class, () ->
                controller.fetchLibrarianSalesData(username, null, endDate));

        //End date null (T, T, F)
        assertThrows(IllegalArgumentException.class, () ->
                controller.fetchLibrarianSalesData(username, startDate, null));

        //Start date after end date
        LocalDate invalidStartDate = LocalDate.of(2024, 2, 1);
        assertThrows(IllegalArgumentException.class, () ->
                controller.fetchLibrarianSalesData(username, invalidStartDate, endDate));
    }


}