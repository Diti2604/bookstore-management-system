package Controller;
import Model.User;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class ManageEmployeesController {
    private Connection conn;

    public ManageEmployeesController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bookstore";
            String user = System.getenv("root");
            String password = System.getenv("IndritFerati2604!");
            conn = DriverManager.getConnection(url, "root", "IndritFerati2604!");
            System.out.println("Connected to the database!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("No name for class com.mysql.cj.jdbc.Driver, or connection with db failed");
        }
    }

    public ManageEmployeesController(Connection mockConnection) {
        conn = mockConnection;
    }


    public boolean registerEmployee(User user) {
        // Calculate the age from the birthday
        int age = Period.between(user.getBirthday(), LocalDate.now()).getYears();

        // Check if age is between 18 and 65
        if (age < 18 || age > 65) {
            showAlert("Invalid Age", "The age of the user must be between 18 and 65.");
            return false; // Exit registration process if age is not valid
        }
        if (isUsernameExists(user.getUsername())) {
            showAlert("Duplicate Username", "Username '" + user.getUsername() + "' already exists.");
            return false; // Exit registration process
        }
        String sql = "INSERT INTO users (username, password, name, birthday, phone, email, salary, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getEmail());
            stmt.setDouble(7, user.getSalary());
            stmt.setString(8, user.getRole());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting Employee in db");
            return false;
        }
    }


    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting User from db");
            return false;
        }
    }


    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String name = rs.getString("name");
                LocalDate birthday = rs.getDate("birthday").toLocalDate();
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                double salary = rs.getDouble("salary");

                User user = new User(username, password, name, birthday, phone, email, salary, role);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user from DB");
        }
        return users;
    }
    public boolean updateUser(User user, String currentUsername) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (!user.getPassword().matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Password must contain only letters and numbers");
        }

        String sql = "UPDATE users SET username = ?, password = ?, name = ?, " +
                "birthday = ?, phone = ?, email = ?, salary = ?, role = ? " +
                "WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getEmail());
            stmt.setDouble(7, user.getSalary());
            stmt.setString(8, user.getRole());
            stmt.setString(9, currentUsername);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user in db");
            return false;
        }
    }
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) AS count FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error from sql checking if username exists");
        }
        return false;
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
