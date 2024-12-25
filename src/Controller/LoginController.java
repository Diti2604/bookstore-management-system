package Controller;

import Model.User;
import Model.Librarian;
import Model.Manager;
import Model.Administrator;
import View.MainDashboardView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class LoginController {

    private Connection conn;

    public LoginController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bookstore";
            conn = DriverManager.getConnection(url, "root", "IndritFerati2604!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("No name for class com.mysql.cj.jdbc.Driver, or connection with db failed");
        }
    }

    // Validate login credentials (without hashing)
    public User validateCredentials(String username, String enteredPassword) {
        try {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");  // Retrieve stored password (plain text)

                // Debugging line to print out the plain passwords
                System.out.println("Stored Password: " + storedPassword);
                System.out.println("Entered Password: " + enteredPassword);

                // Compare the plain text passwords directly
                if (enteredPassword.equals(storedPassword)) {
                    String name = rs.getString("name");
                    LocalDate birthday = rs.getDate("birthday").toLocalDate();
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    double salary = rs.getDouble("salary");
                    String role = rs.getString("role");

                    System.out.println("Role from database: " + role);  // Debugging role

                    switch (role) {
                        case "Librarian":
                            return new Librarian(username, storedPassword, name, birthday, phone, email, salary, role);
                        case "Manager":
                            return new Manager(username, storedPassword, name, birthday, phone, email, salary, role);
                        case "Administrator":
                            return new Administrator(username, storedPassword, name, birthday, phone, email, salary, role);
                    }
                } else {
                    System.out.println("Password mismatch");
                }
            } else {
                System.out.println("No user found");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return null;
    }

    // Handle login action
    public void handleLogin(Stage stage, String username, String password) {
        User user = validateCredentials(username, password);
        if (user != null) {
            System.out.println("Login successful. Role: " + user.getRole());
            try {
                MainDashboardView dashboardView = new MainDashboardView(user.getRole(), username);
                dashboardView.start(stage);
            } catch (Exception e) {
                System.out.println("Login failed");
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
