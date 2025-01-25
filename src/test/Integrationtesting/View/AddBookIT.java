package test.Integrationtesting.View;

import Model.Book;
import Controller.AddBookController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

public class AddBookIT extends ApplicationTest {

    private AddBookController mockController;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    private TextField bookNameField;
    private TextField isbnField;
    private TextField authorField;
    private ComboBox<String> categoryComboBox;
    private TextField sellingPriceField;

    @BeforeEach
    public void setUp() throws Exception {
        Connection mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        mockController = new AddBookController(mockConnection);
        bookNameField = new TextField();
        isbnField = new TextField();
        authorField = new TextField();
        sellingPriceField = new TextField();
        categoryComboBox = new ComboBox<>();
    }

    @Test
    public void testValidBookSubmission() throws SQLException {
        bookNameField.setText("Test Book");
        isbnField.setText("1234-56-7890");
        authorField.setText("John Doe");
        sellingPriceField.setText("19.99");
        categoryComboBox.setValue("Fiction");
        when(mockResultSet.next()).thenReturn(false);
        Book book = new Book(
                "http://example.com/image.png",
                bookNameField.getText(),
                categoryComboBox.getValue(),
                isbnField.getText(),
                authorField.getText(),
                Double.parseDouble(sellingPriceField.getText())
        );

        mockController.saveBook(book);
        verify(mockStatement).executeUpdate();
    }

    @Test
    public void testInvalidISBNAlert() {
        Alert alert = mock(Alert.class);
        Book invalidBook = new Book(
                "http://example.com/image.png",
                "Test Book",
                "Fiction",
                "12345", // Invalid ISBN
                "John Doe",
                19.99
        );
        AddBookController controller = new AddBookController();

        Platform.runLater(() -> controller.saveBook(invalidBook));

        waitForRunLater();
        verify(alert, times(1)).showAndWait();
    }
    @Test
    public void testInvalidAuthorAlert() {
        Alert alert = mock(Alert.class);
        Book invalidAuthorBook = new Book(
                "http://example.com/image.png",
                "Test Book",
                "Fiction",
                "1234-56-7890",
                "", // Invalid author name (empty string)
                19.99
        );
        AddBookController controller = new AddBookController();
        Platform.runLater(() -> controller.saveBook(invalidAuthorBook));
        waitForRunLater();
        verify(alert, times(1)).showAndWait();
    }
    @Test
    public void testInvalidSellingPriceAlert() {
        Alert alert = mock(Alert.class);
        Book invalidPriceBook = new Book(
                "http://example.com/image.png",
                "Test Book",
                "Fiction",
                "1234-56-7890",
                "John Doe",
                -19.99
        );
        AddBookController controller = new AddBookController();
        Platform.runLater(() -> controller.saveBook(invalidPriceBook));
        waitForRunLater();
        verify(alert, times(1)).showAndWait(); // Ensure showAndWait was invoked once
    }

    private void waitForRunLater() {
        try {
            Platform.runLater(() -> {});
            Thread.sleep(500); // Ensure JavaFX tasks complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }



}
