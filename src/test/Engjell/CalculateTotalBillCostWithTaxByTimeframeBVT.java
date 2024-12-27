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
import static org.mockito.Mockito.*;

public class CalculateTotalBillCostWithTaxByTimeframeBVT {
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
    void testCalculateTotalBillCostWithTaxByTimeframe_Daily_LowerBound() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0);

        LocalDate today = LocalDate.now();

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("daily");

        assertEquals(20.0, taxAmount, 0.01);

        verify(mockPreparedStatement).setDate(1, java.sql.Date.valueOf(today));
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testCalculateTotalBillCostWithTaxByTimeframe_Daily_UpperBound() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0); // Two bills

        LocalDate today = LocalDate.now();

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("daily");

        assertEquals(60.0, taxAmount, 0.01);

        verify(mockPreparedStatement).setDate(1, java.sql.Date.valueOf(today));
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testCalculateTotalBillCostWithTaxByTimeframe_Weekly_LowerBound() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0);

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("weekly");

        assertEquals(20.0, taxAmount, 0.01);

        verify(mockPreparedStatement).setDate(1, java.sql.Date.valueOf(startOfWeek));
        verify(mockPreparedStatement).setDate(2, java.sql.Date.valueOf(endOfWeek));
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testCalculateTotalBillCostWithTaxByTimeframe_Weekly_UpperBound() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0);

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("weekly");

        assertEquals(60.0, taxAmount, 0.01);

        verify(mockPreparedStatement).setDate(1, java.sql.Date.valueOf(startOfWeek));
        verify(mockPreparedStatement).setDate(2, java.sql.Date.valueOf(endOfWeek));
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testCalculateTotalBillCostWithTaxByTimeframe_Monthly_LowerBound() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0);

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("monthly");

        assertEquals(20.0, taxAmount, 0.01);

        verify(mockPreparedStatement).setDate(1, java.sql.Date.valueOf(startOfMonth));
        verify(mockPreparedStatement).setDate(2, java.sql.Date.valueOf(endOfMonth));
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testCalculateTotalBillCostWithTaxByTimeframe_Monthly_UpperBound() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0);

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("monthly");

        assertEquals(60.0, taxAmount, 0.01);

        verify(mockPreparedStatement).setDate(1, java.sql.Date.valueOf(startOfMonth));
        verify(mockPreparedStatement).setDate(2, java.sql.Date.valueOf(endOfMonth));
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testCalculateTotalBillCostWithTaxByTimeframe_Yearly_LowerBound() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate startOfYear = today.withDayOfYear(1);
        LocalDate endOfYear = startOfYear.plusYears(1).minusDays(1);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0);

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("yearly");

        assertEquals(20.0, taxAmount, 0.01);

        verify(mockPreparedStatement).setDate(1, java.sql.Date.valueOf(startOfYear));
        verify(mockPreparedStatement).setDate(2, java.sql.Date.valueOf(endOfYear));
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testCalculateTotalBillCostWithTaxByTimeframe_Yearly_UpperBound() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate startOfYear = today.withDayOfYear(1);
        LocalDate endOfYear = startOfYear.plusYears(1).minusDays(1);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0);

        double taxAmount = totalCostController.calculateTotalBillCostWithTaxByTimeframe("yearly");

        assertEquals(60.0, taxAmount, 0.01);

        verify(mockPreparedStatement).setDate(1, java.sql.Date.valueOf(startOfYear));
        verify(mockPreparedStatement).setDate(2, java.sql.Date.valueOf(endOfYear));
        verify(mockPreparedStatement, times(1)).executeQuery();
    }
}
