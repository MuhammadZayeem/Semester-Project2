package SemesterProject.Exception;
public class InvalidLoginException extends Exception {

    public InvalidLoginException() {
        super("Invalid username or password!");
    }

    public InvalidLoginException(String message) {
        super(message);
    }
}
