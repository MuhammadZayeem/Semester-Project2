package SemesterProject.Login;

public class PasswordResetRequest {
    private String username;     // Staff username requesting reset
    private boolean approved;    // Admin approval status

    public PasswordResetRequest(String username) {
        this.username = username;
        this.approved = false;
    }

    public String getUsername() {
        return username;
    }

    public boolean isApproved() {
        return approved;
    }

    public void approve() {
        this.approved = true;
    }
}

