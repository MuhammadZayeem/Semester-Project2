package SemesterProject.Sales;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sale {
    private String partName;
    private int quantitySold;
    private double cost;
    private LocalDateTime saleDateTime; // Store date/time for display

    // Constructor matching the three core transaction arguments
    public Sale(String partName, int quantitySold, double cost) {
        this.partName = partName;
        this.quantitySold = quantitySold;
        this.cost = cost;
        this.saleDateTime = LocalDateTime.now(); // Default to current time if no timestamp provided
    }

    // NEW Constructor to support database retrieval (with Timestamp)
    public Sale(String partName, int quantitySold, double cost, Timestamp timestamp) {
        this.partName = partName;
        this.quantitySold = quantitySold;
        this.cost = cost;
        // Convert SQL Timestamp to LocalDateTime for better JavaFX display integration
        if (timestamp != null) {
            this.saleDateTime = timestamp.toLocalDateTime();
        } else {
            this.saleDateTime = LocalDateTime.now();
        }
    }

    public String getPartName() {
        return partName;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public double getCost() {
        return cost;
    }

    // NEW: Method to retrieve the Sale date/time (resolves similar errors)
    public LocalDateTime getSaleDateTime() {
        return saleDateTime;
    }

    // NEW: Utility method for UI display
    public String getFormattedSaleDate() {
        if (saleDateTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return saleDateTime.format(formatter);
    }
}