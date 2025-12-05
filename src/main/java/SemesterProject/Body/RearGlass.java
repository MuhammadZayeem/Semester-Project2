package SemesterProject.Body;
import SemesterProject.Supplier.Supplier;
public class RearGlass extends Glass {
    public RearGlass(String partId, String name, String carModel, int currentStock, int minThreshold, double unitPrice, Supplier supplier) {
        super(partId, name, carModel, currentStock, minThreshold, unitPrice, supplier);
    }
    @Override public void displayDetails() {}
}