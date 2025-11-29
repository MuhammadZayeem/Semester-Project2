package SemesterProject.InventoryManagment;

import java.util.ArrayList;
import java.util.List;
import SemesterProject.Part;
import SemesterProject.Supplier.Supplier;

public class InventoryManager {

    private List<Part> parts;

    public InventoryManager() {
        parts = new ArrayList<>();
    }

    // Add any part
    public void addPart(Part part) {
        parts.add(part);
    }

    // ----------------------------
    // 1. SEARCH BY NAME
    // ----------------------------
    public List<Part> searchByName(String name) {
        List<Part> result = new ArrayList<>();

        for (Part p : parts) {
            if (p.getName().equalsIgnoreCase(name)) {
                result.add(p);
            }
        }
        return result;
    }


    // ----------------------------
    // 2. SEARCH BY CATEGORY
    // ----------------------------
    public List<Part> searchByCategory(String category) {
        List<Part> result = new ArrayList<>();

        for (Part p : parts) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                result.add(p);
            }
        }
        return result;
    }


    // ----------------------------
    // 3. SEARCH BY CAR MODEL
    // ----------------------------
    public List<Part> searchByCarModel(String model) {
        List<Part> result = new ArrayList<>();

        for (Part p : parts) {
            if (p.getCarModel().equalsIgnoreCase(model)) {
                result.add(p);
            }
        }
        return result;
    }


    // ----------------------------
    // 4. SEARCH BY SUPPLIER
    // ----------------------------
    // To use this, Supplier must have a part list.
    // suppliers will be passed from SupplierManager
    public List<Part> searchBySupplier(Supplier supplier) {
        return supplier.getSuppliedParts();
    }


    // ----------------------------
    // 5. FILTER: LOW STOCK
    // ----------------------------
    public List<Part> filterLowStock() {
        List<Part> result = new ArrayList<>();

        for (Part p : parts) {
            if (p.isLowStock()) {   // uses your overridable method
                result.add(p);
            }
        }
        return result;
    }

    // ----------------------------
    // List all parts
    // ----------------------------
    public void listAllParts() {
        if (parts.isEmpty()) {
            System.out.println("No parts in inventory.");
            return;
        }

        System.out.println("------ ALL INVENTORY PARTS ------");
        for (Part p : parts) {
            p.displayDetails();
        }
    }
}
