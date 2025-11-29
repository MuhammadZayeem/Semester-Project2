package SemesterProject.Exception;
public class NegativeStockException extends Exception {

    public NegativeStockException() {
        super("Stock cannot go negative!");
    }

    public NegativeStockException(String message) {
        super(message);
    }
}
