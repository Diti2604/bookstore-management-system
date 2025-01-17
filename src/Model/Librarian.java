package Model;

import java.time.LocalDate;

public class Librarian extends User{
    public Librarian(String username, String password, String name, LocalDate birthday, String phone, String email, double salary, String role) {
        super(username, password, name, birthday, phone, email, salary, role);
    }
}
