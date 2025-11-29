package SemesterProject.GUI;

import SemesterProject.Body.FrontGlass; // Importing your specific classes
import SemesterProject.Body.FrontLaminatedGlass;
import SemesterProject.Body.BodyPart;
import SemesterProject.Sales.Sale;
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

        // Column 1: Time
        TableColumn<Sale, String> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFormattedTime()));

        // Column 2: Part Name (Extracting from the nested Part object)
        TableColumn<Sale, String> colName = new TableColumn<>("Part Name");
        colName.setCellValueFactory(cell -> {
            // Safe casting to BodyPart to access getName() if Part doesn't have it exposed directly
            BodyPart part = (BodyPart) cell.getValue().getPart();
            return new SimpleStringProperty(part.getName());
        });

        // Column 3: Car Model
        TableColumn<Sale, String> colModel = new TableColumn<>("Car Model");
        colModel.setCellValueFactory(cell -> {
            BodyPart part = (BodyPart) cell.getValue().getPart();
            return new SimpleStringProperty(part.getCarModel());
        });

        // Column 4: Quantity
        TableColumn<Sale, Number> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantityUsed()));

        // Column 5: Total Amount
        TableColumn<Sale, Number> colTotal = new TableColumn<>("Total (PKR)");
        colTotal.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getTotalAmount()));

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
            totalRev += s.getTotalAmount();
            totalQty += s.getQuantityUsed();
        }

        lblTotalRevenueVal.setText("PKR " + totalRev);
        lblTotalSoldVal.setText(String.valueOf(totalQty));

        // Simple logic to just show the first item as top seller for this example
        if (!salesData.isEmpty()) {
            BodyPart p = (BodyPart) salesData.get(0).getPart();
            lblTopItemVal.setText(p.getName());
        }
    }

    // --- Mock Data ---
    private void generateMockData() {
        // Using your actual classes

        // Sale 1: Front Glass for Civic
        FrontGlass p1 = new FrontGlass("Civic", 10, 5, 5000);
        Sale s1 = new Sale(p1, 2); // Sold 2

        // Sale 2: Front Laminated for Corolla
        FrontLaminatedGlass p2 = new FrontLaminatedGlass("Corolla", 20, 5, 12000);
        Sale s2 = new Sale(p2, 1); // Sold 1

        salesData.addAll(s1, s2);
    }
}