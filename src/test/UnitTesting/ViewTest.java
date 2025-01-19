package test.UnitTesting;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.Scene;
import javafx.scene.control.Button;

import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
public class ViewTest extends ApplicationTest {
    private ComboBox<String> isbnComboBox;
    private TextField quantityField;
    private Button loadBookButton;

    @Override
    public void start(Stage stage) {
        // Initialize the ComboBox with ISBN options
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("ISBN1", "ISBN2");

        // Initialize the quantity field (starting disabled)
        quantityField = new TextField();
        quantityField.setDisable(true); // Initially disabled

        // Initialize the button
        loadBookButton = new Button("Load Book Info");

        // Set an action for the button click to enable the quantity field
        loadBookButton.setOnAction(e -> quantityField.setDisable(false)); // Enable quantity field on button click

        // Create a simple layout and add the controls
        Scene scene = new Scene(comboBox);
        scene.setRoot(new javafx.scene.layout.VBox(comboBox, loadBookButton, quantityField));

        stage.setScene(scene);
        stage.show();

        // Assign references to the fields
        isbnComboBox = comboBox;
    }

    @Test
    public void should_select_isbn_from_combobox() {
        // Simulate user selecting an ISBN from the ComboBox
        interact(() -> isbnComboBox.getSelectionModel().select(0));

        // Verify that the ComboBox has the correct selection
        assertThat(isbnComboBox.getSelectionModel().getSelectedItem())
                .isEqualTo("ISBN1");
    }

    @Test
    public void should_enable_quantity_field_after_loading_book() {
        // Simulate user selecting an ISBN
        interact(() -> isbnComboBox.getSelectionModel().select(0)); // Select a valid index (0 or 1)

        // Simulate clicking on the "Load Book Info" button
        clickOn(loadBookButton);

        // Verify that the quantity field is enabled
        assertThat(quantityField.isDisabled()).isFalse();
    }
}
