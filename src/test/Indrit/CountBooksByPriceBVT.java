package test.Indrit;

import View.MainDashboardView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CountBooksByPriceBVT {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private MainDashboardView dashboardView;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        dashboardView = new MainDashboardView(mockConnection);
    }

    @Test
    void testPriceAtLowerBoundary() throws SQLException {
        int price = 0;
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("book_count")).thenReturn(0);

        int count = dashboardView.countBooksByPrice(price, "Price");

        assertEquals(0, count);
        verify(mockPreparedStatement, times(1)).setInt(1, price);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testPriceAtUpperBoundary() throws SQLException {
        int price = 1000000;
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("book_count")).thenReturn(0);

        int count = dashboardView.countBooksByPrice(price, "Price");

        assertEquals(0, count);
        verify(mockPreparedStatement, times(1)).setInt(1, price);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testPriceBelowLowerBoundary() throws SQLException {
        int price = -1;
        when(mockResultSet.next()).thenReturn(false);

        int count = dashboardView.countBooksByPrice(price, "Price");

        assertEquals(0, count);
        verify(mockPreparedStatement, times(1)).setInt(1, price);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testPriceAboveUpperBoundary() throws SQLException {
        int price = 1000001;
        when(mockResultSet.next()).thenReturn(false);

        int count = dashboardView.countBooksByPrice(price, "Price");

        assertEquals(0, count);
        verify(mockPreparedStatement, times(1)).setInt(1, price);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testNominalPrice() throws SQLException {
        int avgPrice = 25;

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("avg_price")).thenReturn(avgPrice);

        int actualAvgPrice = dashboardView.getAvgPrice();

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("book_count")).thenReturn(10);

        int count = dashboardView.countBooksByPrice(actualAvgPrice, "Price");

        assertEquals(10, count);
        verify(mockPreparedStatement, times(1)).setInt(1, avgPrice);
        verify(mockPreparedStatement, times(2)).executeQuery();
    }
}
