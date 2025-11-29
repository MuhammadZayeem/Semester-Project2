package SemesterProject.Login;
import SemesterProject.User;
import java.time.LocalDateTime;
import SemesterProject.Exception.UserAlreadyExistsException;
import SemesterProject.Exception.UserNotFoundException;

public class Admin extends User {

    public Admin(String userId, String username, String password, String fullName, String contactNumber) {
        super(userId, username, password, fullName, contactNumber,UserRoles.ADMIN);
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

    //Add a new user to the system
    public void addUser(User[] users, User newUser)throws UserAlreadyExistsException, Exception{
        for (int i=0;i<users.length;i++) {
            if (users[i]!=null&&users[i].getUsername().equalsIgnoreCase(newUser.getUsername())) {
                throw new UserAlreadyExistsException("Username already exists: "+newUser.getUsername());
            }
        }

        for (int i=0;i<users.length;i++) {
            if(users[i]==null) {
                users[i]=newUser;
                System.out.println("User added successfully: "+newUser.getUsername());
                return;
            }
        }
        throw new Exception("User list is full. Cannot add more users.");
    }

    // Remove a user
    public void removeUser(User[] users, String username) throws UserNotFoundException,Exception{
        for (int i=0;i<users.length;i++) {
            if (users[i]!=null && users[i].getUsername().equalsIgnoreCase(username)) {
                users[i] = null;
                System.out.println("User removed successfully: "+username);
                return;
            }
        }
        throw new UserNotFoundException("User not found: "+username);
    }

    //Reset user's password
    public void resetUserPassword(User user, String newPassword) {
        user.updatePassword(newPassword);
        System.out.println("Password reset successfully for: " + user.getUsername());
    }

    //View user profile
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