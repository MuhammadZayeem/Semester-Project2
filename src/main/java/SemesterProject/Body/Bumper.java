package SemesterProject.Body;

import SemesterProject.Supplier.LocalSupplier;
import SemesterProject.Supplier.Supplier;

public abstract class Bumper extends BodyPart {

    public Bumper(String name,String carChasis,int quantity,int threshold,double price){
        super(name,carChasis,quantity,threshold,price,ABDUllah);
    }
    static Supplier ABDUllah=new LocalSupplier("ABDULLAH","()*)(&)(&","@","LAHORE","BUMPERS");

}
