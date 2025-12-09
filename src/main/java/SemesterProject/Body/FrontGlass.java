package SemesterProject.Body;
import SemesterProject.Supplier.Supplier;
public class FrontGlass extends Glass {
    //--------------------------------------------------------Constructor
    public FrontGlass(String partId, String name, String carModel, int currentStock, int minThreshold, double unitPrice, Supplier supplier) {
        super(partId, name, carModel, currentStock, minThreshold, unitPrice, supplier);
    }
}