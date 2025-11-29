package SemesterProject.Body;


import SemesterProject.Part;
import SemesterProject.Supplier.Supplier;

public abstract class BodyPart extends Part {

    public BodyPart(String name, String carChasis, int quantity, int threshold, double unitPrice,Supplier supplier) {

        super(name,"Body", carChasis, quantity, threshold, unitPrice,supplier);
    }

    @Override
    public String getCategory() {
        return super.getCategory();
    }
}
