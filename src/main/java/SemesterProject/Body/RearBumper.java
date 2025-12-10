package SemesterProject.Body;
import SemesterProject.Supplier.Supplier;
public class RearBumper extends Bumper {
    //--------------------------------------------------------Constructor
    public RearBumper(String partId, String name, String carModel, int currentStock, int minThreshold, double unitPrice, Supplier supplier) {
        super(partId, name, carModel, currentStock, minThreshold, unitPrice, supplier);
    }
}