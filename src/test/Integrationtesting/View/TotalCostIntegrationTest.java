package test.Integrationtesting.View;

import Controller.TotalCostController;
import View.TotalCostView;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TotalCostIntegrationTest extends ApplicationTest {

    private Connection testConn;

    @Override
    public void start(Stage stage) throws Exception {
        testConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookstore", "root", "IndritFerati2604!");
        testConn.setAutoCommit(false);

        TotalCostController controller = new TotalCostController(testConn);
        TotalCostView view = new TotalCostView(controller);
        view.start(stage);
    }

    @AfterEach
    public void rollback() throws SQLException {
        testConn.rollback();
    }

    @AfterAll
    public void closeConnection() throws SQLException {
        testConn.close();
    }

    @Test
    @Order(1)
    public void testCalculateDailyCost() {
        ComboBox<String> timeframeComboBox = lookup("#timeframeComboBox").queryComboBox();
        clickOn(timeframeComboBox).clickOn("Daily");
        Button calculateButton = lookup("#calculateButton").queryButton();
        clickOn(calculateButton);
        TableView<?> tableView = lookup("#totalCostTable").queryTableView();
        assertFalse(tableView.getItems().isEmpty(), "TableView should not be empty after calculation.");
        var firstRow = tableView.getItems().get(0);
        assertNotNull(firstRow, "First row should not be null.");
    }

    @Test
    @Order(2)
    public void testCalculateWeeklyCost() {
        ComboBox<String> timeframeComboBox = lookup("#timeframeComboBox").queryComboBox();
        clickOn(timeframeComboBox).clickOn("Weekly");
        Button calculateButton = lookup("#calculateButton").queryButton();
        clickOn(calculateButton);
        TableView<?> tableView = lookup("#totalCostTable").queryTableView();
        assertFalse(tableView.getItems().isEmpty(), "TableView should not be empty after calculation.");
    }

    @Test
    @Order(3)
    public void testInvalidTimeframe() {
        ComboBox<String> timeframeComboBox = lookup("#timeframeComboBox").queryComboBox();
        interact(() -> timeframeComboBox.getItems().add("Invalid"));
        clickOn(timeframeComboBox).clickOn("Invalid");
        Button calculateButton = lookup("#calculateButton").queryButton();
        clickOn(calculateButton);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new TotalCostController(testConn).calculateSalaryByRoleAndTimeframe("Administrator", "Invalid");
        });
    }
}
