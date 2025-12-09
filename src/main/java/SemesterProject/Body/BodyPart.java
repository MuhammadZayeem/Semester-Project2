package SemesterProject.Body;

import SemesterProject.Part;
import SemesterProject.Supplier.Supplier;

public abstract class BodyPart extends Part {
    // Corrected constructor to accept 7 args and pass them up (with category "Body")
    public BodyPart(String partId, String name, String carChasis, int quantity, int threshold, double unitPrice, Supplier supplier) {
        super(partId, name, "Body", carChasis, quantity, threshold, unitPrice, supplier);
    }

    @Override
    public String getCategory() { return super.getCategory(); }
}