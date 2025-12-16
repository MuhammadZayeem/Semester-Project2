package SemesterProject.GUI;

import SemesterProject.Demand.DemandItem;
import SemesterProject.Demand.DemandManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DemandView {

    // The main layout container
    private GridPane layout;

    private DemandManager demandManager;
    private MainApp app;
    private TableView<DemandItem> table;

    public DemandView(MainApp app, DemandManager manager) {
        this.app = app;
        this.demandManager = manager;

        // Initialize Layout (GridPane)
        layout = new GridPane();
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setVgap(15);
        layout.setHgap(10);
        layout.setStyle("-fx-background-color: #ecf0f1;");

        // 1. Back Button
        Button btnBack = new Button("⬅ Back to Dashboard");
        btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-cursor: hand;");
        btnBack.setOnAction(e -> app.showMainDashboard());

        // 2. Header
        Label lblHeader = new Label("Demand List");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblHeader.setStyle("-fx-text-fill: #2c3e50;");

        // 3. Table Setup
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        GridPane.setHgrow(table, Priority.ALWAYS); // Ensure table expands horizontally
        GridPane.setVgrow(table, Priority.ALWAYS); // Ensure table expands vertically

        TableColumn<DemandItem, String> colPart = new TableColumn<>("Part Name");
        colPart.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPart().getName()));

        TableColumn<DemandItem, String> colQty = new TableColumn<>("Qty Needed");
        colQty.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getQuantityNeeded())));

        table.getColumns().addAll(colPart, colQty);

        // Load Data
        if (demandManager.getDemandList() != null) {
            table.getItems().setAll(demandManager.getDemandList());
        }

        // --- ADDING TO GRID ---

        // Row 0: Back Button (Left Aligned)
        layout.add(btnBack, 0, 0);
        GridPane.setHalignment(btnBack, HPos.LEFT);

        // Row 1: Header (Centered)
        layout.add(lblHeader, 0, 1);
        GridPane.setHalignment(lblHeader, HPos.CENTER);

        // Row 2: Table (Fills space)
        layout.add(table, 0, 2);
    }

    // --- Critical Method to return the view ---
    public GridPane getView() {
        return layout;
    }
}