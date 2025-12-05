package SemesterProject;

import SemesterProject.Login.UserRoles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class User {
    private String userId;
    private String username;
    private String password;
    private String fullName;
    private String contactNumber;
    private UserRoles role;
    private LocalDateTime lastLogin;

    // Constructor
    public User(String userId, String username, String password, String fullName, String contactNumber, UserRoles role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.role = role;
    }

    // --- Core Abstract/Basic Methods ---
    public abstract String[] getAllowedActions();
    public abstract void displayDashboardGreeting();

    public boolean PasswordValidation(String attemptedPassword) {
        return this.password.equals(attemptedPassword);
    }

    public void setLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public String ShowLastLogin() {
        if (lastLogin == null) return "Never Logged In";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return lastLogin.format(formatter);
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // --- Getters (Concrete) ---
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getContactNumber() { return contactNumber; }
    public UserRoles getRole() { return role; }

    // --- Setters (Concrete) ---
    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public void setFullName(String newFullName) {
        this.fullName = newFullName;
    }
}