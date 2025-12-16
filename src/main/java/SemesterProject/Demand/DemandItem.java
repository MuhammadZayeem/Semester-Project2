package SemesterProject.Demand;

import SemesterProject.Part;

public class DemandItem {
    private Part part;
    private int quantityNeeded;


    public DemandItem(Part part, int quantityNeeded) {
        this.part = part;
        this.quantityNeeded = quantityNeeded;
    }

    public Part getPart() {return part;}
    public int getQuantityNeeded() {return quantityNeeded;}
}