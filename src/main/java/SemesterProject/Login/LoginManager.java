package SemesterProject.Login;
import SemesterProject.User;
import java.util.ArrayList;
import java.util.List;
import SemesterProject.Data;

public class LoginManager {

    private Data data;
    private ArrayList<PasswordResetRequest> resetRequests = new ArrayList<>();

    public LoginManager(Data data) {

        this.data = data;
    }

    // --------------------------------------------------------------------LOGIN
    public User login(String username, String password) {
        User u = data.findUserByUsername(username);

        if (u == null) {
            //"User not found!"
            return null;
        }
        if (u.PasswordValidation(password)) {
            data.updateLastLogin(username);
            //login success
            return u;
        } else {
            //wrong password
            return null;
        }
    }

    // --------------------------------------------------------FIND USER
    public User findUser(String username) {

        return data.findUserByUsername(username);
    }

    // -------------------------------------- STAFF REQUEST PASSWORD RESET
    public void requestPasswordReset(String username) {
        User user = findUser(username);
        if (user == null) {
            //no user found
            return;
        }
        for (PasswordResetRequest req : resetRequests) {
            if (req.getUsername().equalsIgnoreCase(username)) {
                //only staff can request
                return;
            }
        }
        resetRequests.add(new PasswordResetRequest(username));
    }

    // ----------------------------------------------------------------------- ADMIN APPROVE RESET
    public boolean approvePasswordReset(String adminUsername, String staffUsername, String newPassword) {
        User admin = findUser(adminUsername);
        if (admin == null || admin.getRole() != UserRoles.ADMIN) {
            return false;
        }
        PasswordResetRequest request = null;
        for (PasswordResetRequest req : resetRequests) {
            if (req.getUsername().equalsIgnoreCase(staffUsername)) {
                request = req;
                break;
            }
        }
   if (data.updatePassword(staffUsername, newPassword)) {
            resetRequests.remove(request);
            return true;
        } else {
            //failed to update
            return false;
        }
    }

        // ----------------------------------------------------------------GET PENDING RESET REQUESTS
        public List<PasswordResetRequest> getPendingRequests () {
            return new ArrayList<>(resetRequests);
        }

        // ------------------------------------------------------GET ALL USERS
        public List<User> getActiveUsers () {
            return data.getAllUsers();
        }
    }