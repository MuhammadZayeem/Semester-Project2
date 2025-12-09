package SemesterProject.Login;
import SemesterProject.User;

public class Staff extends User {

    public Staff(String userId, String username, String password, String fullName, String contactNumber) {
        super(userId, username, password, UserRoles.STAFF);
    }
    @Override
    public String getPassword() {
        return super.getPassword();
    }
}