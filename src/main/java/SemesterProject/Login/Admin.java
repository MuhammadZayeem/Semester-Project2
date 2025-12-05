package SemesterProject.Login;
import SemesterProject.User;
import java.time.LocalDateTime;
import SemesterProject.Exception.UserAlreadyExistsException;
import SemesterProject.Exception.UserNotFoundException;
import SemesterProject.GUI.MainApp;

public class Admin extends User {

    public Admin(String userId, String username, String password, String fullName, String contactNumber) {
        super(userId, username, password, fullName, contactNumber,UserRoles.ADMIN);
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }


    @Override
    public String[] getAllowedActions() {
        return new String[]{
                "Add User",
                "Remove User",
                "Update Inventory",
                "Generate Reports",
                "View Demand List",
                "Backup Database",
                "Restore Database"
        };
    }

    @Override
    public void displayDashboardGreeting() {
        System.out.println("===========================================");
        System.out.println("Welcome Admin: "+getFullName());
        System.out.println("Last Login : "+ShowLastLogin());
        System.out.println("You have full access to the system.");
        System.out.println("===========================================\n");
    }

    // --- DELEGATION METHODS (Admin Exclusive, calling MainApp) ---

    // Delegation to MainApp (which calls DatabaseManager)
    public void addUser(MainApp app, User newUser) throws Exception {
        app.addUser(newUser);
    }

    // Delegation to MainApp (which calls DatabaseManager)
    public void removeUser(MainApp app, String username) throws UserNotFoundException {
        app.removeUser(username);
    }

    // Delegation to MainApp (which calls DatabaseManager)
    public void updateUserDetails(MainApp app, String targetUsername, String newUsername, String newFullName) throws UserNotFoundException, UserAlreadyExistsException {
        if (!app.updateUserDetails(targetUsername, newUsername, newFullName)) {
            // This assumes updateUserDetails throws exception if username conflict, or returns false if user not found.
            throw new UserNotFoundException("User not found: " + targetUsername);
        }
    }

    // Delegation to MainApp (for direct password reset)
    public void resetUserPassword(MainApp app, String username, String newPassword) {
        User userToReset = app.findUser(username);
        if (userToReset != null) {
            // FIX: Calling the correct method in MainApp with the new signature
            if (app.adminApprovePasswordReset(username, newPassword)) {
                System.out.println("Direct password reset successful for: " + username);
            } else {
                System.out.println("Direct password reset failed for: " + username);
            }
        } else {
            System.out.println("User not found: " + username);
        }
    }

    // --- Existing Methods ---

    public void viewUserProfile(User user) {
        System.out.println("\n================ USER PROFILE =================");
        System.out.println("Name      : "+user.getFullName());
        System.out.println("Username  : "+user.getUsername());
        System.out.println("Contact   : "+user.getContactNumber());
        System.out.println("Last Login: "+user.ShowLastLogin());
        System.out.println("==============================================\n");
    }

    public void showAdminDashboard() {
        displayDashboardGreeting();
        System.out.println("Available Actions:");
        String[] actions = getAllowedActions();
        for (int i=0;i<actions.length;i++) {
            System.out.println((i+1)+". "+ actions[i]);
        }
        System.out.println();
    }

    public void approveStaffPassword(LoginManager manager, String staffUsername, String newPassword) {
        manager.approvePasswordReset(getUsername(), staffUsername, newPassword);
    }

    public void viewPendingRequests(LoginManager manager) {
        manager.showPendingRequests();
    }
    public void changeOwnPassword(String oldPassword, String newPassword) {
        if (!PasswordValidation(oldPassword)) {
            System.out.println("Old password is incorrect!");
            return;
        }
        updatePassword(newPassword);
        System.out.println("Your password has been successfully changed.");
    }
}