package View;

import Controller.LoginController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginView extends Application {
    private LoginController loginController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        loginController = new LoginController();

        Image image = new Image(Objects.requireNonNull(getClass().getResource("/Assets/loginPicture.jpg")).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Enter username");
        usernameTextField.setMaxWidth(200);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setMaxWidth(200);

        Button loginButton = new Button("Log in");
        loginButton.setStyle("-fx-border-radius: 8px;-fx-border-width:4px;  -fx-font-weight:bold; -fx-background-color:white;-fx-cursor:pointer;");
        loginButton.setPrefHeight(20);

        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-border-radius: 8px; -fx-border-width: 4px; -fx-font-weight: bold; -fx-background-color: white; -fx-cursor: hand;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-border-radius: 8px; -fx-border-width: 4px; -fx-font-weight: bold; -fx-background-color: white; -fx-cursor: pointer;"));

        loginButton.setOnAction(event -> {
            String username = usernameTextField.getText();
            String password = passwordField.getText();
            loginController.handleLogin(stage, username, password);
        });

        VBox loginPane = new VBox();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setPadding(new Insets(15));
        loginPane.setSpacing(10);
        loginPane.getChildren().addAll(usernameTextField, passwordField, loginButton);

        Rectangle rectangle = new Rectangle(300, 200);
        rectangle.setFill(Color.rgb(0, 0, 0, 0.5));
        rectangle.setArcHeight(20);
        rectangle.setArcWidth(20);

        StackPane rootPane = new StackPane();
        rootPane.setStyle("-fx-background-color: #1e1e2e;");
        rootPane.getChildren().addAll(imageView, rectangle, loginPane);
        StackPane.setAlignment(rectangle, Pos.CENTER);
        StackPane.setAlignment(loginPane, Pos.CENTER);

        Scene scene = new Scene(rootPane, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Login Page");
        stage.setMaximized(true);

        stage.show();
    }
}
