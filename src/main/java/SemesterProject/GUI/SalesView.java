package SemesterProject.GUI;

import SemesterProject.Sales.Sale;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;

public class SalesView {

    // The main layout container (GridPane)
    private GridPane layout;

    private TableView<Sale> table;
    private List<Sale> SaleList;
    private MainApp app;

    public SalesView(MainApp app, List<Sale> masterSaleList) {
        this.app = app;
        this.SaleList = masterSaleList;

        // Initialize Layout (GridPane)
        layout = new GridPane();
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setVgap(15); // Vertical spacing between rows
        layout.setHgap(10);
        layout.setStyle("-fx-background-color: #ecf0f1;");

        // 1. Back Button
        Button btnBack = new Button("⬅ Back to Dashboard");
        btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-cursor: hand;");
        btnBack.setOnAction(e -> app.showMainDashboard());

        // 2. Header
        Label lblHeader = new Label("Sales History & Transactions");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblHeader.setStyle("-fx-text-fill: #2c3e50;");

        // 3. Table Setup
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);

        TableColumn<Sale, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFormattedSaleDate()));

        TableColumn<Sale, String> colPart = new TableColumn<>("Part Name");
        colPart.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPartName()));

        TableColumn<Sale, String> colQuantity = new TableColumn<>("Qty");
        colQuantity.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getQuantitySold())));

        TableColumn<Sale, String> colCost = new TableColumn<>("Total Cost");
        colCost.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getCost())));

        table.getColumns().addAll(colDate, colPart, colQuantity, colCost);

        // ------------------------------------------------ADDING TO GRID

        //Back Button
        layout.add(btnBack, 0, 0);
        GridPane.setHalignment(btnBack, HPos.LEFT);

        //Header
        layout.add(lblHeader, 0, 1);
        GridPane.setHalignment(lblHeader, HPos.CENTER);

        //Table
        layout.add(table, 0, 2);

        refreshTable();
    }

    // -------------------------Method for return the view
    public GridPane getView() {
        return layout;
    }

    public void refreshTable() {
        ObservableList<Sale> data = FXCollections.observableArrayList(SaleList);
        table.setItems(data);
    }
}