package test.Integrationtesting.View;

import View.LoginView;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import static org.testfx.api.FxRobot.*;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginControllerIntegrationTest extends ApplicationTest {

    private Connection conn;

    @Override
    public void start(Stage stage) throws Exception {
        LoginView loginView = new LoginView();
        loginView.start(stage);
    }

    @BeforeAll
    public void setUpDatabase() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/bookstore";
        conn = DriverManager.getConnection(url, "root", "IndritFerati2604!");
        conn.setAutoCommit(false);
        String insertUserQuery = "INSERT INTO users (username, password, name, birthday, phone, email, salary, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(insertUserQuery);
        pstmt.setString(1, "testuser");
        pstmt.setString(2, "testpassword"); // Plain text password for testing
        pstmt.setString(3, "Test User");
        pstmt.setDate(4, java.sql.Date.valueOf("1990-01-01"));
        pstmt.setString(5, "123456789");
        pstmt.setString(6, "testuser@example.com");
        pstmt.setDouble(7, 50000.0);
        pstmt.setString(8, "Librarian");
        pstmt.executeUpdate();
    }

    @Test
    public void testLoginSuccessful() {
        TextField usernameField = lookup("#usernameTextField").queryAs(TextField.class);
        PasswordField passwordField = lookup("#passwordField").queryAs(PasswordField.class);
        Button loginButton = lookup("Log in").queryAs(Button.class);

        clickOn(usernameField).write("testuser");
        clickOn(passwordField).write("testpassword");
        clickOn(loginButton);
        assertNotNull(lookup(".dashboard-view"), "MainDashboardView should be loaded");
    }

    @Test
    public void testLoginFailure() {
        TextField usernameField = lookup("#usernameTextField").queryAs(TextField.class);
        PasswordField passwordField = lookup("#passwordField").queryAs(PasswordField.class);
        Button loginButton = lookup("Log in").queryAs(Button.class);

        clickOn(usernameField).write("wronguser");
        clickOn(passwordField).write("wrongpassword");
        clickOn(loginButton);

        DialogPane dialogPane = lookup(".dialog-pane").queryAs(DialogPane.class);
        assertNotNull(dialogPane, "DialogPane should be displayed");
        assertEquals("Invalid Credentials", dialogPane.getHeaderText(), "DialogPane header should indicate invalid credentials");
        assertEquals("The username or password you entered is incorrect.", dialogPane.getContentText(), "DialogPane content should explain the error");
    }

    @AfterAll
    public void tearDownDatabase() throws Exception {
        if (conn != null) {
            conn.rollback();
            conn.close();
        }
    }
}
