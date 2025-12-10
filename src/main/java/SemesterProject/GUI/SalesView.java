package SemesterProject.GUI;

import SemesterProject.Sales.Sale;
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

    private TableView<Sale> table;
    private List<Sale> SaleList;

    public SalesView(List<Sale> masterSaleList) {
        this.SaleList = masterSaleList;
        this.setPadding(new Insets(20));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: Light Gray");

        Label lblHeader = new Label("Sales History & Transactions");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblHeader.setStyle("-fx-text-fill: #2c3e50;");

        table = new TableView<>();
        table.setEditable(false);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //-----------------------------------------------------Columns of tablw

        TableColumn<Sale, String> colDate = new TableColumn<>("Date & Time");
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFormattedSaleDate()));
        colDate.setPrefWidth(200);
        TableColumn<Sale, String> colPart = new TableColumn<>("Part Name");
        colPart.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPartName()));
        TableColumn<Sale, String> colQuantity = new TableColumn<>("Quantity Sold");
        colQuantity.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getQuantitySold())));
        colQuantity.setStyle("-fx-alignment: CENTER;");
        TableColumn<Sale, String> colCost = new TableColumn<>("Total Cost");
        colCost.setCellValueFactory(data -> new SimpleStringProperty(String.format("$%.2f", data.getValue().getCost())));

        colCost.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold;");

        table.getColumns().addAll(colDate, colPart, colQuantity, colCost);
        this.getChildren().addAll(lblHeader, table);
        refreshTable();
    }

    public void refreshTable() {
        ObservableList<Sale> data = FXCollections.observableArrayList(SaleList);
        table.setItems(data);
    }
}