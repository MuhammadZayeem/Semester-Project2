package SemesterProject.Exception;
public class UserCreationException extends Exception {

    public UserCreationException()
    {
        super("User not added!");
    }

    public UserCreationException(String message) {
        super(message);
    }
}
