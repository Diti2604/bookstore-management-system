package test.Bori;

import Controller.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.sql.*;
import org.mockito.Mockito;
import  static org.mockito.Mockito.*;


public class CalculateSalaryByRoleAndTimeframeEQ {
    @Test
    void testCalculateSalaryByRoleAndTimeframeWithValidRoleAndDailyTimeframe() throws SQLException {

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getDouble("salary")).thenReturn(73000.0); // Mocked salary

        TotalCostController controller = new TotalCostController(mockConnection);


        double result = controller.calculateSalaryByRoleAndTimeframe("Manager", "daily");

        assertEquals(200.0, result, 0.01);
        assertNotEquals(400.0, result, 0.01);

    }
    @Test
    void testInvalidArgumentExceptionCalculateSalaryByRoleAndTimeframeWithInvalidRoleAndDailyTimeframe() throws SQLException {

        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        TotalCostController controller = new TotalCostController(mockConnection);

        assertThrows(IllegalArgumentException.class, () -> controller.calculateSalaryByRoleAndTimeframe("Manager", "invalid"),"Expected Illegal Argument exception thrown");
    }

}
