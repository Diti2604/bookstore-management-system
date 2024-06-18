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

public class CheckLibrarianPerformanceView extends Application {
    //HERE THE LIBRARIAN SHOULD BE PICKED FROM THE LIBRARIAN GROUP THAT IS SAVED IN THE DATABASE
    //AND ACCORDING TO THE NAME OR USERNAME OR OTHER CREDENTIALS, HIS PERFORMANCE WILL BE SHOWN
    private Label dataLabel;
    @Override
    public void start(Stage stage) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);

        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        Rectangle headerRectangle = new Rectangle(screenWidth, 50);
        headerRectangle.setFill(Color.BLUE);

        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);

        Text headerText = new Text("Librarians Performance");
        headerText.setFont(Font.font("Times New Roman", 36));
        headerText.setFill(Color.WHITE);

        Label durationLabel = new Label("Select Librarian:");
        durationLabel.setFont(Font.font("Arial", 16));
        durationLabel.setStyle("-fx-font-weight: bold;");

        HBox optionsBox = new HBox(10);
        optionsBox.setAlignment(Pos.CENTER);

        DatePicker singleDatePicker = new DatePicker();
        singleDatePicker.setPromptText("Select Date");
        singleDatePicker.setStyle("-fx-border-color: blue;");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        startDatePicker.setStyle("-fx-border-color: blue;");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        endDatePicker.setStyle("-fx-border-color: blue;");

        VBox.setMargin(singleDatePicker, new Insets(0, 10, 0, 0));
        VBox.setMargin(endDatePicker, new Insets(0, 0, 0, 10));

        optionsBox.getChildren().addAll(singleDatePicker, startDatePicker, endDatePicker);
        optionsBox.setPadding(new Insets(10, 0, 10, 0));

        Button showDataButton = new Button("Show Data");
        showDataButton.setOnAction(e -> showData(singleDatePicker.getValue(), startDatePicker.getValue(), endDatePicker.getValue()));
        showDataButton.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-weight: bold;");

        VBox.setMargin(showDataButton, new Insets(10, 0, 10, 0));

        VBox dataBox = new VBox();
        dataBox.setAlignment(Pos.CENTER);
        dataBox.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(6), Insets.EMPTY)));
        dataBox.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(3))));
        dataBox.setPadding(new Insets(20, 20, 20, 20));

        dataLabel = new Label();
        dataLabel.setFont(Font.font("Arial", 20));
        dataLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow;");
        dataBox.getChildren().add(dataLabel);

        stackPane.getChildren().addAll(headerRectangle, headerText);
        vbox.getChildren().addAll(stackPane, durationLabel, optionsBox, showDataButton, dataBox);

        Scene librarianScene = new Scene(vbox, 800, 600);
        stage.setScene(librarianScene);
        stage.setTitle("Librarian View");
        stage.setFullScreen(true);
        stage.show();
    }
    private User[] readUserDataFromBinaryFile(String filePath) {
        try (InputStream inputStream = getClass().getResourceAsStream(filePath);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {

            Object[] objects = (Object[]) objectInputStream.readObject();
            User[] users = new User[objects.length];

            for (int i = 0; i < objects.length; i++) {
                users[i] = (User) objects[i];
            }

            return users;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void showData(LocalDate singleDate, LocalDate startDate, LocalDate endDate) {
        User[] users = readUserDataFromBinaryFile("/user_data.dat");
        if (users != null) {
            StringBuilder dataStringBuilder = new StringBuilder();
            for (User user : users) {
                String userData = "Username: " + user.getUsername() +
                        ", Role: " + user.getRole() +
                        ", Name: " + user.getName() +
                        ", Birthday: " + user.getBirthday() +
                        ", Phone: " + user.getPhone() +
                        ", Email: " + user.getEmail() +
                        ", Salary: " + user.getSalary();

                dataStringBuilder.append(userData).append("\n");
            }
            dataLabel.setText("User Data:\n" + dataStringBuilder.toString());
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}