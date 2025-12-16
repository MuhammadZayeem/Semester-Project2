package SemesterProject.Sales;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sale {
    private String partName;
    private int quantitySold;
    private double cost;
    private LocalDateTime saleDateTime;

    // ---------------------------------------------------------------Constructor
    public Sale(String partName, int quantitySold, double cost) {
        this.partName = partName;
        this.quantitySold = quantitySold;
        this.cost = cost;
        this.saleDateTime = LocalDateTime.now();
    }

    public String getPartName() {return partName;}
    public int getQuantitySold() {return quantitySold;}
    public double getCost() {return cost;}
    public LocalDateTime getSaleDateTime(){return saleDateTime;}

    public String getFormattedSaleDate() {
        if (saleDateTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return saleDateTime.format(formatter);
    }
}