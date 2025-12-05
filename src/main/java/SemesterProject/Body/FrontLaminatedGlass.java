package SemesterProject.Body;

import SemesterProject.Supplier.Supplier;

public class FrontLaminatedGlass extends Glass {
    public FrontLaminatedGlass(String partId, String name, String carModel, int currentStock, int minThreshold, double unitPrice, Supplier supplier) {
        super(partId, name, carModel, currentStock, minThreshold, unitPrice, supplier);
    }

    @Override
    public void displayDetails() {
        System.out.println("Front Laminated Glass: " + getName());
    }
}