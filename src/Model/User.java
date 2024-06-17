package Model;

import java.time.LocalDate;

public abstract class User {
    protected String username;
    protected String password;
    protected String name;
    protected LocalDate birthday;
    protected String phone;
    protected String email;
    protected double salary;
    protected String role;

    public User(String username, String password, String name, LocalDate birthday, String phone, String email, double salary, String role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.salary = salary;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
