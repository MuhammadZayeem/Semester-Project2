package SemesterProject;

import SemesterProject.Login.UserRoles;


public abstract class User {
    private String userId;
    private String username;
    private String password;
    private UserRoles role;

    // --------------------------------------------------------------------------------Constructor
    public User(String userId, String username, String password, UserRoles role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }


    public boolean PasswordValidation(String attemptedPassword) {
        return this.password.equals(attemptedPassword);
    }


    // ----------------------------------------------------------------------Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public UserRoles getRole() { return role; }

    // --- Setters (Concrete) ---
    public void setUsername(String newUsername) {
        this.username = newUsername;
    }
}