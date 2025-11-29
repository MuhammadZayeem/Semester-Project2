package SemesterProject.Body;


import SemesterProject.Supplier.LocalSupplier;
import SemesterProject.Supplier.Supplier;
import SemesterProject.Supplier.LocalSupplier;
//import SemesterProject.Supplier.Main;
public abstract class Glass extends BodyPart {
    public Glass(String name, String carChasis, int quantity, int threshold, double unitPrice) {

        super(name, carChasis, quantity, threshold, unitPrice,AGI);

    }
    static Supplier AGI=new LocalSupplier("AGI","##@#@#@","DWDAWDA","WDHUI@","GLASS");


}
