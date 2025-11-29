package SemesterProject.Supplier;

import SemesterProject.Part;

import java.util.ArrayList;
import java.util.List;

public abstract class Supplier  {

    // -------------------------
    // Attributes
    // -------------------------
    protected String name;
    protected String contactNumber;
    protected String email;
    protected String address;
    protected String specialization;  // Example: "Glass", "Bumper", "Belts"

    protected List<Part> suppliedParts;       // Which parts this supplier provides
    protected List<PurchaseRecord> history;   // Purchase History


    // -------------------------
    // Constructor
    // -------------------------
    public Supplier(String name, String contactNumber, String email,
                    String address, String specialization) {

        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.specialization = specialization;

        this.suppliedParts = new ArrayList<>();
        this.history = new ArrayList<>();
    }


    // -------------------------
    // Abstract Method
    // -------------------------
    public abstract void displaySupplierDetails();


    // -------------------------
    // Concrete Methods
    // -------------------------

    // Add part this supplier can provide
    public void addSuppliedPart(Part part) {
        suppliedParts.add(part);
    }

    public List<Part> getSuppliedParts() {
        return suppliedParts;
    }

    // Add a purchase record
    public void addPurchaseRecord(PurchaseRecord record) {
        history.add(record);
    }

    public List<PurchaseRecord> getPurchaseHistory() {
        return history;
    }


    // -------------------------
    // Getters
    // -------------------------
    public String getName() { return name; }
    public String getContactNumber() { return contactNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getSpecialization() { return specialization; }
}
