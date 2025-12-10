package SemesterProject.GUI;

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
import java.util.List;


public class DashboardManager {

    private List<Part> PartList;
    private List<Sale> SaleList;
    private DemandManager demandManager;
    private VBox dashboardView;

    //------------------------------------------------Show these on dashboard
    private int totalStockQuantity;  //Total Parts
    private int lowStockItems;       //low stock items
    private int partsUsedToday;      // Parts Sold Today
    private int pendingDemandsCount; // pending demand

    // Label references
    private Label LabelTotalStockQuantity;
    private Label LabelLowStock;
    private Label LabelPartsUsedToday;
    private Label LabelPendingDemands;

    public DashboardManager(List<Part> PartList, List<Sale> SaleList, DemandManager demandManager) {
        this.PartList = PartList;
        this.SaleList = SaleList;
        this.demandManager = demandManager;
       this.dashboardView = createDashboardLayout();
    }

    public void updateDashboardData() {     //to claculate values
        totalStockQuantity = PartList.stream().mapToInt(Part::getCurrentStock).sum();
        lowStockItems = (int) PartList.stream().filter(p -> p.getCurrentStock() <= p.getMinThreshold()).count();
        calculateUsageMetrics();
        this.pendingDemandsCount = demandManager.getDemandList().size();
        updateUI();
    }

    private void calculateUsageMetrics() { //parts sold today
        LocalDateTime Today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        partsUsedToday = 0;
        for (Sale sale : SaleList) {
            if (sale.getSaleDateTime() != null && sale.getSaleDateTime().isAfter(Today)) {
                partsUsedToday += sale.getQuantitySold();
            }
        }
    }


    //-----------------------------------------------------------------GUI Methods
    private VBox createDashboardLayout() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: lightgray;");

        Label labelTitle = new Label("System Dashboard"); // Title
        labelTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        GridPane tileGrid = new GridPane();
        tileGrid.setHgap(20);
        tileGrid.setVgap(20);

        LabelTotalStockQuantity = createDataLabel(String.valueOf(totalStockQuantity));
        LabelLowStock = createDataLabel(String.valueOf(lowStockItems));
        LabelPartsUsedToday = createDataLabel(String.valueOf(partsUsedToday));
        LabelPendingDemands = createDataLabel(String.valueOf(pendingDemandsCount));

        //-----------------------------------------------------Tiles layout
        tileGrid.add(createTile("Total Parts", LabelTotalStockQuantity, "blue"), 0, 0);
        tileGrid.add(createTile("Low Stock", LabelLowStock, "red"), 1, 0);
        tileGrid.add(createTile("Sold Parts", LabelPartsUsedToday, "green"), 0, 1);
        tileGrid.add(createTile("Pending Demands", LabelPendingDemands, "orange"), 1, 1);

        container.getChildren().addAll(labelTitle, tileGrid);
        return container;
    }

    private void updateUI() {
        LabelTotalStockQuantity.setText(String.valueOf(totalStockQuantity));
        LabelLowStock.setText(String.valueOf(lowStockItems));
        LabelPartsUsedToday.setText(String.valueOf(partsUsedToday));
        LabelPendingDemands.setText(String.valueOf(pendingDemandsCount));
    }

    private VBox createTile(String title, Label dataLabel, String color) {
        VBox tile = new VBox(5);
        tile.setAlignment(Pos.CENTER);
        tile.setPadding(new Insets(15));
        tile.setPrefSize(200, 100);
        tile.setStyle("-fx-background-color: "+color);

        Label labelTitle = new Label(title);
        labelTitle.setFont(Font.font("Arial", 15));
        labelTitle.setStyle("-fx-text-fill: white;");
        dataLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        tile.getChildren().addAll(labelTitle, dataLabel);
        return tile;
    }

    private Label createDataLabel(String data) {
        Label labelData = new Label(data);
        labelData.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        return labelData;
    }

    public VBox getView() {
        updateDashboardData();
        return dashboardView;
    }
    public List<Sale> getSalesList() {return SaleList;}
}