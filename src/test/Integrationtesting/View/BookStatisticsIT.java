package test.Integrationtesting.View;

import View.BookStatisticsView;
import Controller.BookStatisticsController;
import Model.Book;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxRobot.*;

public class BookStatisticsIT extends ApplicationTest {

    private Connection conn;
    private BookStatisticsController statisticsController;

    @Override
    public void start(Stage stage) throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookstore", "root", "IndritFerati2604!");
        conn.setAutoCommit(false);

        statisticsController = new BookStatisticsController(conn);
        BookStatisticsView bookStatisticsView=new BookStatisticsView(statisticsController);
        bookStatisticsView.start(stage);

        populateTestDatabase();
    }

    private void populateTestDatabase() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT INTO bills (librarian_username,book_title, quantity, total_price, created_at) " +
                    "VALUES ('test_lib','Test Book', 10, 100.0, NOW())");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBookStatisticsView_ShowsCorrectDataWhenSelectingTimeline() {
        ComboBox<String> statisticsComboBox = lookup("#statisticsComboBox").query();
        assertNotNull(statisticsComboBox, "ComboBox should be available in the scene");
        clickOn(statisticsComboBox);
        write("Daily");
        sleep(1000);

        ObservableList<Book> books = statisticsController.getDailyStatistics();

        assertNotNull(books, "Daily statistics should be retrieved");
        assertTrue(books.stream().anyMatch(book ->
                        book.getTitle().equals("Test Book") &&
                                book.getStock() == 10 &&
                                book.getSellingPrice() == 100.0),
                "Test Book should be displayed with correct statistics");
    }

    @Test
    public void testDatabaseRollback() throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM bills WHERE book_title = 'Test Book'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkQuery)) {
            if (rs.next()) {
                int count = rs.getInt(1);
                assertEquals(1, count, "There should be 1 row with 'Test Book'");
            }
        }
        conn.rollback();
    }

    @Override
    public void stop() throws SQLException {
        if (conn != null) {
            conn.rollback();
            conn.close();
        }
    }
}
