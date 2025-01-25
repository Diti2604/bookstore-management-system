package test.Integrationtesting.View;

import Controller.CheckLibrarianPerformanceController;
import View.CheckLibrarianPerformanceView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import static org.testfx.api.FxRobot.*;

import java.sql.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CheckLibrarianPerformanceIT extends ApplicationTest {

    private Connection conn;

    @Override
    public void start(Stage stage) throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookstore", "root", "IndritFerati2604!");
        conn.setAutoCommit(false);

        CheckLibrarianPerformanceController controller = new CheckLibrarianPerformanceController(conn);

        CheckLibrarianPerformanceView view = new CheckLibrarianPerformanceView(controller);
        view.start(stage);
        cleanupTestData();
        addTestData();
    }

    @BeforeEach
    public void setup() throws SQLException {
        addTestData();
    }

    private void addTestData() throws SQLException {
        String uniqueUsername = "test_" + System.currentTimeMillis();

        try (Statement stmt = this.conn.createStatement()) {
            stmt.executeUpdate("INSERT INTO users (username, role) VALUES ('" + uniqueUsername + "', 'Librarian')");
            stmt.executeUpdate("INSERT INTO bills (book_title, quantity, total_price, created_at, librarian_username) " +
                    "VALUES ('Book A', 5, 50.0, '2025-01-15', '" + uniqueUsername + "')");
            stmt.executeUpdate("INSERT INTO bills (book_title, quantity, total_price, created_at, librarian_username) " +
                    "VALUES ('Book B', 3, 30.0, '2025-01-16', '" + uniqueUsername + "')");
        }
    }
    private void cleanupTestData() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM users WHERE username = 'test_%'");
        }
    }

    @Test
    public void testLibrarianPerformanceView() {
        ComboBox<String> librarianComboBox = lookup(".combo-box").query();
        assertNotNull(librarianComboBox, "ComboBox should be available in the scene");
        clickOn(librarianComboBox);
        write("librarian1");

        DatePicker startDatePicker = lookup(".date-picker").nth(0).query();
        DatePicker endDatePicker = lookup(".date-picker").nth(1).query();

        LocalDate startDate = LocalDate.of(2025, 1, 15);
        LocalDate endDate = LocalDate.of(2025, 1, 16);

        clickOn(startDatePicker);
        write(startDate.toString());
        clickOn(endDatePicker);
        write(endDate.toString());

        clickOn("Show Data");

        // Check if the dataLabel contains the correct sales data
        Label dataLabel = lookup(".label").query();
        assertNotNull(dataLabel, "Data label should be available");

        String expectedText = "Sales Data for: test:\n\n" +
                "Book Title: Book A, Quantity Sold: 5, Total Price: $50.00, Sale Date: 2025-01-15\n" +
                "Book Title: Book B, Quantity Sold: 3, Total Price: $30.00, Sale Date: 2025-01-16";
        assertEquals(expectedText, dataLabel.getText(), "The sales data should match the expected data.");
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (conn != null) {
            conn.rollback();
            conn.close();  // Close the connection after test
        }
    }

}
