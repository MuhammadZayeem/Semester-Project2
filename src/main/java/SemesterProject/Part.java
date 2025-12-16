package SemesterProject;
import SemesterProject.Exception.LowStockException;
import SemesterProject.Exception.NegativeStockException;

public abstract class Part {

    protected String partId;
    protected String name;
    protected String category;
    protected String carChasis;
    protected int quantity;
    protected int threshold;
    protected double unitPrice;

    //----------------------------------------------------------------Constructor
    public Part(String partId, String name, String category, String carChasis,
                int quantity, int threshold, double unitPrice) {
        this.partId = partId;
        this.name = name;
        this.category = category;
        this.carChasis = carChasis;
        this.quantity = quantity;
        this.threshold = threshold;
        this.unitPrice = unitPrice;
    }

    // --------------------------------------------------------Quantity Methods
    public void addQuantity(int amount) {
        if (amount <= 0) return;
        this.quantity += amount;
    }



    //----------------------------------------------------------------------------Getters & Setters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getCarModel() { return carChasis; }
    public int getCurrentStock() { return quantity; }
    public int getMinThreshold() { return threshold; }
    public double getUnitPrice() { return unitPrice; }
    public String getPartId() { return partId; }
    public void setPartId(String partId) { this.partId = partId; }
    public void setCurrentStock(int quantity) { this.quantity = quantity; }
    //public void setThreshold(int threshold) { this.threshold = threshold; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}