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
import java.util.ArrayList;

public class DemandView extends VBox {

    private DemandManager demandManager;
    private TableView<DemandItem> table;

    public DemandView(DemandManager manager) {
        this.demandManager = manager;
        this.setPadding(new Insets(20));
        this.setSpacing(10);

        Label lblHeader = new Label("Demand List / Purchase Orders");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DemandItem, String> colPart = new TableColumn<>("Part Name");
        colPart.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPart().getName()));

        TableColumn<DemandItem, String> colSupplier = new TableColumn<>("Supplier");
        colSupplier.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPart().getSupplier() != null) {
                return new SimpleStringProperty(cellData.getValue().getPart().getSupplier().getName());
            }
            return new SimpleStringProperty("N/A");
        });

        TableColumn<DemandItem, String> colQty = new TableColumn<>("Qty Needed");
        colQty.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantityNeeded())));

        TableColumn<DemandItem, String> colCost = new TableColumn<>("Est. Cost");
        colCost.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getTotalCost())));

        table.getColumns().addAll(colPart, colSupplier, colQty, colCost);

        Button btnRefresh = new Button("Refresh Demands");
        btnRefresh.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        btnRefresh.setOnAction(e -> refreshTable());

        this.getChildren().addAll(lblHeader, btnRefresh, table);
        refreshTable();
    }

    public void refreshTable() {
        // SIMPLIFIED: No reflection needed. Just call the method directly.
        if (demandManager.getDemandList() != null) {
            table.getItems().setAll(demandManager.getDemandList());
        }
    }
}