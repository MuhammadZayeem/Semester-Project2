package SemesterProject.Login;

public class PasswordResetRequest {
    private String username;
    private boolean approved;

    public PasswordResetRequest(String username) {
        this.username = username;
        this.approved = false;
    }

    public String getUsername() {return username;}

}

