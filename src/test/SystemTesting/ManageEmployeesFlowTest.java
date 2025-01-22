package test.SystemTesting;

import Controller.LoginController;
import Model.Book;
import Model.User;
import View.LoginView;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;
import java.util.Random;

import static javafx.beans.binding.Bindings.select;
import static org.junit.jupiter.api.Assertions.*;

public class ManageEmployeesFlowTest extends ApplicationTest {
    private LoginController loginController;
    private LoginView loginView;
    private Stage stage;
    private Connection connection;
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        loginView = new LoginView();
        loginView.start(stage); // Initial view to start with
    }

    @BeforeEach
    public void setUp() {
        loginController = new LoginController(connection);
    }

    @Test
    public void testRegisterCorrect() {
        // Simulate entering credentials and clicking login button
        clickOn(".text-field").write("admin1"); // Enter username
        clickOn(".password-field").write("admin1"); // Enter password
        clickOn(".button"); // Click login button

        WaitForAsyncUtils.waitForFxEvents();

        // Verify login success
        User user = loginController.validateCredentials("admin1", "admin1");
        assertNotNull(user, "Login should succeed with correct credentials");

        // Navigate to Create Bill
        clickOn("#actionComboBox"); // Click on the combo box
        clickOn("Manage Employees"); // Select "Create Bill" option

        WaitForAsyncUtils.waitForFxEvents();

        // Add assertions to verify that the Create Bill view is displayed
        // For example, check for the presence of a specific element in the Create Bill view
        assertTrue(lookup("Manage Employees").tryQuery().isPresent(), "Manage Employees view should be displayed");

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#manageEmployeesComboBox");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Register");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#usernameTextField").write("test");
        clickOn("#passwordField").write("Test1");
        clickOn("#nameTextField").write("test");
        clickOn("#birthdayDatePicker").write("1/1/2000");
        clickOn("#phoneTextField").write("0682341220");
        clickOn("#emailTextField").write("test@gmail.com");
        clickOn("#salaryTextField").write("50000");
        clickOn("#roleComboBox").clickOn("Administrator");
        clickOn("#registerButton");

        // Verify alert popup
        // Verify the alert is present
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");

        // Verify alert popup
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");


    }

    @Test
    public void testModifyCorrect() {
        // Simulate entering credentials and clicking login button
        clickOn(".text-field").write("admin1"); // Enter username
        clickOn(".password-field").write("admin1"); // Enter password
        clickOn(".button"); // Click login button

        WaitForAsyncUtils.waitForFxEvents();

        // Verify login success
        User user = loginController.validateCredentials("admin1", "admin1");
        assertNotNull(user, "Login should succeed with correct credentials");

        // Navigate to Create Bill
        clickOn("#actionComboBox"); // Click on the combo box
        clickOn("Manage Employees"); // Select "Create Bill" option

        WaitForAsyncUtils.waitForFxEvents();

        // Add assertions to verify that the Create Bill view is displayed
        // For example, check for the presence of a specific element in the Create Bill view
        assertTrue(lookup("Manage Employees").tryQuery().isPresent(), "Manage Employees view should be displayed");

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#manageEmployeesComboBox");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Modify");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#managerComboBox");
        clickOn("#librarianComboBox");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#userComboBox").clickOn("librarian1");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#fetchButton");


        clickOn("#salaryTextField").eraseText(7).write(Math.random() * 100000 + "");
        clickOn("#updateButton");

        // Verify alert popup
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");

    }
    @Test
    public void testDeleteCorrect() {
        // Simulate entering credentials and clicking login button
        clickOn(".text-field").write("admin1"); // Enter username
        clickOn(".password-field").write("admin1"); // Enter password
        clickOn(".button"); // Click login button

        WaitForAsyncUtils.waitForFxEvents();

        // Verify login success
        User user = loginController.validateCredentials("admin1", "admin1");
        assertNotNull(user, "Login should succeed with correct credentials");

        // Navigate to Create Bill
        clickOn("#actionComboBox"); // Click on the combo box
        clickOn("Manage Employees"); // Select "Create Bill" option

        WaitForAsyncUtils.waitForFxEvents();

        // Add assertions to verify that the Create Bill view is displayed
        // For example, check for the presence of a specific element in the Create Bill view
        assertTrue(lookup("Manage Employees").tryQuery().isPresent(), "Manage Employees view should be displayed");

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#manageEmployeesComboBox");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Delete");
        WaitForAsyncUtils.waitForFxEvents();



        clickOn("#userComboBoxDelete").clickOn("test");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#deleteButton");

        // Verify alert popup
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");

    }

    @Test
    public void testRegisterInvalidEmail() {
        // Simulate login
        clickOn(".text-field").write("admin1");
        clickOn(".password-field").write("admin1");
        clickOn(".button");

        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Manage Employees -> Register
        clickOn("#actionComboBox");
        clickOn("Manage Employees");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#manageEmployeesComboBox");
        clickOn("Register");

        WaitForAsyncUtils.waitForFxEvents();

        // Fill in registration fields with invalid email
        clickOn("#usernameTextField").write("test");
        clickOn("#passwordField").write("Test1");
        clickOn("#nameTextField").write("test");
        clickOn("#birthdayDatePicker").write("01/01/2000");
        clickOn("#phoneTextField").write("0682341220");
        clickOn("#emailTextField").write("invalid-email");
        clickOn("#salaryTextField").write("50000");
        clickOn("#roleComboBox").clickOn("Administrator");

        clickOn("#registerButton");

        // Verify alert popup for invalid email
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed");
    }
    @Test
    public void testRegisterInvalidPassword() {
        // Simulate login
        clickOn(".text-field").write("admin1");
        clickOn(".password-field").write("admin1");
        clickOn(".button");

        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Manage Employees -> Register
        clickOn("#actionComboBox");
        clickOn("Manage Employees");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#manageEmployeesComboBox");
        clickOn("Register");

        WaitForAsyncUtils.waitForFxEvents();

        // Fill in registration fields with invalid password
        clickOn("#usernameTextField").write("test");
        clickOn("#passwordField").write(""); // Empty password
        clickOn("#nameTextField").write("test");
        clickOn("#birthdayDatePicker").write("01/01/2000");
        clickOn("#phoneTextField").write("0682341220");
        clickOn("#emailTextField").write("test@gmail.com");
        clickOn("#salaryTextField").write("50000");
        clickOn("#roleComboBox").clickOn("Administrator");

        clickOn("#registerButton");

        // Verify alert popup for invalid password
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed for invalid password");
    }

    @Test
    public void testRegisterInvalidUsername() {
        // Simulate login
        clickOn(".text-field").write("admin1");
        clickOn(".password-field").write("admin1");
        clickOn(".button");

        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Manage Employees -> Register
        clickOn("#actionComboBox");
        clickOn("Manage Employees");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#manageEmployeesComboBox");
        clickOn("Register");

        WaitForAsyncUtils.waitForFxEvents();

        // Fill in registration fields with invalid username
        clickOn("#usernameTextField").write(""); // Empty username
        clickOn("#passwordField").write("Test1");
        clickOn("#nameTextField").write("test");
        clickOn("#birthdayDatePicker").write("01/01/2000");
        clickOn("#phoneTextField").write("0682341220");
        clickOn("#emailTextField").write("test@gmail.com");
        clickOn("#salaryTextField").write("50000");
        clickOn("#roleComboBox").clickOn("Administrator");

        clickOn("#registerButton");

        // Verify alert popup for invalid username
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed for invalid username");
    }

    @Test
    public void testRegisterInvalidName() {
        // Simulate login
        clickOn(".text-field").write("admin1");
        clickOn(".password-field").write("admin1");
        clickOn(".button");

        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Manage Employees -> Register
        clickOn("#actionComboBox");
        clickOn("Manage Employees");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#manageEmployeesComboBox");
        clickOn("Register");

        WaitForAsyncUtils.waitForFxEvents();

        // Fill in registration fields with invalid name
        clickOn("#usernameTextField").write("test");
        clickOn("#passwordField").write("Test1");
        clickOn("#nameTextField").write(""); // Empty name
        clickOn("#birthdayDatePicker").write("01/01/2000");
        clickOn("#phoneTextField").write("0682341220");
        clickOn("#emailTextField").write("test@gmail.com");
        clickOn("#salaryTextField").write("50000");
        clickOn("#roleComboBox").clickOn("Administrator");

        clickOn("#registerButton");

        // Verify alert popup for invalid name
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed for invalid name");
    }

    @Test
    public void testRegisterInvalidPhone() {
        // Simulate login
        clickOn(".text-field").write("admin1");
        clickOn(".password-field").write("admin1");
        clickOn(".button");

        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Manage Employees -> Register
        clickOn("#actionComboBox");
        clickOn("Manage Employees");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#manageEmployeesComboBox");
        clickOn("Register");

        WaitForAsyncUtils.waitForFxEvents();

        // Fill in registration fields with invalid phone
        clickOn("#usernameTextField").write("test");
        clickOn("#passwordField").write("Test1");
        clickOn("#nameTextField").write("test");
        clickOn("#birthdayDatePicker").write("01/01/2000");
        clickOn("#phoneTextField").write("invalid-phone"); // Invalid phone
        clickOn("#emailTextField").write("test@gmail.com");
        clickOn("#salaryTextField").write("50000");
        clickOn("#roleComboBox").clickOn("Administrator");

        clickOn("#registerButton");

        // Verify alert popup for invalid phone
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed for invalid phone");
    }

    @Test
    public void testRegisterInvalidSalary() {
        // Simulate login
        clickOn(".text-field").write("admin1");
        clickOn(".password-field").write("admin1");
        clickOn(".button");

        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Manage Employees -> Register
        clickOn("#actionComboBox");
        clickOn("Manage Employees");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#manageEmployeesComboBox");
        clickOn("Register");

        WaitForAsyncUtils.waitForFxEvents();

        // Fill in registration fields with invalid salary
        clickOn("#usernameTextField").write("test");
        clickOn("#passwordField").write("Test1");
        clickOn("#nameTextField").write("test");
        clickOn("#birthdayDatePicker").write("01/01/2000");
        clickOn("#phoneTextField").write("0682341220");
        clickOn("#emailTextField").write("test@gmail.com");
        clickOn("#salaryTextField").write("invalid-salary"); // Invalid salary
        clickOn("#roleComboBox").clickOn("Administrator");

        clickOn("#registerButton");

        // Verify alert popup for invalid salary
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should be displayed for invalid salary");
    }

    @Test
    public void testModifyInvalidSalary() {
        // Simulate login
        clickOn(".text-field").write("admin1");
        clickOn(".password-field").write("admin1");
        clickOn(".button");

        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Manage Employees -> Modify
        clickOn("#actionComboBox");
        clickOn("Manage Employees");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#manageEmployeesComboBox");
        clickOn("Modify");

        WaitForAsyncUtils.waitForFxEvents();

        // Select user and modify salary with invalid value
        clickOn("#userComboBox").clickOn("librarian1");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#fetchButton");
        clickOn("#salaryTextField").eraseText(7).write("invalid"); // Invalid salary
        clickOn("#updateButton");

        // Verify alert popup for invalid salary
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should indicate invalid salary");
    }


    @Test
    public void testModifyEmptyFields() {
        // Simulate login
        clickOn(".text-field").write("admin1");
        clickOn(".password-field").write("admin1");
        clickOn(".button");

        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Manage Employees -> Modify
        clickOn("#actionComboBox");
        clickOn("Manage Employees");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#manageEmployeesComboBox");
        clickOn("Modify");

        WaitForAsyncUtils.waitForFxEvents();

        // Select user and clear fields
        clickOn("#userComboBox").clickOn("librarian1");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#fetchButton");
        clickOn("#salaryTextField").eraseText(7); // Leave salary empty
        clickOn("#updateButton");

        // Verify alert popup for empty fields
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should indicate empty fields");
    }
    @Test
    public void testDeleteUserNotSelected() {
        // Simulate login
        clickOn(".text-field").write("admin1");
        clickOn(".password-field").write("admin1");
        clickOn(".button");

        WaitForAsyncUtils.waitForFxEvents();

        // Navigate to Manage Employees -> Delete
        clickOn("#actionComboBox");
        clickOn("Manage Employees");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#manageEmployeesComboBox");
        clickOn("Delete");

        WaitForAsyncUtils.waitForFxEvents();

        // Attempt to delete without selecting a user
        clickOn("#deleteButton");

        // Verify alert popup for no user selected
        assertTrue(lookup(".alert").tryQuery().isPresent(), "Alert popup should indicate no user selected for deletion");
    }

}