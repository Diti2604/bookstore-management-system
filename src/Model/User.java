package Model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public class User {
    protected String username;
    private String hashedPassword;
    protected String name;
    protected LocalDate birthday;
    protected String phone;
    protected String email;
    protected double salary;
    protected String role;

    public User(String username, String password, String name, LocalDate birthday, String phone, String email, double salary, String role) {
        this.username = username;
        this.hashedPassword=hashPassword(password);
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
    @Override
    public String toString() {
        return this.username;
    }
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            // Convert byte array to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Handle exception appropriately
            return null;
        }
    }
    public String getPassword() {
        return hashedPassword;
    }

    public void setPassword(String password) {
        this.hashedPassword = password;
    }
}
