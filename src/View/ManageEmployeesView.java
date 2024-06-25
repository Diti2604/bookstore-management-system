package View;

import Controller.ManageEmployeesController;
import Model.User;
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

import java.time.LocalDate;
import java.util.List;

public class ManageEmployeesView extends Application {
    private ManageEmployeesController controller;

    private ComboBox<User> managerComboBox;
    private ComboBox<User> librarianComboBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employee Management");
        controller = new ManageEmployeesController();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Label actionLabel = new Label("Select Action:");
        actionLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));

        grid.add(actionLabel, 0, 0);
        ComboBox<String> actionComboBox = new ComboBox<>();
        actionComboBox.getItems().addAll("Select", "Register", "Modify", "Delete");
        actionComboBox.setValue("Select");
        grid.add(actionComboBox, 1, 0);

        managerComboBox = new ComboBox<>();
        managerComboBox.setPromptText("Managers: ");
        managerComboBox.setVisible(false);


        librarianComboBox = new ComboBox<>();
        librarianComboBox.setPromptText("Librarians: ");
        librarianComboBox.setVisible(false);


        // Fetch and populate ComboBoxes with managers and librarians
        List<User> managers = controller.getUsersByRole("manager");
        List<User> librarians = controller.getUsersByRole("librarian");

        managerComboBox.getItems().addAll(managers);
        librarianComboBox.getItems().addAll(librarians);

        grid.add(managerComboBox, 0, 2);
        grid.add(librarianComboBox, 1, 2);
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
            if ("Modify".equals(selectedAction)) {
                managerComboBox.setVisible(true);
                librarianComboBox.setVisible(true);
                showModifyForm(dataContainer); // Example: Show modify form
            } else if ("Register".equals(selectedAction)) {
                showRegistrationForm(dataContainer); // Example: Show registration form
            } else if ("Delete".equals(selectedAction)) {
                showDeleteOptions(dataContainer); // Example: Show delete options
            } else {
                dataContainer.getChildren().clear(); // Clear data container for other actions
                managerComboBox.setVisible(false);
                librarianComboBox.setVisible(false);
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
        TextField roleTextField = new TextField();

        Button registerButton = new Button("Register");

        dataContainer.getChildren().addAll(
                usernameLabel, usernameTextField,
                passwordLabel, passwordField,
                nameLabel, nameTextField,
                birthdayLabel, birthdayDatePicker,
                phoneLabel, phoneTextField,
                emailLabel, emailTextField,
                salaryLabel, salaryTextField,
                roleLabel, roleTextField,
                registerButton
        );

        registerButton.setOnAction(e -> {
            String username = usernameTextField.getText();
            String password = passwordField.getText();
            String name = nameTextField.getText();
            LocalDate birthday = birthdayDatePicker.getValue();
            String phone = phoneTextField.getText();
            String email = emailTextField.getText();
            double salary = Double.parseDouble(salaryTextField.getText());
            String role = roleTextField.getText();

            // Create a new User object
            User user = new User(username, password, name, birthday, phone, email, salary, role);

            // Call the registerEmployee method in the controller
            boolean success = controller.registerEmployee(user);

            if (success) {
                showAlert("Registration Successful", "Employee registered successfully.");
            } else {
                showAlert("Registration Failed", "Failed to register employee.");
            }
        });
    }

    private void showModifyForm(VBox dataContainer) {
        dataContainer.getChildren().clear();

        Label selectionLabel = new Label("Select User:");
        ComboBox<User> userComboBox = new ComboBox<>();
        userComboBox.getItems().addAll(managerComboBox.getItems());
        userComboBox.getItems().addAll(librarianComboBox.getItems());

        Button fetchButton = new Button("Fetch User");
        dataContainer.getChildren().addAll(selectionLabel, userComboBox, fetchButton);

        fetchButton.setOnAction(e -> {
            User selectedUser = userComboBox.getValue();

            if (selectedUser != null) {
                // Display user details for modification
                dataContainer.getChildren().clear();

                Label usernameLabel = new Label("Username:");
                Label usernameValueLabel = new Label(selectedUser.getUsername());

                Label passwordLabel = new Label("Password:");
                PasswordField passwordField = new PasswordField();
                passwordField.setText(selectedUser.getPassword());

                Label nameLabel = new Label("Name:");
                TextField nameTextField = new TextField();
                nameTextField.setText(selectedUser.getName());

                Label birthdayLabel = new Label("Birthday:");
                DatePicker birthdayDatePicker = new DatePicker();
                birthdayDatePicker.setValue(selectedUser.getBirthday());

                Label phoneLabel = new Label("Phone:");
                TextField phoneTextField = new TextField();
                phoneTextField.setText(selectedUser.getPhone());

                Label emailLabel = new Label("Email:");
                TextField emailTextField = new TextField();
                emailTextField.setText(selectedUser.getEmail());

                Label salaryLabel = new Label("Salary:");
                TextField salaryTextField = new TextField();
                salaryTextField.setText(String.valueOf(selectedUser.getSalary()));

                Label roleLabel = new Label("Role:");
                TextField roleTextField = new TextField();
                roleTextField.setText(selectedUser.getRole());

                Label newUsernameLabel = new Label("New Username:");
                TextField newUsernameTextField = new TextField();
                newUsernameTextField.setText(selectedUser.getUsername());

                Button updateButton = new Button("Update");

                dataContainer.getChildren().addAll(
                        usernameLabel, usernameValueLabel,
                        passwordLabel, passwordField,
                        nameLabel, nameTextField,
                        birthdayLabel, birthdayDatePicker,
                        phoneLabel, phoneTextField,
                        emailLabel, emailTextField,
                        salaryLabel, salaryTextField,
                        roleLabel, roleTextField,
                        newUsernameLabel, newUsernameTextField,
                        updateButton
                );

                updateButton.setOnAction(event -> {
                    // Update user with modified details
                    String newUsername = newUsernameTextField.getText();
                    String newPassword = passwordField.getText();
                    String newName = nameTextField.getText();
                    LocalDate newBirthday = birthdayDatePicker.getValue();
                    String newPhone = phoneTextField.getText();
                    String newEmail = emailTextField.getText();
                    double newSalary = Double.parseDouble(salaryTextField.getText());
                    String newRole = roleTextField.getText();

                    User updatedUser = new User(newUsername, newPassword, newName, newBirthday, newPhone, newEmail, newSalary, newRole);

                    boolean updated = controller.updateUser(updatedUser, selectedUser.getUsername());

                    if (updated) {
                        showAlert("Update Successful", "Employee details updated successfully.");
                    } else {
                        showAlert("Update Failed", "Failed to update employee details.");
                    }
                });
            } else {
                showAlert("User Not Selected", "Please select a user to modify.");
            }
        });
    }

    private void showDeleteOptions(VBox dataContainer) {
        dataContainer.getChildren().clear();

        Label selectionLabel = new Label("Select User:");
        ComboBox<User> userComboBox = new ComboBox<>();
        userComboBox.getItems().addAll(managerComboBox.getItems());
        userComboBox.getItems().addAll(librarianComboBox.getItems());

        Button deleteButton = new Button("Delete");
        dataContainer.getChildren().addAll(selectionLabel, userComboBox, deleteButton);

        deleteButton.setOnAction(e -> {
            User selectedUser = userComboBox.getValue();

            if (selectedUser != null) {
                boolean deleted = controller.deleteUser(selectedUser.getUsername());

                if (deleted) {
                    showAlert("Delete Successful", "Employee deleted successfully.");
                } else {
                    showAlert("Delete Failed", "Failed to delete employee.");
                }
            } else {
                showAlert("User Not Selected", "Please select a user to delete.");
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}