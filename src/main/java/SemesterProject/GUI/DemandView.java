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
    private MainApp app;
    private TableView<DemandItem> table;

    public DemandView(MainApp app, DemandManager manager) {
        this.app = app;
        this.demandManager = manager;
        this.setPadding(new Insets(20));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #ecf0f1;");

        // Back Button
        Button btnBack = new Button("⬅ Back to Dashboard");
        btnBack.setOnAction(e -> app.showMainDashboard());

        Label lblHeader = new Label("Demand List");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DemandItem, String> colPart = new TableColumn<>("Part Name");
        colPart.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPart().getName()));

        TableColumn<DemandItem, String> colQty = new TableColumn<>("Qty Needed");
        colQty.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getQuantityNeeded())));

        table.getColumns().addAll(colPart, colQty);

        this.getChildren().addAll(btnBack, lblHeader, table);

        if (demandManager.getDemandList() != null) {
            table.getItems().setAll(demandManager.getDemandList());
        }
    }
}