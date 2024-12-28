package test.Engjell;

import Controller.TotalCostController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CalculateTotalBillCostWithTaxByTimeframeCoverageTest {
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private TotalCostController totalCostController;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        totalCostController = new TotalCostController(mockConnection);
    }

    @Test
    void testStatementCoverage_ValidDailyTimeframe() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0);

        double result = totalCostController.calculateTotalBillCostWithTaxByTimeframe("daily");
        assertEquals(20.0, result, 0.01);

        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testBranchCoverage_InvalidTimeframe() {
        String invalidTimeframe = "invalid_timeframe"; // Use an unsupported timeframe
        TotalCostController controller = new TotalCostController();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            controller.calculateTotalBillCostWithTaxByTimeframe(invalidTimeframe);
        });

        assertEquals("Invalid timeframe specified", exception.getMessage(),
                "Exception message should match the expected output");
    }


    @Test
    void testConditionCoverage_WeeklyMultipleRecords() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(150.0, 250.0); // Two bills

        double result = totalCostController.calculateTotalBillCostWithTaxByTimeframe("weekly");
        assertEquals(80.0, result, 0.01);

        verify(mockPreparedStatement).setDate(1, java.sql.Date.valueOf(startOfWeek));
        verify(mockPreparedStatement).setDate(2, java.sql.Date.valueOf(endOfWeek));
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testMCDC_ConditionTesting() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        // Case 1: No records
        reset(mockPreparedStatement, mockResultSet); // Reset the mocks before each case
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        double resultNoRecords = totalCostController.calculateTotalBillCostWithTaxByTimeframe("monthly");
        assertEquals(0.0, resultNoRecords, 0.01);

        verify(mockPreparedStatement, times(1)).setDate(1, java.sql.Date.valueOf(startOfMonth));

        // Case 2: Single record
        reset(mockPreparedStatement, mockResultSet); // Reset the mocks before each case
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(200.0);
        double resultSingleRecord = totalCostController.calculateTotalBillCostWithTaxByTimeframe("monthly");
        assertEquals(40.0, resultSingleRecord, 0.01);

        verify(mockPreparedStatement, times(1)).setDate(1, java.sql.Date.valueOf(startOfMonth));

        // Case 3: Multiple records
        reset(mockPreparedStatement, mockResultSet); // Reset the mocks before each case
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(150.0, 250.0);
        double resultMultipleRecords = totalCostController.calculateTotalBillCostWithTaxByTimeframe("monthly");
        assertEquals(80.0, resultMultipleRecords, 0.01);

        verify(mockPreparedStatement, times(1)).setDate(1, java.sql.Date.valueOf(startOfMonth));
    }
}
