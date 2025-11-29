package SemesterProject.Supplier;

import SemesterProject.Part;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class SupplierManager {

    private List<Supplier> suppliers;

    public SupplierManager() {
        suppliers = new ArrayList<>();
    }

    // -------------------------
    // Add Supplier
    // -------------------------
    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
        System.out.println("Supplier added successfully: " + supplier.getName());
    }

    // -------------------------
    // Search Supplier by Name
    // -------------------------
    public Supplier searchSupplier(String name) {
        for (Supplier s : suppliers) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    // -------------------------
    // Remove Supplier
    // -------------------------
    public boolean removeSupplier(String name) {
        Supplier s = searchSupplier(name);
        if (s != null) {
            suppliers.remove(s);
            System.out.println("Supplier removed: " + name);
            return true;
        }
        return false;
    }

    // -------------------------
    // List All Suppliers
    // -------------------------
    public void listSuppliers() {
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers available.");
            return;
        }

        System.out.println("------- SUPPLIER LIST -------");
        for (Supplier s : suppliers) {
            s.displaySupplierDetails();
        }
    }

    // -------------------------
    // Assign a Part to a Supplier
    // -------------------------
    public void assignPartToSupplier(Supplier supplier, Part part) {
        supplier.addSuppliedPart(part);
        System.out.println(part.getName() + " assigned to " + supplier.getName());
    }

    // -------------------------
    // Purchase (Record & Update Inventory)
    // -------------------------
    public void recordPurchase(Supplier supplier, Part part, int quantity, double pricePerUnit) {

        double totalCost = quantity * pricePerUnit;
        Date date = new Date();

        // Create purchase record
        PurchaseRecord record = new PurchaseRecord(part, quantity, totalCost, date);
        supplier.addPurchaseRecord(record);

        // Update part stock
        part.addQuantity(quantity);

        System.out.println("Purchase recorded from supplier: " + supplier.getName());
        System.out.println("Purchased " + quantity + " x " + part.getName());
        System.out.println("Total Cost: " + totalCost);
    }

}
