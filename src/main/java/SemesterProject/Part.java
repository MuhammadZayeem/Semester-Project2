package SemesterProject;

import SemesterProject.Exception.LowStockException;
import SemesterProject.Exception.NegativeStockException;
import SemesterProject.Supplier.Supplier;

public abstract class Part {

    protected String partId;
    protected String name;
    protected String category;
    protected String carChasis;
    protected int quantity;
    protected int threshold;
    protected double unitPrice;
    protected Supplier supplier;

    // Standard 7-Argument Constructor for DB
    public Part(String partId, String name, String category, String carChasis,
                int quantity, int threshold, double unitPrice, Supplier supplier) {
        this.partId = partId;
        this.name = name;
        this.category = category;
        this.carChasis = carChasis;
        this.quantity = quantity;
        this.threshold = threshold;
        this.unitPrice = unitPrice;
        this.supplier = supplier;
    }

    public abstract void displayDetails();

    // Public methods (Keep for compatibility)
    public void addQuantity(int amount) {
        if (amount <= 0) return;
        this.quantity += amount;
    }

    public void reduceQuantity(int amount) throws LowStockException, NegativeStockException {
        if (amount <= 0) return;
        if (quantity - amount < 0) throw new NegativeStockException("Stock for " + name + " cannot go negative!");
        this.quantity -= amount;
        if (isLowStock()) throw new LowStockException("Low stock alert for: " + name);
    }

    public boolean isLowStock() { return this.quantity <= this.threshold; }

    public double calculateStockValue() { return quantity * unitPrice; }

    // Getters & Setters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getCarModel() { return carChasis; }
    public int getCurrentStock() { return quantity; }
    public int getMinThreshold() { return threshold; }
    public double getUnitPrice() { return unitPrice; }
    public Supplier getSupplier() { return supplier; }
    public String getPartId() { return partId; }

    public void setPartId(String partId) { this.partId = partId; }
    public void setCurrentStock(int quantity) { this.quantity = quantity; }
    public void setThreshold(int threshold) { this.threshold = threshold; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
}