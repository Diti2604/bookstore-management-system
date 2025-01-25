package View;

import Controller.BookStatisticsController;
import Model.Book;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Arrays;

public class BookStatisticsView extends Application {
    private TableView<Book> statisticsTableView;
    private BookStatisticsController statisticsController;

    public BookStatisticsView(BookStatisticsController statisticsController) {
        this.statisticsController = statisticsController;
    }
    public BookStatisticsView (){}

    @Override
    public void start(Stage stage) {
        statisticsController = new BookStatisticsController();

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(20));

        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        Rectangle headerRectangle = new Rectangle(screenWidth, 140);
        headerRectangle.setFill(Color.DARKBLUE);

        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);

        Text headerText = new Text("Book Statistics");
        headerText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 36));
        headerText.setFill(Color.WHITE);

        stackPane.getChildren().addAll(headerRectangle, headerText);

        VBox statisticsBox = new VBox(10);
        statisticsBox.setId("statisticsBox");
        statisticsBox.setAlignment(Pos.CENTER);
        statisticsBox.setPadding(new Insets(20));
        statisticsBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #c0c0c0; -fx-border-width: 1;");

        Text tableTitle = new Text("Sales Statistics");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        statisticsTableView = new TableView<>();
        statisticsTableView.setId("bookStatisticsTable");
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, Integer> quantityColumn = new TableColumn<>("Quantity Sold");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Book, Double> totalPriceColumn = new TableColumn<>("Total Price");
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        statisticsTableView.getColumns().addAll(
                Arrays.asList(titleColumn, quantityColumn, totalPriceColumn)
        );
        statisticsTableView.setPrefHeight(400);
        statisticsTableView.setPrefWidth(600);

        ComboBox<String> statisticsComboBox = new ComboBox<>();
        statisticsComboBox.setId("statisticsComboBox");
        statisticsComboBox.getItems().addAll("Select Timeline", "Daily", "Monthly", "Total");
        statisticsComboBox.setValue("Select Timeline");
        statisticsComboBox.setPadding(new Insets(5));


        statisticsComboBox.setOnAction(event -> {
            String selectedTimeline = statisticsComboBox.getValue();
            ObservableList<Book> statistics = FXCollections.observableArrayList();

            switch (selectedTimeline) {
                case "Daily":
                    statistics = statisticsController.getDailyStatistics();
                    break;
                case "Monthly":
                    statistics = statisticsController.getMonthlyStatistics();
                    break;
                case "Total":
                    statistics = statisticsController.getTotalStatistics();
                    break;
                default:
                    break;
            }
            statisticsTableView.setItems(statistics);
        });

        statisticsBox.getChildren().addAll(tableTitle, statisticsTableView, statisticsComboBox);

        vbox.getChildren().addAll(stackPane, statisticsBox);

        Scene bookStatisticsScene = new Scene(vbox, 800, 600);
        stage.setScene(bookStatisticsScene);
        stage.setTitle("Book Statistics View");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
