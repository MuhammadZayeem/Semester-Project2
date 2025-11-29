package SemesterProject.Exception;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException() {
        super("The user already exists in the system.");
    }
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}