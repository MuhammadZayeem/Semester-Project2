package SemesterProject.Body;

import SemesterProject.Supplier.Supplier;

public abstract class Glass extends BodyPart {
    // Corrected constructor to accept 7 args and pass them up
    public Glass(String partId, String name, String carChasis, int quantity, int threshold, double unitPrice, Supplier supplier) {
        super(partId, name, carChasis, quantity, threshold, unitPrice, supplier);
    }
}