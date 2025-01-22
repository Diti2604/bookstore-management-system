package test.SystemTesting;

import Controller.LoginController;
import Model.User;
import View.LoginView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class AddBookFlowTest extends ApplicationTest {
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
    public void testAddBook() {
        // Simulate entering credentials and clicking login button
        clickOn(".text-field").write("admin1"); // Enter username
        clickOn(".password-field").write("admin1"); // Enter password
        clickOn(".button"); // Click login button

        WaitForAsyncUtils.waitForFxEvents();

        // Verify login success
        User user = loginController.validateCredentials("admin1", "admin1");
        assertNotNull(user, "Login should succeed with correct credentials");

        // Navigate to Add Book
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

    }

        @Test
        public void testInvalidSellingPrice() {
            clickOn(".text-field").write("admin1"); // Enter username
            clickOn(".password-field").write("admin1"); // Enter password
            clickOn(".button"); // Click login button

            WaitForAsyncUtils.waitForFxEvents();

            // Verify login success
            User user = loginController.validateCredentials("admin1", "admin1");
            assertNotNull(user, "Login should succeed with correct credentials");

            // Navigate to Add Book
            clickOn("#actionComboBox"); // Click on the action combo box
            clickOn("Add Book"); // Select "Add Book" option

            WaitForAsyncUtils.waitForFxEvents();

            // Add assertions to verify that the Add Book view is displayed
            assertTrue(lookup("Add Book").tryQuery().isPresent(), "Add Book view should be displayed");

            WaitForAsyncUtils.waitForFxEvents();

            // Simulate entering invalid selling price
            clickOn("#bookUrlField").write("https://images");
            clickOn("#bookNameField").write("Test Book");
            clickOn("#categoryComboBox").clickOn("Fantasy");
            clickOn("#isbnField").write("9780061120084");
            clickOn("#authorField").write("Harper Lee");
            clickOn("#sellingPriceField").write("-10.99"); // Invalid negative price

            // Click "Add Book" button
            clickOn("#addBookButton");
            WaitForAsyncUtils.waitForFxEvents();

            // Verify alert popup
            assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");
            assertEquals("Invalid Input", lookup(".alert .label").queryLabeled().getText(), "Alert message should match");
        }

        @Test
        public void testInvalidISBNFormat() {
            clickOn(".text-field").write("admin1"); // Enter username
            clickOn(".password-field").write("admin1"); // Enter password
            clickOn(".button"); // Click login button

            WaitForAsyncUtils.waitForFxEvents();

            // Verify login success
            User user = loginController.validateCredentials("admin1", "admin1");
            assertNotNull(user, "Login should succeed with correct credentials");

            // Navigate to Add Book
            clickOn("#actionComboBox"); // Click on the action combo box
            clickOn("Add Book"); // Select "Add Book" option

            WaitForAsyncUtils.waitForFxEvents();

            // Add assertions to verify that the Add Book view is displayed
            assertTrue(lookup("Add Book").tryQuery().isPresent(), "Add Book view should be displayed");

            WaitForAsyncUtils.waitForFxEvents();
            // Simulate entering invalid ISBN format
            clickOn("#bookUrlField").write("https://images");
            clickOn("#bookNameField").write("Test Book");
            clickOn("#categoryComboBox").clickOn("Fantasy");
            clickOn("#isbnField").write("12345"); // Invalid ISBN format
            clickOn("#authorField").write("Harper Lee");
            clickOn("#sellingPriceField").write("10.99");

            // Click "Add Book" button
            clickOn("#addBookButton");
            WaitForAsyncUtils.waitForFxEvents();

            // Verify alert popup
            assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");
            assertEquals("Invalid Input", lookup(".alert .label").queryLabeled().getText(), "Alert message should match");
        }

        @Test
        public void testInvalidAuthorName() {
            clickOn(".text-field").write("admin1"); // Enter username
            clickOn(".password-field").write("admin1"); // Enter password
            clickOn(".button"); // Click login button

            WaitForAsyncUtils.waitForFxEvents();

            // Verify login success
            User user = loginController.validateCredentials("admin1", "admin1");
            assertNotNull(user, "Login should succeed with correct credentials");

            // Navigate to Add Book
            clickOn("#actionComboBox"); // Click on the action combo box
            clickOn("Add Book"); // Select "Add Book" option

            WaitForAsyncUtils.waitForFxEvents();

            // Add assertions to verify that the Add Book view is displayed
            assertTrue(lookup("Add Book").tryQuery().isPresent(), "Add Book view should be displayed");

            WaitForAsyncUtils.waitForFxEvents();
            // Simulate entering an invalid author name (e.g., an empty author field)
            clickOn("#bookUrlField").write("https://images");
            clickOn("#bookNameField").write("Test Book");
            clickOn("#categoryComboBox").clickOn("Fantasy");
            clickOn("#isbnField").write("9780061120084");
            clickOn("#authorField").write(""); // Invalid empty author name
            clickOn("#sellingPriceField").write("10.99");

            // Click "Add Book" button
            clickOn("#addBookButton");
            WaitForAsyncUtils.waitForFxEvents();

            // Verify alert popup
            assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");
            assertEquals("Invalid Input", lookup(".alert .label").queryLabeled().getText(), "Alert message should match");
        }

        @Test
        public void testBookSavedSuccessfully() {
            clickOn(".text-field").write("admin1"); // Enter username
            clickOn(".password-field").write("admin1"); // Enter password
            clickOn(".button"); // Click login button

            WaitForAsyncUtils.waitForFxEvents();

            // Verify login success
            User user = loginController.validateCredentials("admin1", "admin1");
            assertNotNull(user, "Login should succeed with correct credentials");

            // Navigate to Add Book
            clickOn("#actionComboBox"); // Click on the action combo box
            clickOn("Add Book"); // Select "Add Book" option

            WaitForAsyncUtils.waitForFxEvents();

            // Add assertions to verify that the Add Book view is displayed
            assertTrue(lookup("Add Book").tryQuery().isPresent(), "Add Book view should be displayed");

            WaitForAsyncUtils.waitForFxEvents();
            // Simulate adding a valid book
            clickOn("#bookUrlField").write("https://images");
            clickOn("#bookNameField").write("Valid Book");
            clickOn("#categoryComboBox").clickOn("Fantasy");
            clickOn("#isbnField").write("9780061120084");
            clickOn("#authorField").write("Harper Lee");
            clickOn("#sellingPriceField").write("10.99");

            // Click "Add Book" button
            clickOn("#addBookButton");
            WaitForAsyncUtils.waitForFxEvents();

            // Verify alert popup
            assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");
            assertEquals("Invalid Input", lookup(".alert .label").queryLabeled().getText(), "Alert message should match");
        }

        @Test
        public void testBookNotFound() {
            clickOn(".text-field").write("admin1"); // Enter username
            clickOn(".password-field").write("admin1"); // Enter password
            clickOn(".button"); // Click login button

            WaitForAsyncUtils.waitForFxEvents();

            // Verify login success
            User user = loginController.validateCredentials("admin1", "admin1");
            assertNotNull(user, "Login should succeed with correct credentials");

            // Navigate to Add Book
            clickOn("#actionComboBox"); // Click on the action combo box
            clickOn("Add Book"); // Select "Add Book" option

            WaitForAsyncUtils.waitForFxEvents();

            // Add assertions to verify that the Add Book view is displayed
            assertTrue(lookup("Add Book").tryQuery().isPresent(), "Add Book view should be displayed");

            WaitForAsyncUtils.waitForFxEvents();
            // Simulate searching for a book that doesn't exist
            clickOn("#isbnField").write("9999999999"); // ISBN not found

            // Click "Add Book" button
            clickOn("#addBookButton");
            WaitForAsyncUtils.waitForFxEvents();

            // Verify alert popup
            assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");
            assertEquals("Invalid Input", lookup(".alert .label").queryLabeled().getText(), "Alert message should match");
        }

        @Test
        public void testEmptyISBNField() {
            clickOn(".text-field").write("admin1"); // Enter username
            clickOn(".password-field").write("admin1"); // Enter password
            clickOn(".button"); // Click login button

            WaitForAsyncUtils.waitForFxEvents();

            // Verify login success
            User user = loginController.validateCredentials("admin1", "admin1");
            assertNotNull(user, "Login should succeed with correct credentials");

            // Navigate to Add Book
            clickOn("#actionComboBox"); // Click on the action combo box
            clickOn("Add Book"); // Select "Add Book" option

            WaitForAsyncUtils.waitForFxEvents();

            // Add assertions to verify that the Add Book view is displayed
            assertTrue(lookup("Add Book").tryQuery().isPresent(), "Add Book view should be displayed");

            WaitForAsyncUtils.waitForFxEvents();
            // Simulate leaving the ISBN field empty
            clickOn("#bookUrlField").write("https://images");
            clickOn("#bookNameField").write("Test Book");
            clickOn("#categoryComboBox").clickOn("Fantasy");
            clickOn("#isbnField").write(""); // Empty ISBN field
            clickOn("#authorField").write("Harper Lee");
            clickOn("#sellingPriceField").write("10.99");

            // Click "Add Book" button
            clickOn("#addBookButton");
            WaitForAsyncUtils.waitForFxEvents();

            // Verify alert popup
            assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");
            assertEquals("Invalid Input", lookup(".alert .label").queryLabeled().getText(), "Alert message should match");
        }

    }


