package test.Integrationtesting.View;

import View.*;
import Controller.CreateBillController;
import Model.Book;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import javafx.scene.Node;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class BillIT extends ApplicationTest {

    private BillView billView;
    private CreateBillController createBillController;
    private Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bookstore";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "IndritFerati2604!";

    @Before
    public void setUp() {
        try {
            // Initialize database connection before anything else
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            connection.setAutoCommit(false);

            // Create controller with our managed connection
            createBillController = new CreateBillController(connection);
        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            if (createBillController == null) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                connection.setAutoCommit(false);
                createBillController = new CreateBillController(connection);
            }

            billView = new BillView(createBillController);
            BillView.setLibrarianUsername("TestLibrarian");
            billView.start(stage);
        } catch (SQLException e) {
            fail("Failed to start application: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        if (connection != null) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error in tearDown: " + e.getMessage());
            }
        }
    }

    @Test
    public void testLoadBookInfo() {
        waitForFxEvents();

        ComboBox<String> isbnComboBox = lookup("#isbnComboBox").query();
        interact(() -> isbnComboBox.getSelectionModel().select("9780061120044"));

        clickOn("#loadBookButton");

        TextField quantityField = lookup("#quantityField").query();
        assertFalse(quantityField.isDisabled());
        assertEquals("0", quantityField.getText());
    }

    @Test
    public void testAddValidBook() {
        waitForFxEvents();

        ComboBox<String> isbnComboBox = lookup("#isbnComboBox").query();
        interact(() -> isbnComboBox.getSelectionModel().select("9780061120044"));
        clickOn("#loadBookButton");

        TextField quantityField = lookup("#quantityField").query();
        clickOn(quantityField);
        write("2");

        clickOn("#addBookButton");

        TableView<Book> bookTable = lookup("#bookTableView").query();
        assertEquals(1, bookTable.getItems().size());


    }

    @Test
    public void testAddInvalidQuantity() {
        waitForFxEvents();

        ComboBox<String> isbnComboBox = lookup("#isbnComboBox").query();
        interact(() -> isbnComboBox.getSelectionModel().select("1234567890"));
        clickOn("#loadBookButton");

        TextField quantityField = lookup("#quantityField").query();
        clickOn(quantityField);
        write("11");

        clickOn("#addBookButton");

        waitForFxEvents();

        Node dialogPane = lookup(".dialog-pane").query();
        assertTrue(dialogPane.isVisible());

        clickOn("OK");

        TableView<Book> bookTable = lookup("#bookTableView").query();
        assertTrue(bookTable.getItems().isEmpty());
    }

}