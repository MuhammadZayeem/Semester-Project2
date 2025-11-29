package SemesterProject.Exception;
public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super("The user was not found in the system.");
    }
    public UserNotFoundException(String message) {
        super(message);
    }
}
