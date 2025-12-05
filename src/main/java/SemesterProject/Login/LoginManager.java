package SemesterProject.Login;
import SemesterProject.User; // <-- FIX: This import is required
import java.util.ArrayList;
import java.util.List;
import SemesterProject.DatabaseManager;

public class LoginManager {

    private DatabaseManager dbManager;
    private ArrayList<PasswordResetRequest> resetRequests = new ArrayList<>();

    public LoginManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    // ------------------- LOGIN -------------------
    public User login(String username, String password) {
        User u = dbManager.findUserByUsername(username);

        if (u == null) {
            System.out.println("User not found!");
            return null;
        }
        if (u.PasswordValidation(password)) {
            dbManager.updateLastLogin(username);
            u.setLastLogin();
            System.out.println("Login successful!");
            return u;
        } else {
            System.out.println("Incorrect password!");
            return null;
        }
    }

    // ------------------- FIND USER (Publicly accessible) -------------------
    public User findUser(String username) {
        return dbManager.findUserByUsername(username);
    }

    // ------------------- STAFF REQUEST PASSWORD RESET -------------------
    public void requestPasswordReset(String username) {
        User user = findUser(username);
        if (user == null) {
            System.out.println("Username not found.");
            return;
        }
        if (user.getRole()!=UserRoles.STAFF) {
            System.out.println("Only Staff can request password reset.");
            return;
        }

        for (PasswordResetRequest req : resetRequests) {
            if (req.getUsername().equalsIgnoreCase(username)) {
                System.out.println("A password reset request already exists by this user.");
                return;
            }
        }
        resetRequests.add(new PasswordResetRequest(username));
        System.out.println("Password reset request sent to Admin.");
    }

    // ------------------- ADMIN APPROVE RESET -------------------
    public boolean approvePasswordReset(String adminUsername, String staffUsername, String newPassword) {
        User admin = findUser(adminUsername);
        if (admin == null || admin.getRole() != UserRoles.ADMIN) {
            System.out.println("Only Admin can approve password reset.");
            return false;
        }

        PasswordResetRequest request = null;
        for (PasswordResetRequest req : resetRequests) {
            if (req.getUsername().equalsIgnoreCase(staffUsername)) {
                request = req;
                break;
            }
        }
        if (request == null) {
            System.out.println("No password reset request found for this staff.");
            return false;
        }

        // Use DBManager to update password
        if (dbManager.updatePassword(staffUsername, newPassword)) {
            resetRequests.remove(request); // Remove from in-memory list
            System.out.println("Password reset approved and updated by Admin.");
            return true;
        } else {
            System.out.println("Failed to update password in database.");
            return false;
        }
    }

    // ------------------- GET PENDING RESET REQUESTS -------------------
    public List<PasswordResetRequest> getPendingRequests() {
        return new ArrayList<>(resetRequests);
    }

    // ------------------- GET ALL USERS (from DB) -------------------
    public List<User> getActiveUsers() {
        return dbManager.getAllUsers();
    }

    public void showPendingRequests() {

    }
}