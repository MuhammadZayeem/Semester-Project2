package SemesterProject.Dashboard;

import SemesterProject.Demand.DemandManager;
import SemesterProject.Part;
import SemesterProject.Sales.Sale;
import SemesterProject.Supplier.Supplier;

// --- FIX 1: ADD THESE IMPORTS ---
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane; // Used to arrange cards
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.*;

public class DashboardManager {

    private List<Part> parts;
    private List<Sale> sales;
    private DemandManager demandManager;

    // Define the Labels globally so we can update them dynamically
    private Label lblTotalRevenue = new Label("PKR 0");
    private Label lblTotalParts = new Label("0");
    private Label lblLowStock = new Label("0");
    private Label lblTodayUsage = new Label("0");

    public DashboardManager(List<Part> parts, List<Sale> sales, DemandManager demandManager) {
        this.parts = parts;
        this.sales = sales;
        this.demandManager = demandManager;
    }

    // ------------------------------------------
    //  1. The Main UI Method (Call this in MainLayout)
    // ------------------------------------------
    public Node getView() {
        // Create a Grid to hold the cards
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20));

        // Create the Cards
        Node cardRevenue = createRevenueCard(); // Purple
        Node cardParts   = createInfoCard("Total Parts", lblTotalParts, "#3498db"); // Blue
        Node cardStock   = createInfoCard("Low Stock", lblLowStock, "#e74c3c");     // Red
        Node cardUsage   = createInfoCard("Today's Usage", lblTodayUsage, "#2ecc71"); // Green

        // Add them to the grid (Col, Row)
        grid.add(cardRevenue, 0, 0);
        grid.add(cardParts,   1, 0);
        grid.add(cardStock,   0, 1);
        grid.add(cardUsage,   1, 1);

        // Update values immediately
        updateDashboardData();

        return grid;
    }

    // ------------------------------------------
    //  2. Logic to Update Values
    // ------------------------------------------
    public void updateDashboardData() {
        int totalPartsCount = parts.size();
        int lowStockCount = 0;
        int usageCount = 0;
        double totalRevenue = 0.0;
        LocalDate today = LocalDate.now();

        // Calculate Stock
        for (Part p : parts) {
            if (p.getQuantity() <= p.getThreshold()) {
                lowStockCount++;
            }
        }

        // Calculate Revenue & Usage
        for (Sale s : sales) {
            if (s.getTimestamp().toLocalDate().equals(today)) {
                usageCount += s.getQuantityUsed();
                totalRevenue += s.getTotalAmount();
            }
        }

        // Update UI Labels
        lblTotalRevenue.setText("PKR " + totalRevenue);
        lblTotalParts.setText(String.valueOf(totalPartsCount));
        lblLowStock.setText(String.valueOf(lowStockCount));
        lblTodayUsage.setText(String.valueOf(usageCount));
    }

    // ------------------------------------------
    //  3. Card Creation Helpers
    // ------------------------------------------

    // The Purple Revenue Card
    private Node createRevenueCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefSize(220, 120);
        card.setStyle("-fx-background-color: #8E44AD; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        Label title = new Label("Total Revenue");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", 16));

        lblTotalRevenue.setTextFill(Color.WHITE);
        lblTotalRevenue.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        card.getChildren().addAll(title, lblTotalRevenue);
        return card;
    }

    // Generic Helper for Blue/Red/Green Cards
    private Node createInfoCard(String titleText, Label valueLabel, String hexColor) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefSize(220, 120);
        card.setStyle("-fx-background-color: " + hexColor + "; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        Label title = new Label(titleText);
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Arial", 16));

        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        card.getChildren().addAll(title, valueLabel);
        return card;
    }

    // ------------------------------------------
    //  4. Getters & Console Logic
    // ------------------------------------------

    // FIX 2: Corrected the variable name here (sales -> sales)
    public List<Sale> getSalesList() {
        return sales;
    }

    // Keep your existing console method if you want (Optional)
    public void showDashboard() {
        // You can leave this as is for debugging
    }
}