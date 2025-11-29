package SemesterProject.Body;

import SemesterProject.Supplier.LocalSupplier;
import SemesterProject.Supplier.Supplier;

public class FrontLaminatedGlass extends Glass {
    public FrontLaminatedGlass(String carChasis, int quantity, int threshold, double unitPrice) {

        super("FLG", carChasis, quantity, threshold, unitPrice);
    }

    @Override
    public void displayDetails() {
        System.out.println("-------- FRONT SCREEN DETAILS --------");
        System.out.println("Car Model: " + getCarModel());
        System.out.println("PRODUCT NAME: " + getName());
        System.out.println("Quantity: " + getQuantity());
        System.out.println("Unit Price: " + getUnitPrice());
        System.out.println("Stoke value " + calculateStockValue());
        System.out.println("--------------------------------------");
    }
}

