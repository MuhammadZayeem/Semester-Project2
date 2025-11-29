package SemesterProject.GUI;

import SemesterProject.Demand.DemandItem;
import SemesterProject.Demand.DemandManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class DemandView extends VBox {

    private DemandManager demandManager;
    private TableView<DemandItem> table;

    // THIS CONSTRUCTOR IS WHAT MAINLAYOUT IS LOOKING FOR
    public DemandView(DemandManager manager) {
        this.demandManager = manager;
        this.setPadding(new Insets(20));
        this.setSpacing(10);

        Label lblHeader = new Label("Demand List / Purchase Orders");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        // Setup Table
        table = new TableView<>();

        // 1. Part Name
        TableColumn<DemandItem, String> colPart = new TableColumn<>("Part Name");
        colPart.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPart().getName()));

        // 2. Supplier
        TableColumn<DemandItem, String> colSupplier = new TableColumn<>("Supplier");
        colSupplier.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(cellData.getValue().getPart().getSupplier().getName());
            } catch (Exception e) {
                return new SimpleStringProperty("N/A");
            }
        });

        // 3. Quantity Needed
        TableColumn<DemandItem, String> colQty = new TableColumn<>("Qty Needed");
        colQty.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getRequiredQuantity())));

        // 4. Total Cost
        TableColumn<DemandItem, String> colCost = new TableColumn<>("Est. Cost");
        colCost.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getTotalCost())));

        table.getColumns().addAll(colPart, colSupplier, colQty, colCost);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Refresh Button
        Button btnRefresh = new Button("Refresh Demands");
        btnRefresh.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnRefresh.setOnAction(e -> refreshTable());

        this.getChildren().addAll(lblHeader, btnRefresh, table);

        // Load data initially
        refreshTable();
    }

    public void refreshTable() {
        try {
            // Using reflection to access the private list in DemandManager
            Field f = DemandManager.class.getDeclaredField("demandList");
            f.setAccessible(true);
            ArrayList<DemandItem> list = (ArrayList<DemandItem>) f.get(demandManager);
            table.getItems().setAll(list);
        } catch (Exception e) {
            System.out.println("Error accessing demand list: " + e.getMessage());
        }
    }
}