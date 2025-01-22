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
import java.util.regex.Pattern;

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
        actionComboBox.setId("manageEmployeesComboBox");
        actionComboBox.getItems().addAll("Select", "Register", "Modify", "Delete");
        actionComboBox.setValue("Select");
        grid.add(actionComboBox, 1, 0);

        managerComboBox = new ComboBox<>();
        managerComboBox.setPromptText("Managers: ");
        managerComboBox.setVisible(false);


        librarianComboBox = new ComboBox<>();
        librarianComboBox.setPromptText("Librarians: ");
        librarianComboBox.setVisible(false);


        List<User> managers = controller.getUsersByRole("manager");
        List<User> librarians = controller.getUsersByRole("librarian");

        managerComboBox.getItems().addAll(managers);
        librarianComboBox.getItems().addAll(librarians);
        managerComboBox.setId("managerComboBox");
        librarianComboBox.setId("librarianComboBox");

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
                showModifyForm(dataContainer);
            } else if ("Register".equals(selectedAction)) {
                showRegistrationForm(dataContainer);
            } else if ("Delete".equals(selectedAction)) {
                showDeleteOptions(dataContainer);
            } else {
                dataContainer.getChildren().clear();
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
        usernameTextField.setId("usernameTextField");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setId("passwordField");

        Label nameLabel = new Label("Name:");
        TextField nameTextField = new TextField();
        nameTextField.setId("nameTextField");

        Label birthdayLabel = new Label("Birthday:");
        DatePicker birthdayDatePicker = new DatePicker();
        birthdayDatePicker.setId("birthdayDatePicker");

        Label phoneLabel = new Label("Phone:");
        TextField phoneTextField = new TextField();
        phoneTextField.setId("phoneTextField");

        Label emailLabel = new Label("Email:");
        TextField emailTextField = new TextField();
        emailTextField.setId("emailTextField");

        Label salaryLabel = new Label("Salary:");
        TextField salaryTextField = new TextField();
        salaryTextField.setId("salaryTextField");

        Label roleLabel = new Label("Role:");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.setId("roleComboBox");
        roleComboBox.getItems().addAll("Manager", "Administrator", "Librarian");

        Button registerButton = new Button("Register");
        registerButton.setId("registerButton");

        dataContainer.getChildren().addAll(
                usernameLabel, usernameTextField,
                passwordLabel, passwordField,
                nameLabel, nameTextField,
                birthdayLabel, birthdayDatePicker,
                phoneLabel, phoneTextField,
                emailLabel, emailTextField,
                salaryLabel, salaryTextField,
                roleLabel, roleComboBox,
                registerButton
        );

        registerButton.setOnAction(e -> {
            String username = usernameTextField.getText();
            String password = passwordField.getText();
            String name = nameTextField.getText();
            LocalDate birthday = birthdayDatePicker.getValue();
            String phone = phoneTextField.getText();
            String email = emailTextField.getText();
            String salaryStr = salaryTextField.getText();
            String role = roleComboBox.getValue();
            String phoneRegex = "^(068|069|067)\\d{7}$";

            if (!Pattern.matches("^[a-zA-Z0-9]+$", username)) {
                showAlert("Invalid Username", "Username should only contain letters and numbers.");
                return;
            }
            if (!Pattern.matches("^[a-zA-Z0-9]+$", password)) {
                showAlert("Invalid Password", "Password should only contain letters and numbers.");
                return;
            }
            if (!Pattern.matches("^[a-zA-Z]+$", name)) {
                showAlert("Invalid Name", "Name should only contain letters.");
                return;
            }
            if (!Pattern.matches(phoneRegex, phone)) {
                showAlert("Invalid Phone", "Phone number must start with 068, 069, or 067 and be 10 digits long.");
                return;
            }
            if (!Pattern.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$", email)) {
                showAlert("Invalid Email", "Email should be a valid Gmail address.");
                return;
            }
            if (!Pattern.matches("^\\d+(\\.\\d+)?$", salaryStr)) {
                showAlert("Invalid Salary", "Salary should only contain numbers.");
                return;
            }

            double salary = Double.parseDouble(salaryStr);


            User user = new User(username, password, name, birthday, phone, email, salary, role);

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
        userComboBox.setId("userComboBox");
        userComboBox.getItems().addAll(managerComboBox.getItems());
        userComboBox.getItems().addAll(librarianComboBox.getItems());

        Button fetchButton = new Button("Fetch User");
        fetchButton.setId("fetchButton");
        dataContainer.getChildren().addAll(selectionLabel, userComboBox, fetchButton);

        fetchButton.setOnAction(e -> {
            User selectedUser = userComboBox.getValue();

            if (selectedUser != null) {
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
                ComboBox<String> roleComboBox = new ComboBox<>();
                roleComboBox.getItems().addAll("manager", "administrator", "librarian");
                roleComboBox.setValue(selectedUser.getRole());

                Label newUsernameLabel = new Label("New Username:");
                TextField newUsernameTextField = new TextField();
                newUsernameTextField.setText(selectedUser.getUsername());

                Button updateButton = new Button("Update");


                salaryTextField.setId("salaryTextField");
                updateButton.setId("updateButton");


                dataContainer.getChildren().addAll(
                        usernameLabel, usernameValueLabel,
                        passwordLabel, passwordField,
                        nameLabel, nameTextField,
                        birthdayLabel, birthdayDatePicker,
                        phoneLabel, phoneTextField,
                        emailLabel, emailTextField,
                        salaryLabel, salaryTextField,
                        roleLabel, roleComboBox,
                        newUsernameLabel, newUsernameTextField,
                        updateButton
                );

                updateButton.setOnAction(event -> {
                    String newUsername = newUsernameTextField.getText();
                    String newPassword = passwordField.getText();
                    String newName = nameTextField.getText();
                    LocalDate newBirthday = birthdayDatePicker.getValue();
                    String newPhone = phoneTextField.getText();
                    String newEmail = emailTextField.getText();
                    String newSalaryStr = salaryTextField.getText();
                    String newRole = roleComboBox.getValue();

                    if (!Pattern.matches("^[a-zA-Z0-9]+$", newPassword)) {
                        showAlert("Invalid Password", "Password should only contain letters and numbers.");
                        return;
                    }
                    if (!Pattern.matches("^[a-zA-Z]+$", newName)) {
                        showAlert("Invalid Name", "Name should only contain letters.");
                        return;
                    }
                    if (!Pattern.matches("^\\d{10}$", newPhone)) {
                        showAlert("Invalid Phone", "Phone should only contain 10 digits.");
                        return;
                    }
                    if (!Pattern.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$", newEmail)) {
                        showAlert("Invalid Email", "Email should be a valid Gmail address.");
                        return;
                    }
                    if (!Pattern.matches("^\\d+(\\.\\d+)?$", newSalaryStr)) {
                        showAlert("Invalid Salary", "Salary should only contain numbers.");
                        return;
                    }
                    if (!Pattern.matches("^[a-zA-Z0-9]+$", newUsername)) {
                        showAlert("Invalid Username", "Username should only contain letters and numbers.");
                        return;
                    }

                    double newSalary = Double.parseDouble(newSalaryStr);


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
        userComboBox.setId("userComboBoxDelete");
        userComboBox.getItems().addAll(managerComboBox.getItems());
        userComboBox.getItems().addAll(librarianComboBox.getItems());

        Button deleteButton = new Button("Delete");
        deleteButton.setId("deleteButton");
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