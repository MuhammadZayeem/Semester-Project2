package SemesterProject.GUI;

import SemesterProject.Sales.Sale;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public class SalesView extends VBox {

    private TableView<Sale> table;
    private List<Sale> SaleList;
    private MainApp app;

    public SalesView(MainApp app, List<Sale> masterSaleList) {
        this.app = app;
        this.SaleList = masterSaleList;
        this.setPadding(new Insets(20));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #ecf0f1;");

        // Back Button
        Button btnBack = new Button("⬅ Back to Dashboard");
        btnBack.setOnAction(e -> app.showMainDashboard());

        Label lblHeader = new Label("Sales History & Transactions");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Sale, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFormattedSaleDate()));

        TableColumn<Sale, String> colPart = new TableColumn<>("Part Name");
        colPart.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPartName()));

        TableColumn<Sale, String> colQuantity = new TableColumn<>("Qty");
        colQuantity.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getQuantitySold())));

        TableColumn<Sale, String> colCost = new TableColumn<>("Total Cost");
        colCost.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getCost())));

        table.getColumns().addAll(colDate, colPart, colQuantity, colCost);

        this.getChildren().addAll(btnBack, lblHeader, table);
        refreshTable();
    }

    public void refreshTable() {
        ObservableList<Sale> data = FXCollections.observableArrayList(SaleList);
        table.setItems(data);
    }
}