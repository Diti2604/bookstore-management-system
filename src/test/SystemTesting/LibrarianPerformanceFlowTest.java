package test.SystemTesting;

import Controller.LoginController;
import Model.Book;
import Model.User;
import View.LoginView;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;

import static javafx.beans.binding.Bindings.select;
import static org.junit.jupiter.api.Assertions.*;

public class LibrarianPerformanceFlowTest extends ApplicationTest {
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
        // Ensuring TestFX is initialized
        Platform.runLater(() -> {
        });
    }


    @Test
    public void testLibrarianPerformance() {
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
        clickOn("Check Librarian Performance"); // Select "Create Bill" option

        WaitForAsyncUtils.waitForFxEvents();

        // Add assertions to verify that the Create Bill view is displayed
        // For example, check for the presence of a specific element in the Create Bill view
        assertTrue(lookup("Check Librarian Performance").tryQuery().isPresent(), "Check Librarian Performance view should be displayed");

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#librarianComboBox");
        clickOn("librarian2");

        clickOn("#startDatePicker");
        WaitForAsyncUtils.waitForFxEvents();
        write("1/1/2024");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#endDatePicker");
        WaitForAsyncUtils.waitForFxEvents();
        write("1/31/2025");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#showDataButton");

        String expectedText = "Sales Data for: librarian2"; // Adjust this according to the actual output data
        assertTrue(lookup("#salesDataLabel").tryQuery().isPresent(), "Check Librarian Performance view should be displayed");
    }
}