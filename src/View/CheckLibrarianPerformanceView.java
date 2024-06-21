package View;

import Controller.CheckLibrarianPerformanceController;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CheckLibrarianPerformanceView extends Application {
    private Label dataLabel;
    private ComboBox<String> librarianComboBox;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private CheckLibrarianPerformanceController controller = new CheckLibrarianPerformanceController();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(10);

        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        Rectangle headerRectangle = new Rectangle(screenWidth, 50);
        headerRectangle.setFill(Color.BLUE);

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        Text headerText = new Text("Librarian Performance");
        headerText.setFont(Font.font("Times New Roman", 36));
        headerText.setFill(Color.BLUE);
        headerBox.getChildren().add(headerText);

        Label durationLabel = new Label("Select Librarian:");
        durationLabel.setFont(Font.font("Arial", 16));
        durationLabel.setStyle("-fx-font-weight: bold;");
        durationLabel.setPadding(new Insets(10, 0, 0, 0));

        ComboBox<String> librarianComboBox = new ComboBox<>();
        librarianComboBox.setStyle("-fx-font-size: 14px;");
        librarianComboBox.setPromptText("Select Librarian");

        ObservableList<String> librarianUsernames = controller.fetchLibrariansFromDatabase();
        librarianComboBox.setItems(librarianUsernames);

        HBox optionsBox = new HBox(10);
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setPadding(new Insets(0, 0, 10, 0));

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        startDatePicker.setStyle("-fx-border-color: blue;");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        endDatePicker.setStyle("-fx-border-color: blue;");

        optionsBox.getChildren().addAll(librarianComboBox, startDatePicker, endDatePicker);

        Button showDataButton = new Button("Show Data");
        showDataButton.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-weight: bold;");
        VBox.setMargin(showDataButton, new Insets(10, 0, 10, 0));
        showDataButton.setOnAction(e -> {
            String selectedLibrarian = librarianComboBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            if (selectedLibrarian != null && startDate != null && endDate != null) {
                showData(selectedLibrarian, startDate, endDate);
            } else {
                dataLabel.setText("Please select a librarian and choose start/end dates.");
            }
        });

        dataLabel = new Label();
        dataLabel.setFont(Font.font("Arial", 14));
        dataLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow;");
        VBox dataBox = new VBox(10);
        dataBox.setAlignment(Pos.CENTER);
        dataBox.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(6), Insets.EMPTY)));
        dataBox.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(3))));
        dataBox.setPadding(new Insets(20));
        dataBox.getChildren().add(dataLabel);

        root.getChildren().addAll(headerRectangle, headerBox, durationLabel, optionsBox, showDataButton, dataBox);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Librarian Performance View");
        stage.setFullScreen(true);
        stage.show();
    }

    private void showData(String selectedLibrarian, LocalDate startDate, LocalDate endDate) {
        List<String> salesData = controller.fetchLibrarianSalesData(selectedLibrarian, startDate, endDate);
        StringBuilder dataStringBuilder = new StringBuilder();

        if (!salesData.isEmpty()) {
            dataStringBuilder.append("Sales Data for: ").append(selectedLibrarian).append(":\n\n");
            for (String sale : salesData) {
                dataStringBuilder.append(sale).append("\n");
            }
        } else {
            dataStringBuilder.append("No sales data found for -> ").append(selectedLibrarian);
        }

        dataLabel.setText(dataStringBuilder.toString());
    }

}
