package Controller;

import Model.User;
import Model.Librarian;
import Model.Manager;
import Model.Administrator;
import View.LoginView;
import View.MainDashboardView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;

public class LoginController {

    private Connection conn;

    public LoginController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bookstore";
            String user = System.getenv("root");
            String password = System.getenv("DitiHost2604");
            conn = DriverManager.getConnection(url, "root", "DitiHost2604");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public User validateCredentials(String username, String enteredPassword) {
        try {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String enteredHashedPassword = User.hashPassword(enteredPassword);
                if (enteredHashedPassword.equals(storedHashedPassword)) {
                    String name = rs.getString("name");
                    LocalDate birthday = rs.getDate("birthday").toLocalDate();
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    double salary = rs.getDouble("salary");
                    String role = rs.getString("role");

                    switch (role) {
                        case "Librarian":
                            return new Librarian(username, storedHashedPassword, name, birthday, phone, email, salary, role);
                        case "Manager":
                            return new Manager(username, storedHashedPassword, name, birthday, phone, email, salary, role);
                        case "Administrator":
                            return new Administrator(username, storedHashedPassword, name, birthday, phone, email, salary, role);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void handleLogin(Stage stage, String username, String password) {
        User user = validateCredentials(username, password);
        if (user != null) {
            System.out.println("Login successful. Role: " + user.getRole());
            try {
                MainDashboardView dashboardView = new MainDashboardView(user.getRole(), username);
                dashboardView.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Invalid Credentials");
            alert.setContentText("The username or password you entered is incorrect.");
            alert.showAndWait();
        }
    }
}
