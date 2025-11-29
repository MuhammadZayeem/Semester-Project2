package SemesterProject.Login;
import SemesterProject.User;
import java.util.ArrayList;

public class LoginManager {

    private User[] users;
    private ArrayList<PasswordResetRequest>resetRequests=new ArrayList<>();

    public LoginManager(User[] users) {
        this.users = users;
    }

    // ------------------- LOGIN -------------------
    public User login(String username, String password) {
        User u=findUser(username);
        if (u==null) {
            System.out.println("User not found!");
            return null;
        }
        if (u.PasswordValidation(password)) {
            u.setLastLogin();
            System.out.println("Login successful!");
            return u;
        } else {
            System.out.println("Incorrect password!");
            return null;
        }
    }

    // ------------------- FIND USER -------------------
    private User findUser(String username) {
        for (User u:users) {
            if (u!=null && u.getUsername().equalsIgnoreCase(username)){
                return u;
            }
        }
        return null;
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
    public void approvePasswordReset(String adminUsername, String staffUsername, String newPassword) {
        User admin=findUser(adminUsername);
        if (admin == null||admin.getRole() != UserRoles.ADMIN) {
            System.out.println("Only Admin can approve password reset.");
            return;
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
            return;
        }

        User staff=findUser(staffUsername);
        if (staff==null) {
            System.out.println("Staff not found.");
            return;
        }

        staff.updatePassword(newPassword);
        resetRequests.remove(request);
        System.out.println("Password reset approved and updated by Admin.");
    }
    // ------------------- SHOW USERS -------------------
    public void showAllUsers() {
        System.out.println("\n======= REGISTERED USERS =======");
        for (User u : users) {
            if (u!=null) {
                System.out.println(u.getUsername() + " (" + u.getRole() + ")");
            }
        }
        System.out.println("================================\n");
    }
    // ------------------- SHOW PENDING RESET REQUESTS -------------------
    public void showPendingRequests() {
        if (resetRequests.isEmpty()) {
            System.out.println("No pending password reset requests.");
            return;
        }
        System.out.println("\n--- Pending Password Reset Requests ---");
        for (PasswordResetRequest req : resetRequests) {
            System.out.println(req.getUsername());
        }
        System.out.println("-------------------------------------\n");
    }
}

