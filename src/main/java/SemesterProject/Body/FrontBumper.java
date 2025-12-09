package SemesterProject.Body;
import SemesterProject.Supplier.Supplier;
public class FrontBumper extends Bumper {
    //--------------------------------------------------------Constructor
    public FrontBumper(String partId, String name, String carModel, int currentStock, int minThreshold, double unitPrice, Supplier supplier) {
        super(partId, name, carModel, currentStock, minThreshold, unitPrice, supplier);
    }

}