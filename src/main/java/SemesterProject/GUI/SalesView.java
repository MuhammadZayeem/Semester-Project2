package SemesterProject.GUI;

import SemesterProject.Body.BodyPart;
import SemesterProject.Sales.Sale;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class SalesView extends VBox {

    public SalesView(List<Sale> salesList) {
        this.setPadding(new Insets(20));
        this.setSpacing(10);

        // Header
        Label lblHeader = new Label("Sales Transaction History");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        // Create Table
        TableView<Sale> table = new TableView<>();

        // 1. Date Column (FIXED: Uses getTimestamp().toLocalDate())
        TableColumn<Sale, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getTimestamp().toLocalDate().toString()
        ));

        // 2. Time Column (Added this so you can see the time too)
        TableColumn<Sale, String> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFormattedTime()
        ));

        // 3. Part Name Column
        TableColumn<Sale, String> colPart = new TableColumn<>("Part Name");
        colPart.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPart().getName()
        ));

        // 4. Quantity Column
        TableColumn<Sale, Number> colQty = new TableColumn<>("Qty Sold");
        colQty.setCellValueFactory(data -> new SimpleIntegerProperty(
                data.getValue().getQuantityUsed()
        ));

        // 5. Total Revenue Column (Calculated)
        TableColumn<Sale, Number> colTotal = new TableColumn<>("Total Amount");
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(
                data.getValue().getTotalAmount()
        ));

        table.getColumns().addAll(colDate, colTime, colPart, colQty, colTotal);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load Data
        ObservableList<Sale> data = FXCollections.observableArrayList(salesList);
        table.setItems(data);

        this.getChildren().addAll(lblHeader, table);
    }
}