package test.UnitTesting;

import Model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {
    @Test
    void testValidInputHashing() {
        String password = "password123";
        String hashedPassword = User.hashPassword(password);
        assertNotNull(hashedPassword);
        assertFalse(hashedPassword.isEmpty());
        System.out.println(hashedPassword);
    }

    @Test
    void testEmptyValueHashing() {
        String password = "";
        assertThrows(IllegalArgumentException.class,
                () -> User.hashPassword(password));
    }

    @Test
    void testInvalidCharactersInPassword() {
        String invalidPassword = "!!!@@@...,,,***";
        assertThrows(IllegalArgumentException.class,
                () -> User.hashPassword(invalidPassword));
    }

}
