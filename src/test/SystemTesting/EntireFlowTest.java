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

public class EntireFlowTest extends ApplicationTest {
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
    public void testCreateBill() {
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
        clickOn("Create Bill"); // Select "Create Bill" option

        WaitForAsyncUtils.waitForFxEvents();

        // Add assertions to verify that the Create Bill view is displayed
        // For example, check for the presence of a specific element in the Create Bill view
        assertTrue(lookup("Create Bill").tryQuery().isPresent(), "Create Bill view should be displayed");

        WaitForAsyncUtils.waitForFxEvents();

        // Select an ISBN from the ComboBox
        clickOn("#isbnComboBox"); // Click on the ComboBox

        clickOn("9780061120044"); // Simulate typing the ISBN
        press(javafx.scene.input.KeyCode.ENTER).release(javafx.scene.input.KeyCode.ENTER); // Confirm selection

        // Click "Load Book Info" button
        clickOn("Load Book Info");
        WaitForAsyncUtils.waitForFxEvents();

        // Enter a quantity
        clickOn("#quantityField"); // Click on the quantity field
        eraseText(1);
        write("1"); // Enter the quantity

        // Click "Add Book" button
        clickOn("Add Book");
        WaitForAsyncUtils.waitForFxEvents();


        // Click "Create Bill" button
        clickOn("Create Bill");
        WaitForAsyncUtils.waitForFxEvents();

        // Verify the bill is created and the table is cleared
        assertEquals(17, lookup("#bookTableView .table-row-cell").queryAll().size(), "The table should be cleared after creating the bill");

        press(javafx.scene.input.KeyCode.ALT).press(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.ALT);
        WaitForAsyncUtils.waitForFxEvents();


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

        press(javafx.scene.input.KeyCode.ALT).press(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.ALT);
        WaitForAsyncUtils.waitForFxEvents();

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

        press(javafx.scene.input.KeyCode.ALT).press(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.ALT);
        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Create Bill
        clickOn("#actionComboBox"); // Click on the combo box
        clickOn("Manage Employees"); // Select "Create Bill" option

        WaitForAsyncUtils.waitForFxEvents();

        // Add assertions to verify that the Create Bill view is displayed
        // For example, check for the presence of a specific element in the Create Bill view
        assertTrue(lookup("Manage Employees").tryQuery().isPresent(), "Manage Employees view should be displayed");

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#manageEmployeesComboBox");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Register");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#usernameTextField").write("admin_test");
        clickOn("#passwordField").write("Test1");
        clickOn("#nameTextField").write("test");
        clickOn("#birthdayDatePicker").write("1/1/2000");
        clickOn("#phoneTextField").write("0682341220");
        clickOn("#emailTextField").write("test@gmail.com");
        clickOn("#salaryTextField").write("50000");
        clickOn("#roleComboBox").clickOn("Administrator");
        clickOn("#registerButton");

        // Verify alert popup
        // Verify the alert is present
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");

        // Verify alert popup
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");

        press(javafx.scene.input.KeyCode.ALT).press(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.ALT);
        WaitForAsyncUtils.waitForFxEvents();
        press(javafx.scene.input.KeyCode.ALT).press(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.ALT);
        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Create Bill
        clickOn("#actionComboBox"); // Click on the combo box
        clickOn("Total Cost"); // Select "Create Bill" option

        WaitForAsyncUtils.waitForFxEvents();

        // Add assertions to verify that the Create Bill view is displayed
        // For example, check for the presence of a specific element in the Create Bill view
        assertTrue(lookup("Total Cost").tryQuery().isPresent(), "Total Cost view should be displayed");

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#timeframeComboBox");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Daily");

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#calculateButton");

        assertTrue(lookup("#costTableView").tryQuery().isPresent(), "The book statistics table should be present.");
        assertTrue(lookup("#costTableView").queryTableView().getItems().size() > 0, "The book statistics table should have rows.");

        press(javafx.scene.input.KeyCode.ALT).press(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.ALT);
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#actionComboBox"); // Click on the action combo box
        clickOn("Add Book"); // Select "Add Book" option

        WaitForAsyncUtils.waitForFxEvents();

        // Add assertions to verify that the Add Book view is displayed
        assertTrue(lookup("Add Book").tryQuery().isPresent(), "Add Book view should be displayed");

        WaitForAsyncUtils.waitForFxEvents();

        // Fill in the book details
        clickOn("#bookUrlField").write("https://images");
        clickOn("#bookNameField").write("To Kill a Mockingbird");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#categoryComboBox").clickOn("Fantasy"); // Enter category
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#isbnField").write("9780061120084"); // Enter ISBN
        clickOn("#authorField").write("Harper Lee"); // Enter author
        clickOn("#sellingPriceField").write("10.99"); // Enter price


        // Click "Add Book" button
        clickOn("#addBookButton");
        WaitForAsyncUtils.waitForFxEvents();

        // Verify alert popup
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");

        press(javafx.scene.input.KeyCode.ALT).press(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.ALT);
        WaitForAsyncUtils.waitForFxEvents();


        press(javafx.scene.input.KeyCode.ALT).press(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.F4).release(javafx.scene.input.KeyCode.ALT);
        WaitForAsyncUtils.waitForFxEvents();

    }

}