package SemesterProject.Body;
import SemesterProject.Supplier.Supplier;

public abstract class Bumper extends BodyPart {
    //-----------------------------------------------------------Constructor
    public Bumper(String partId, String name, String carChasis, int quantity, int threshold, double price, Supplier supplier){
        super(partId, name, carChasis, quantity, threshold, price, supplier);
    }
}