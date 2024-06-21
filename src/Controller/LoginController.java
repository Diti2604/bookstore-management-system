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

import java.sql.*;
import java.time.LocalDate;

public class LoginController {

    private Connection conn;

    public LoginController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bookstore";
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public User validateCredentials(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                LocalDate birthday = rs.getDate("birthday").toLocalDate();
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                double salary = rs.getDouble("salary");
                String role = rs.getString("role");

                switch (role) {
                    case "Librarian":
                        String librarianUsername = rs.getString("username");
                        return new Librarian(username, password, name, birthday, phone, email, salary, role);
                    case "Manager":
                        return new Manager(username, password, name, birthday, phone, email, salary, role);
                    case "Administrator":
                        return new Administrator(username, password, name, birthday, phone, email, salary, role);
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
