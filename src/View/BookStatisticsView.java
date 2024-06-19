package View;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BookStatisticsView extends Application {
    private Label boughtDataLabel;
    private Label soldDataLabel;
    @Override
    public void start(Stage stage) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);

        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        Rectangle headerRectangle = new Rectangle(screenWidth, 140);
        headerRectangle.setFill(Color.BLUE);

        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);

        Text headerText = new Text("Book Statistics");
        headerText.setFont(Font.font("Times New Roman", 36));
        headerText.setFill(Color.WHITE);

        HBox statisticsBox = new HBox(10);
        statisticsBox.setAlignment(Pos.CENTER);

        Label statisticsLabel = new Label("Statistics will go here.");
        statisticsLabel.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 16));
        statisticsLabel.setStyle("-fx-font-weight: bold;");

        HBox.setMargin(statisticsLabel, new Insets(70, 10, 20, 0));

        ComboBox<String> statisticsComboBox = new ComboBox<>();
        statisticsComboBox.getItems().addAll("Select Timeline", "Daily", "Monthly", "Total");
        statisticsComboBox.setValue("Select Timeline");

        HBox.setMargin(statisticsComboBox, new Insets(70, 10, 20, 0));

        statisticsBox.getChildren().addAll(statisticsLabel, statisticsComboBox);

        VBox boughtBooksBox = createDataBox("Bought Books:");

        VBox soldBooksBox = createDataBox("Sold Books:");

        stackPane.getChildren().addAll(headerRectangle, headerText);
        vbox.getChildren().addAll(stackPane, statisticsBox, boughtBooksBox, soldBooksBox);

        statisticsComboBox.setOnAction(e -> {
            String selectedOption = statisticsComboBox.getValue();

            if (selectedOption.equals("Select Timeline")) {
                statisticsLabel.setText("Statistics will go here.");
                boughtDataLabel.setText("");
                soldDataLabel.setText("");
            } else {
                String boughtBooks = readDataFromFile("/boughtBookStatistics.txt", "Original Price");
                String[] lines = boughtBooks.split("\n");
                String boughtBooksData = "";
                String soldBooksData = "";
                for (String line : lines) {
                    boughtBooksData += line + "\n";
                }

                String soldBooks = readDataFromFile("/soldBookStatistics.txt", "Original Price");
                String[] soldBookLines = soldBooks.split("\n");

                for (String soldBookLine : soldBookLines) {
                    soldBooksData += soldBookLine + "\n";
                }

                switch (selectedOption) {
                    case "Daily":
                        boughtBooksData = calculateDailyStatistics(boughtBooksData);
                        soldBooksData = calculateDailyStatistics(soldBooksData);
                        break;
                    case "Monthly":
                        boughtBooksData = calculateMonthlyStatistics(boughtBooksData);
                        soldBooksData = calculateMonthlyStatistics(soldBooksData);
                        break;
                    case "Total":
                        boughtBooksData = calculateTotalStatistics(boughtBooksData);
                        soldBooksData = calculateTotalStatistics(soldBooksData);
                        break;
                }

                statisticsLabel.setText(selectedOption + " Statistics:");
                boughtDataLabel.setText(boughtBooksData);
                soldDataLabel.setText(soldBooksData);
            }
        });

        VBox.setMargin(statisticsBox, new Insets(10, 0, 10, 0));

        Scene bookStatisticsScene = new Scene(vbox, 800, 600);
        stage.setScene(bookStatisticsScene);
        stage.setTitle("Book Statistics View");
        stage.setFullScreen(true);
        stage.show();
    }

    private String readDataFromFile(String filePath, String excludeFieldName) {
        StringBuilder data = new StringBuilder();

        try (InputStream inputStream = getClass().getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = br.readLine()) != null) {

                if (!line.contains(excludeFieldName)) {
                    data.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toString();
    }

    private String calculateDailyStatistics(String data) {
        return "Daily Statistics:\n" + data;
    }

    private String calculateMonthlyStatistics(String data) {
        return "Monthly Statistics:\n" + data;
    }

    private String calculateTotalStatistics(String data) {
        return "Total Statistics:\n" + data;
    }

    private VBox createDataBox(String label) {
        VBox dataBox = new VBox();
        dataBox.setAlignment(Pos.CENTER);
        dataBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(6), Insets.EMPTY)));
        dataBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(2))));
        dataBox.setPadding(new Insets(20, 20, 20, 20));

        Label dataLabel = new Label(label);
        dataLabel.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 16));
        dataLabel.setTextFill(Color.BLACK);

        dataBox.getChildren().add(dataLabel);

        Label specificDataLabel = new Label();
        specificDataLabel.setFont(Font.font("Arial", FontWeight.NORMAL, FontPosture.REGULAR, 14));
        specificDataLabel.setTextFill(Color.BLACK);

        dataBox.getChildren().add(specificDataLabel);

        if (label.equals("Bought Books:")) {
            boughtDataLabel = specificDataLabel;
        } else if (label.equals("Sold Books:")) {
            soldDataLabel = specificDataLabel;
        }
        return dataBox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}