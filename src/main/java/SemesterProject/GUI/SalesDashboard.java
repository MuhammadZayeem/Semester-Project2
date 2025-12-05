package SemesterProject.GUI;

import SemesterProject.Body.FrontGlass; // Importing your specific classes
import SemesterProject.Body.FrontLaminatedGlass;
import SemesterProject.Body.BodyPart; // Note: Assuming BodyPart is the base Part class
import SemesterProject.Sales.Sale;
import SemesterProject.Part; // Import the base Part class

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SalesDashboard extends Application {

    // List to hold the sales data for the table
    private ObservableList<Sale> salesData = FXCollections.observableArrayList();

    // Summary Labels
    private Label lblTotalRevenueVal = new Label("PKR 0.0");
    private Label lblTotalSoldVal = new Label("0");
    private Label lblTopItemVal = new Label("-");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sales Dashboard - Inventory System");

        // 1. Top Toolbar (Date Picker & Title)
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label("Daily Sales Overview");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        Button btnGenerateReport = new Button("Generate PDF Report");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(titleLabel, spacer, new Label("Date:"), datePicker, btnGenerateReport);

        // 2. KPI Summary Cards (Revenue, Items Sold, Top Seller)
        HBox summaryCards = createSummaryCards();

        // 3. Transaction Table
        TableView<Sale> table = createSalesTable();

        // Layout Assembly
        VBox centerLayout = new VBox(20);
        centerLayout.getChildren().addAll(topBar, summaryCards, new Label("Transaction History"), table);

        root.setCenter(centerLayout);

        // --- MOCK DATA GENERATION (Simulating a day of sales) ---
        generateMockData();
        refreshSummary(); // Calculate totals based on the mock data

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- Helper: Creates the Table ---
    private TableView<Sale> createSalesTable() {
        TableView<Sale> table = new TableView<>();
        table.setItems(salesData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // FIX 1: Replaced getFormattedTime() with the correct database-friendly method
        TableColumn<Sale, String> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFormattedSaleDate()));

        // FIX 2 & 3: Part Name and Car Model extraction
        // Note: Sale only stores Part Name (String), not the full Part object.
        TableColumn<Sale, String> colName = new TableColumn<>("Part Name");
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPartName())); // Use getPartName()

        // We can no longer get Car Model directly from Sale, as Sale only holds the part name.
        // For a mock, we display the part name again or leave this column simplified.
        TableColumn<Sale, String> colModel = new TableColumn<>("Car Model");
        colModel.setCellValueFactory(cell -> new SimpleStringProperty("N/A (Stored in DB)")); // Simplified for the mock data structure

        // FIX 4: Replaced getQuantityUsed() with getQuantitySold()
        TableColumn<Sale, Number> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantitySold())); // Use getQuantitySold()

        // FIX 5: Replaced getTotalAmount() with getCost()
        TableColumn<Sale, Number> colTotal = new TableColumn<>("Total (PKR)");
        colTotal.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getCost())); // Use getCost()

        table.getColumns().addAll(colTime, colName, colModel, colQty, colTotal);
        return table;
    }

    // --- Helper: Creates the Summary Cards ---
    private HBox createSummaryCards() {
        HBox container = new HBox(20);
        container.setPadding(new Insets(10, 0, 10, 0));

        container.getChildren().addAll(
                createCard("Total Revenue", lblTotalRevenueVal, Color.LIGHTGREEN),
                createCard("Items Sold", lblTotalSoldVal, Color.LIGHTBLUE),
                createCard("Top Selling Item", lblTopItemVal, Color.LIGHTCORAL)
        );
        return container;
    }

    private VBox createCard(String title, Label valueLabel, Color bgColor) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(250);
        card.setBackground(new Background(new BackgroundFill(bgColor, new CornerRadii(10), Insets.EMPTY)));

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Arial", 14));

        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        card.getChildren().addAll(lblTitle, valueLabel);
        return card;
    }

    // --- Logic: Calculate Totals ---
    private void refreshSummary() {
        double totalRev = 0;
        int totalQty = 0;

        for (Sale s : salesData) {
            // FIX: Replaced obsolete methods with getCost() and getQuantitySold()
            totalRev += s.getCost();
            totalQty += s.getQuantitySold();
        }

        lblTotalRevenueVal.setText("PKR " + String.format("%.2f", totalRev));
        lblTotalSoldVal.setText(String.valueOf(totalQty));

        // Logic to determine top seller
        if (!salesData.isEmpty()) {
            String topSellerName = salesData.stream()
                    .collect(Collectors.groupingBy(Sale::getPartName, Collectors.summingInt(Sale::getQuantitySold)))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("-");

            lblTopItemVal.setText(topSellerName);
        } else {
            lblTopItemVal.setText("-");
        }
    }

    // --- Mock Data ---
    private void generateMockData() {
        // FIX 6: Constructor Mismatch Fix
        // We must create sales using the constructor Sale(String partName, int quantitySold, double cost)

        // Sale 1: Front Glass for Civic
        String p1Name = "Civic Front Glass";
        double p1Price = 5000.0;
        int q1 = 2;
        Sale s1 = new Sale(p1Name, q1, p1Price * q1);

        // Sale 2: Front Laminated for Corolla
        String p2Name = "Corolla Front Laminated";
        double p2Price = 12000.0;
        int q2 = 1;
        Sale s2 = new Sale(p2Name, q2, p2Price * q2);

        // Sale 3: To make p1 the top seller
        Sale s3 = new Sale(p1Name, 5, p1Price * 5);


        salesData.addAll(s1, s2, s3);
    }
}