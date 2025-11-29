package SemesterProject.Supplier;

import SemesterProject.Part;

import java.util.Date;

public class PurchaseRecord {

    private Part part;
    private int quantity;
    private double price;
    private Date date;

    public PurchaseRecord(Part part, int quantity, double price, Date date) {
        this.part = part;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
    }

    public Part getPart() { return part; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public Date getDate() { return date; }
}
