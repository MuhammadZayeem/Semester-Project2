package SemesterProject.Sales;

import SemesterProject.Part;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sale {
    private Part part;
    private int quantityUsed;
    private LocalDateTime timestamp; // Stores both Date and Time
    private double soldPrice;        // Locks the price at the moment of sale

    public Sale(Part part, int quantityUsed) {
        this.part = part;
        this.quantityUsed = quantityUsed;
        this.timestamp = LocalDateTime.now(); // Sets current time automatically

        // We assume your Part class has getUnitPrice().
        // If Part doesn't have it, cast 'part' to 'BodyPart' if needed.
        // For this example, we assume Part or BodyPart has the price.
        if (part instanceof SemesterProject.Body.BodyPart) {
            this.soldPrice = ((SemesterProject.Body.BodyPart) part).getUnitPrice();
        } else {
            this.soldPrice = 0.0; // Fallback
        }
    }

    public Part getPart() {
        return part;
    }

    public int getQuantityUsed() {
        return quantityUsed;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getSoldPrice() {
        return soldPrice;
    }

    public double getTotalAmount() {
        return soldPrice * quantityUsed;
    }

    // --- Helper for GUI Table Display ---
    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return timestamp.format(formatter);
    }
}