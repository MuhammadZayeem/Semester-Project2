package SemesterProject.Body;
import SemesterProject.Part;
import SemesterProject.Supplier.Supplier;

public abstract class BodyPart extends Part {
    //--------------------------------------------------------------------------Constructor
    public BodyPart(String partId, String name, String carChasis, int quantity, int threshold, double unitPrice, Supplier supplier) {
        super(partId, name, "Body", carChasis, quantity, threshold, unitPrice, supplier);
    }
}