package test.Integrationtesting.View;

import Controller.ManageEmployeesController;
import Model.User;
import View.ManageEmployeesView;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.framework.junit.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class ManageEmployeesIntegrationTest extends ApplicationTest {
    private ManageEmployeesView view;
    private Connection conn;
    private ManageEmployeesController controller;

    @Before
    public void setUp() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/bookstore";
        conn = DriverManager.getConnection(url, "root", "IndritFerati2604!");
        conn.setAutoCommit(false);
        controller = new ManageEmployeesController(conn);
    }

    @Override
    public void start(Stage stage) throws Exception {
        view = new ManageEmployeesView(controller);
        view.start(stage);
    }

    @After
    public void tearDown() throws SQLException {
        if (conn != null) {
            conn.rollback();
            conn.close();
        }
    }

    @Test
    public void testEmployeeRegistration() {
        clickOn(".combo-box").clickOn("Register");
        clickOn("#usernameTextField").write("testuser");
        clickOn("#passwordField").write("password123");
        clickOn("#nameTextField").write("TestUser");
        DatePicker db=lookup("#birthdayDatePicker").query();
        db.setValue(LocalDate.of(1990,1,1));
        clickOn("#phoneTextField").write("0681234567");
        clickOn("#emailTextField").write("test@gmail.com");
        clickOn("#salaryTextField").write("50000");
        clickOn("#roleComboBox").clickOn("Manager");
        clickOn("#registerButton");

        DialogPane alertDialog = lookup(".dialog-pane").queryAs(DialogPane.class);
        String alertText = alertDialog.getContentText();
        assertEquals("Employee registered successfully.", alertText);
        User user = controller.getUsersByRole("manager").stream()
                .filter(u -> u.getUsername().equals("testuser"))
                .findFirst()
                .orElse(null);
        assertNotNull("User should exist in database", user);
        assertEquals("TestUser", user.getName());
    }


    @Test
    public void testEmployeeModification() {
        Platform.runLater(() -> {
            User testUser = new User("modifytest" + LocalTime.now(), "pass123", "Modify Test",
                    LocalDate.of(1990, 1, 1), "0681234567", "modify@gmail.com",
                    60000, "manager");
            controller.registerEmployee(testUser);
        });

        clickOn("#actionComboBox").clickOn("Modify");
        sleep(1000);

        clickOn("#userComboBox").clickOn("modifytest");

        clickOn("#fetchButton");
        clickOn("#nameTextField").eraseText(11).write("Modified Name");
        clickOn("#salaryTextField").eraseText(9).write("65000");
        clickOn("#updateButton");

        DialogPane alertDialog = lookup(".dialog-pane").queryAs(DialogPane.class);
        String alertText = alertDialog.getContentText();
        assertEquals("Employee details updated successfully.", alertText);

        sleep(1000);
        Platform.runLater(() -> {
            User modifiedUser = controller.getUsersByRole("manager").stream()
                    .filter(u -> u.getUsername().equals("modifytest"))
                    .findFirst()
                    .orElse(null);
            assertNotNull("Modified user should exist", modifiedUser);
            assertEquals("ModifiedName", modifiedUser.getName());
            assertEquals(65000, modifiedUser.getSalary(), 0.01);
        });
    }


    @Test
    public void testEmployeeDeletion() {
        // Register a test user first
        Platform.runLater(() -> {
            User testUser = new User("deletetest", "pass123", "Delete Test",
                    LocalDate.of(1990, 1, 1), "0681234567", "delete@gmail.com",
                    55000, "manager");
            controller.registerEmployee(testUser);
        });
        clickOn("#actionComboBox").clickOn("Delete");
        sleep(2000);
        clickOn("#userComboBox").clickOn("deletetest");
        clickOn("#deleteButton");
        verifyThat(".alert", hasText("Employee deleted successfully."));
        Platform.runLater(() -> {
            assertTrue("User should not exist in database",
                    controller.getUsersByRole("manager").stream()
                            .noneMatch(u -> u.getUsername().equals("deletetest")));
        });
    }

    @Test
    public void testValidations() {
        clickOn(".combo-box").clickOn("Register");
        clickOn("#usernameTextField").write("test@user");
        clickOn("#registerButton");
        DialogPane alertDialog = lookup(".dialog-pane").queryAs(DialogPane.class);
        String alertText = alertDialog.getContentText();
        assertEquals("Username should only contain letters and numbers.", alertText);


        sleep(300);


        clickOn((Node) lookup(".button").query());


        clickOn("#usernameTextField").eraseText(12).write("testuser");
        clickOn("#emailTextField").write("invalid-email");
        clickOn("#registerButton");
        DialogPane alertDialog_1 = lookup(".dialog-pane").queryAs(DialogPane.class);
        String alertText_1 = alertDialog.getContentText();
        assertEquals("Email should be a valid Gmail address.", alertText_1);
        clickOn("#emailTextField").eraseText(12).write("test@gmail.com");
        clickOn("#phoneTextField").eraseText(10).write("123456");
        clickOn("#registerButton");
        DialogPane alertDialog_2 = lookup(".dialog-pane").queryAs(DialogPane.class);
        String alertText_2 = alertDialog.getContentText();
        assertEquals("Phone number must start with 068, 069, or 067 and be 10 digits long.", alertText_2);
    }


}
