package SemesterProject.Body;

import SemesterProject.Supplier.Supplier;

public class FrontLaminatedGlass extends Glass {
    //--------------------------------------------------------Constructor
    public FrontLaminatedGlass(String partId, String name, String carModel, int currentStock, int minThreshold, double unitPrice, Supplier supplier) {
        super(partId, name, carModel, currentStock, minThreshold, unitPrice, supplier);
    }
}