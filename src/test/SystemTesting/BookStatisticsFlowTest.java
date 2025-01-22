package test.SystemTesting;

import Controller.LoginController;
import Model.Book;
import Model.User;
import View.LoginView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;

import static javafx.beans.binding.Bindings.select;
import static org.junit.jupiter.api.Assertions.*;

public class BookStatisticsFlowTest extends ApplicationTest {
    private LoginController loginController;
    private LoginView loginView;
    private Stage stage;
    private Connection connection;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        loginView = new LoginView();
        loginView.start(stage); // Initial view to start with
    }

    @BeforeEach
    public void setUp() {
        loginController = new LoginController(connection);
    }

    @Test
    public void testBookStatistics() {
        // Simulate entering credentials and clicking login button
        clickOn(".text-field").write("admin1"); // Enter username
        clickOn(".password-field").write("admin1"); // Enter password
        clickOn(".button"); // Click login button

        WaitForAsyncUtils.waitForFxEvents();

        // Verify login success
        User user = loginController.validateCredentials("admin1", "admin1");
        assertNotNull(user, "Login should succeed with correct credentials");

        // Navigate to Create Bill
        clickOn("#actionComboBox"); // Click on the combo box
        clickOn("Book Statistics"); // Select "Create Bill" option

        WaitForAsyncUtils.waitForFxEvents();

        // Add assertions to verify that the Create Bill view is displayed
        // For example, check for the presence of a specific element in the Create Bill view
        assertTrue(lookup("Book Statistics").tryQuery().isPresent(), "Book Statistics view should be displayed");

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#statisticsComboBox");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Total");

        assertTrue(lookup("#bookStatisticsTable").tryQuery().isPresent(), "The book statistics table should be present.");
        assertTrue(lookup("#bookStatisticsTable").queryTableView().getItems().size() > 0, "The book statistics table should have rows.");
    }

    @Test
    public void testBookStatisticsDaily() {
        // Simulate entering credentials and clicking login button
        clickOn(".text-field").write("admin1"); // Enter username
        clickOn(".password-field").write("admin1"); // Enter password
        clickOn(".button"); // Click login button

        WaitForAsyncUtils.waitForFxEvents();

        // Verify login success
        User user = loginController.validateCredentials("admin1", "admin1");
        assertNotNull(user, "Login should succeed with correct credentials");

        // Navigate to Create Bill
        clickOn("#actionComboBox"); // Click on the combo box
        clickOn("Book Statistics"); // Select "Create Bill" option

        WaitForAsyncUtils.waitForFxEvents();

        // Add assertions to verify that the Create Bill view is displayed
        // For example, check for the presence of a specific element in the Create Bill view
        assertTrue(lookup("Book Statistics").tryQuery().isPresent(), "Book Statistics view should be displayed");

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#statisticsComboBox");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Daily");

        assertTrue(lookup("#bookStatisticsTable").tryQuery().isPresent(), "The book statistics table should be present.");
        assertTrue(lookup("#bookStatisticsTable").queryTableView().getItems().size() > 0, "The book statistics table should have rows.");
    }
    @Test
    public void testBookStatisticsMonthly() {
        // Simulate entering credentials and clicking login button
        clickOn(".text-field").write("admin1"); // Enter username
        clickOn(".password-field").write("admin1"); // Enter password
        clickOn(".button"); // Click login button

        WaitForAsyncUtils.waitForFxEvents();

        // Verify login success
        User user = loginController.validateCredentials("admin1", "admin1");
        assertNotNull(user, "Login should succeed with correct credentials");

        // Navigate to Create Bill
        clickOn("#actionComboBox"); // Click on the combo box
        clickOn("Book Statistics"); // Select "Create Bill" option

        WaitForAsyncUtils.waitForFxEvents();

        // Add assertions to verify that the Create Bill view is displayed
        // For example, check for the presence of a specific element in the Create Bill view
        assertTrue(lookup("Book Statistics").tryQuery().isPresent(), "Book Statistics view should be displayed");

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#statisticsComboBox");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Monthly");

        assertTrue(lookup("#bookStatisticsTable").tryQuery().isPresent(), "The book statistics table should be present.");
        assertTrue(lookup("#bookStatisticsTable").queryTableView().getItems().size() > 0, "The book statistics table should have rows.");
    }
}