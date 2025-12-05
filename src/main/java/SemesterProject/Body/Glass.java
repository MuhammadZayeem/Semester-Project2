package SemesterProject.Body;

import SemesterProject.Supplier.LocalSupplier;
import SemesterProject.Supplier.Supplier;

public abstract class Glass extends BodyPart {
    public Glass(String partId, String name, String carChasis, int quantity, int threshold, double unitPrice, Supplier supplier) {
        super(partId, name, carChasis, quantity, threshold, unitPrice, supplier);
    }
}