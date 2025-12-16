package SemesterProject;

public abstract class Part {

    protected String partId;
    protected String name;
    protected int quantity;
    protected int threshold;
    protected double unitPrice;

    //----------------------------------------------------------------Constructor
    public Part(String partId, String name, int quantity, int threshold, double unitPrice) {
        this.partId = partId;
        this.name = name;
        this.quantity = quantity;
        this.threshold = threshold;
        this.unitPrice = unitPrice;
    }

    // --------------------------------------------------------Quantity Methods
    public void addQuantity(int amount) {
        if (this.quantity+amount>= 0)
             this.quantity += amount;
    }



    //----------------------------------------------------------------------------Getters & Setters
    public String getName() { return name; }
    public int getCurrentStock() { return quantity; }
    public int getMinThreshold() { return threshold; }
    public double getUnitPrice() { return unitPrice; }
    public String getPartId() { return partId; }
    public void setPartId(String partId) { this.partId = partId; }
    public void setCurrentStock(int quantity) { this.quantity = quantity; }
}