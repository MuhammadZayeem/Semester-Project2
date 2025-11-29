package SemesterProject.Body;

public class DoorGlass extends Glass {

    public DoorGlass(String chasisModel,
                     int quantity, int threshold, double unitPrice) {

        super("Door Glass", chasisModel, quantity, threshold, unitPrice);
    }

    @Override
    public void displayDetails() {
        System.out.println("-------- DOOR GLASS DETAILS --------");
        System.out.println("Car Model: " + getCarModel());
        System.out.println("Quantity: " + getQuantity());
        System.out.println("Unit Price: " + getUnitPrice());
        System.out.println("------------------------------------");
    }
}
