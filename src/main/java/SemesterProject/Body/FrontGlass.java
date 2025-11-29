package SemesterProject.Body;

public class FrontGlass extends Glass {

    public FrontGlass(String chasisModel, int quantity, int threshold, double unitPrice) {

        super("FG", chasisModel, quantity, threshold, unitPrice);
    }
    @Override
    public void displayDetails() {
        System.out.println("-------- FRONT SCREEN DETAILS --------");
        System.out.println("Car Model: " + getCarModel());
        System.out.println("Quantity: " + getQuantity());
        System.out.println("Unit Price: " + getUnitPrice());
        System.out.println("--------------------------------------");

    }
}
