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

public class CreateBillFlowTest extends ApplicationTest {
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

        press(javafx.scene.input.KeyCode.CONTROL).press(javafx.scene.input.KeyCode.W).release(javafx.scene.input.KeyCode.CONTROL).release(javafx.scene.input.KeyCode.W);
        WaitForAsyncUtils.waitForFxEvents();
    }



    @Test
    public void testInvalidQuantityAlert() {
        // Simulate login
        clickOn(".text-field").write("admin1"); // Enter username
        clickOn(".password-field").write("admin1"); // Enter password
        clickOn(".button"); // Click login button
        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Create Bill
        clickOn("#actionComboBox"); // Click on the combo box
        clickOn("Create Bill"); // Select "Create Bill" option
        WaitForAsyncUtils.waitForFxEvents();

        // Select an existing ISBN
        clickOn("#isbnComboBox");
        clickOn("9780061120044");// Existing ISBN
        press(javafx.scene.input.KeyCode.ENTER).release(javafx.scene.input.KeyCode.ENTER); // Confirm selection
        clickOn("Load Book Info");
        WaitForAsyncUtils.waitForFxEvents();
        // Enter an invalid quantity
        clickOn("#quantityField");
        write("abc"); // Invalid quantity

        // Click "Add Book" button
        clickOn("Add Book");
        WaitForAsyncUtils.waitForFxEvents();

        // Verify alert popup
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");
        assertEquals("Warning",
                lookup(".alert .label").queryLabeled().getText(), "Alert message should match");
    }

    @Test
    public void testInsufficientStockAlert() {
        // Simulate login
        clickOn(".text-field").write("admin1"); // Enter username
        clickOn(".password-field").write("admin1"); // Enter password
        clickOn(".button"); // Click login button
        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Create Bill
        clickOn("#actionComboBox"); // Click on the combo box
        clickOn("Create Bill"); // Select "Create Bill" option
        WaitForAsyncUtils.waitForFxEvents();

        // Select an existing ISBN
        clickOn("#isbnComboBox");
        clickOn("9780061120044"); // Existing ISBN
        press(javafx.scene.input.KeyCode.ENTER).release(javafx.scene.input.KeyCode.ENTER); // Confirm selection
        clickOn("Load Book Info");
        WaitForAsyncUtils.waitForFxEvents();
        // Enter a quantity exceeding the stock
        clickOn("#quantityField");
        write("999"); // Exceeds stock

        // Click "Add Book" button
        clickOn("Add Book");
        WaitForAsyncUtils.waitForFxEvents();

        // Verify alert popup
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");
        assertEquals("Warning",
                lookup(".alert .label").queryLabeled().getText(), "Alert message should match");
    }

    @Test
    public void testLoginErrorWithWrongCredentials() {
        // Enter incorrect credentials
        clickOn(".text-field").write("wrongUser"); // Enter username
        clickOn(".password-field").write("wrongPass"); // Enter password
        clickOn(".button"); // Click login button

        WaitForAsyncUtils.waitForFxEvents();

        // Verify alert popup
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");
        assertEquals("Invalid Credentials", lookup(".alert .label").queryLabeled().getText(), "Alert message should match");
    }

}