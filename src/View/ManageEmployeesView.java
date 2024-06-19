package View;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.swing.text.View;
import java.time.ZoneId;
import java.util.Date;

public class ManageEmployeesView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employee Management");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));
        Label actionLabel = new Label("Select Action:");
        actionLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));
        grid.add(actionLabel, 0, 0);
        ComboBox<String> actionComboBox = new ComboBox<>();
        actionComboBox.getItems().addAll("Select", "Register", "Modify", "Delete", "Permissions");
        actionComboBox.setValue("Select");
        grid.add(actionComboBox, 1, 0);
        VBox dataContainer = new VBox();
        dataContainer.setAlignment(Pos.CENTER);
        dataContainer.setPadding(new Insets(20, 20, 20, 20));
        dataContainer.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 4px; " +
                        "-fx-border-radius: 6px; " +
                        "-fx-padding: 10px;"
        );
        grid.add(dataContainer, 0, 1, 2, 1);
        Scene scene = new Scene(grid, 400, 600);
        primaryStage.setScene(scene);
        actionComboBox.setOnAction(e -> {
            String selectedAction = actionComboBox.getValue();
            if (selectedAction.equals("Register")) {
                showRegistrationForm(dataContainer);
            } else if (selectedAction.equals("Delete")) {
                showDeleteOptions(dataContainer);
            } else if (selectedAction.equals("Permissions")) {
            } else if (selectedAction.equals("Modify")) {
            } else {
                dataContainer.getChildren().clear();
            }
        });
        primaryStage.show();
        centerStage(primaryStage);
    }

    private void centerStage(Stage stage) {
        Screen screen = Screen.getPrimary();
        stage.setX((screen.getVisualBounds().getWidth() - stage.getWidth()) / 2);
        stage.setY((screen.getVisualBounds().getHeight() - stage.getHeight()) / 2);
    }

    private void showRegistrationForm(VBox dataContainer) {
        dataContainer.getChildren().clear();

        Label usernameLabel = new Label("Username:");
        TextField usernameTextField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label nameLabel = new Label("Name:");
        TextField nameTextField = new TextField();
        Label birthdayLabel = new Label("Birthday:");
        DatePicker birthdayDatePicker = new DatePicker();
        Label phoneLabel = new Label("Phone:");
        TextField phoneTextField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailTextField = new TextField();
        Label salaryLabel = new Label("Salary:");
        TextField salaryTextField = new TextField();

        Label roleLabel = new Label("Role:");

        Button registerButton = new Button("Register");
        dataContainer.getChildren().addAll(
                usernameLabel, usernameTextField,
                passwordLabel, passwordField,
                nameLabel, nameTextField,
                birthdayLabel, birthdayDatePicker,
                phoneLabel, phoneTextField,
                emailLabel, emailTextField,
                salaryLabel, salaryTextField,
                registerButton
        );

        registerButton.setOnAction(e -> {
            String username = usernameTextField.getText();
            String password = passwordField.getText();
            String name = nameTextField.getText();
            Date birthday = Date.from(birthdayDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            String phone = phoneTextField.getText();
            String email = emailTextField.getText();
            double salary = Double.parseDouble(salaryTextField.getText());


            showAlert("Registration Successful", "Employee registered successfully.");
        });
    }

    private void showDeleteOptions(VBox dataContainer) {
        dataContainer.getChildren().clear();

        Label usernameLabel = new Label("Enter Username:");
        TextField usernameTextField = new TextField();

        Button deleteButton = new Button("Delete");
        dataContainer.getChildren().addAll(usernameLabel, usernameTextField, deleteButton);


    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}