package SemesterProject.Body;

public class RearGlass extends Glass {

    public RearGlass(String chasisModel,
                     int quantity, int threshold, double unitPrice) {

        super("Rear Screen", chasisModel, quantity, threshold, unitPrice);
    }

    @Override
    public void displayDetails() {
        System.out.println("-------- REAR SCREEN DETAILS --------");
        System.out.println("Car Model: " + getCarModel());
        System.out.println("Quantity: " + getQuantity());
        System.out.println("Unit Price: " + getUnitPrice());
        System.out.println("-------------------------------------");
    }
}
