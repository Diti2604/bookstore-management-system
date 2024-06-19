package View;

import Model.User;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static javafx.application.Application.launch;

public class CheckLibrarianPerformanceView extends Application {
    private Label dataLabel;

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
        headerText.setFill(Color.WHITE);
        headerBox.getChildren().add(headerText);

        Label durationLabel = new Label("Select Librarian:");
        durationLabel.setFont(Font.font("Arial", 16));
        durationLabel.setStyle("-fx-font-weight: bold;");
        durationLabel.setPadding(new Insets(10, 0, 0, 0));

        HBox optionsBox = new HBox(10);
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setPadding(new Insets(0, 0, 10, 0));

        DatePicker singleDatePicker = new DatePicker();
        singleDatePicker.setPromptText("Select Date");
        singleDatePicker.setStyle("-fx-border-color: blue;");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        startDatePicker.setStyle("-fx-border-color: blue;");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        endDatePicker.setStyle("-fx-border-color: blue;");

        optionsBox.getChildren().addAll(singleDatePicker, startDatePicker, endDatePicker);

        Button showDataButton = new Button("Show Data");
        showDataButton.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-weight: bold;");
        VBox.setMargin(showDataButton, new Insets(10, 0, 10, 0));

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

}

