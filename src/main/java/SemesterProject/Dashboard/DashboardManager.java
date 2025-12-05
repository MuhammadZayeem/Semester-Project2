package SemesterProject.Dashboard;

import SemesterProject.Demand.DemandManager;
import SemesterProject.Part;
import SemesterProject.Sales.Sale;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class DashboardManager {

    private List<Part> masterPartList;
    private List<Sale> masterSaleList;
    private DemandManager demandManager;
    private VBox dashboardView;

    // Dashboard Data Fields
    private int totalStockQuantity; // Total sum of all quantities (Total Parts)
    private int lowStockItems;
    private int partsUsedToday; // NEW METRIC: Parts Sold/Used Today
    private int pendingDemandsCount; // NEW METRIC: Placeholder for pending demand count

    // Label references
    private Label dataLabelTotalStockQuantity;
    private Label dataLabelLowStock;
    private Label dataLabelPartsUsedToday;
    private Label dataLabelPendingDemands;

    public DashboardManager(List<Part> masterPartList, List<Sale> masterSaleList, DemandManager demandManager) {
        this.masterPartList = masterPartList;
        this.masterSaleList = masterSaleList;
        this.demandManager = demandManager;
        this.dashboardView = createDashboardLayout(); // Initializes the UI
    }

    public void updateDashboardData() {
        // Recalculate Inventory Metrics
        totalStockQuantity = masterPartList.stream().mapToInt(Part::getCurrentStock).sum();
        lowStockItems = (int) masterPartList.stream().filter(p -> p.getCurrentStock() <= p.getMinThreshold()).count();

        // Recalculate Dynamic Metrics
        calculateUsageMetrics();
        // Placeholder: Assuming DemandManager tracks pending demands
        this.pendingDemandsCount = demandManager.getDemandList().size();

        // Update the UI labels
        updateUI();
    }

    private void calculateUsageMetrics() {
        // Calculate parts sold/used today
        LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        partsUsedToday = 0;

        for (Sale sale : masterSaleList) {
            if (sale.getSaleDateTime() != null && sale.getSaleDateTime().isAfter(startOfDay)) {
                // Sum the quantity sold for today's usage
                partsUsedToday += sale.getQuantitySold();
            }
        }
    }

    // --- UI Methods ---

    private VBox createDashboardLayout() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: #ecf0f1;");

        Label lblTitle = new Label("System Dashboard"); // Title updated to match image
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        GridPane tileGrid = new GridPane();
        tileGrid.setHgap(20);
        tileGrid.setVgap(20);

        // Initialize Data Labels (referenced later in updateUI)
        dataLabelTotalStockQuantity = createDataLabel(String.valueOf(totalStockQuantity));
        dataLabelLowStock = createDataLabel(String.valueOf(lowStockItems));
        dataLabelPartsUsedToday = createDataLabel(String.valueOf(partsUsedToday));
        dataLabelPendingDemands = createDataLabel(String.valueOf(pendingDemandsCount));


        // =========================================================
        // 4-TILE LAYOUT (Matching Image)
        // =========================================================

        // 1. Total Parts (Total Stock Quantity)
        tileGrid.add(createTile("Total Parts", dataLabelTotalStockQuantity, "#3498db"), 0, 0);

        // 2. Low Stock Items
        tileGrid.add(createTile("Low Stock", dataLabelLowStock, "#e74c3c"), 1, 0);

        // 3. Today's Usage
        tileGrid.add(createTile("Today's Usage", dataLabelPartsUsedToday, "#2ecc71"), 0, 1);

        // 4. Pending Demands
        tileGrid.add(createTile("Pending Demands", dataLabelPendingDemands, "#f39c12"), 1, 1);


        container.getChildren().addAll(lblTitle, tileGrid);

        return container;
    }

    private void updateUI() {
        // Update the specific Label references directly.
        dataLabelTotalStockQuantity.setText(String.valueOf(totalStockQuantity));
        dataLabelLowStock.setText(String.valueOf(lowStockItems));
        dataLabelPartsUsedToday.setText(String.valueOf(partsUsedToday));
        dataLabelPendingDemands.setText(String.valueOf(pendingDemandsCount));
    }

    // Simplified createTile to match the image's simple style
    private VBox createTile(String title, Label dataLabel, String color) {
        VBox tile = new VBox(5);
        tile.setAlignment(Pos.CENTER);
        tile.setPadding(new Insets(15));
        tile.setPrefSize(180, 100); // Smaller size to fit 4 tiles better

        // Apply styling for background and shadow
        tile.setStyle("-fx-background-color: " + color + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Arial", 14));
        lblTitle.setStyle("-fx-text-fill: white;");

        // The data label's color and font
        dataLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        // Order: Title, then Data
        tile.getChildren().addAll(lblTitle, dataLabel);
        return tile;
    }

    private Label createDataLabel(String data) {
        Label lblData = new Label(data);
        lblData.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        return lblData;
    }

    public VBox getView() {
        updateDashboardData();
        return dashboardView;
    }

    public List<Sale> getSalesList() {
        return masterSaleList;
    }
}