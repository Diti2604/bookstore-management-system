package Model;

import java.time.LocalDate;

public class Manager extends User{
    public Manager(String username, String password, String name, LocalDate birthday, String phone, String email, double salary, String role) {
        super(username, password, name, birthday, phone, email, salary, role);
    }
}
