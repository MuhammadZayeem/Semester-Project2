package SemesterProject.Exception;
public class LowStockException extends Exception {

    public LowStockException() {
        super("Stock is below minimum threshold!");
    }

    public LowStockException(String message) {
        super(message);
    }
}
