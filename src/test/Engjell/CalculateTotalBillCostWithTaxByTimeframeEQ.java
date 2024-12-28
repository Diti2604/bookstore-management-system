package test.Engjell;

import Controller.TotalCostController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CalculateTotalBillCostWithTaxByTimeframeEQ{
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
    void testDailyTimeframeWithValidResultSet() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0);

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("daily");

        assertEquals(20.0, taxAmount, 0.01);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testWeeklyTimeframeWithMultipleRecords() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(150.0, 250.0);

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("weekly");

        assertEquals(80.0, taxAmount, 0.01);
    }

    @Test
    void testMonthlyTimeframeWithEmptyResultSet() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("monthly");

        assertEquals(0.0, taxAmount, 0.01);
    }

    @Test
    void testYearlyTimeframeWithNegativeBill() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(-200.0);

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("yearly");

        assertEquals(-40.0, taxAmount, 0.01);
    }

    @Test
    void testInvalidTimeframeThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                totalCostController.calculateTotalBillCostWithTaxByTimeframe("hourly")
        );
    }

    @Test
    void testZeroTotalBillAmount() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(0.0);

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("daily");

        assertEquals(0.0, taxAmount, 0.01);
    }
}
