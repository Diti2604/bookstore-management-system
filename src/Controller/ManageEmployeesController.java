package Controller;
import Model.User;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManageEmployeesController {
    private Connection conn;

    public ManageEmployeesController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bookstore";
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean registerEmployee(User user) {
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
            e.printStackTrace();
            return false;//
        }
    }


    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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

                // Create User object and add to list
                User user = new User(username, password, name, birthday, phone, email, salary, role);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    public boolean updateUser(User user, String currentUsername) {
        String sql = "UPDATE users SET username = ?, password = ?, name = ?, " +
                "birthday = ?, phone = ?, email = ?, salary = ?, role = ? " +
                "WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());  // New username
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getEmail());
            stmt.setDouble(7, user.getSalary());
            stmt.setString(8, user.getRole());
            stmt.setString(9, currentUsername);  // Current username to identify the user to update

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
