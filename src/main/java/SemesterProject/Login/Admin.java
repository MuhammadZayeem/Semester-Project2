package SemesterProject.Login;
import SemesterProject.User;
import SemesterProject.Exception.UserAlreadyExistsException;
import SemesterProject.Exception.UserNotFoundException;
import SemesterProject.GUI.MainApp;

public class Admin extends User {

    public Admin(String userId, String username, String password) {
        super(userId, username, password, UserRoles.ADMIN);
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }
    public void addUser(MainApp app, User newUser) throws Exception {
        app.addUser(newUser);
    }

    public void removeUser(MainApp app, String username) throws UserNotFoundException {
        app.removeUser(username);
    }

    /*public void updateUserDetails(MainApp app, String targetUsername, String newUsername, String newFullName) throws UserNotFoundException, UserAlreadyExistsException {
        if (!app.updateUserDetails(targetUsername, newUsername, newFullName)) {
            throw new UserNotFoundException("User not found: " + targetUsername);
        }
    }*/

    public void resetUserPassword(MainApp app, String username, String newPassword) {
        User userToReset = app.findUser(username);
        if (userToReset != null) {
            if (app.adminApprovePasswordReset(username, newPassword)) {
            } else {
            }
        } else {
        }
    }
}