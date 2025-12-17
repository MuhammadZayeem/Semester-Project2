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

    private GridPane layout;
    private DemandManager demandManager;
    private MainApp app;
    private TableView<DemandItem> table;

    public DemandView(MainApp app, DemandManager manager) {
        this.app = app;
        this.demandManager = manager;

        layout = new GridPane();
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setVgap(15);
        layout.setHgap(10);
        layout.setStyle("-fx-background-color: #ecf0f1;");

        //----------Back Button
        Button btnBack = new Button("Back to Dashboard");
        //btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-cursor: hand;");
        btnBack.setOnAction(e -> app.showMainDashboard());

        //----------------Header
        Label lblHeader = new Label("Demand List");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblHeader.setStyle("-fx-text-fill: #2c3e50;");

        //--------------------Table Setup
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);

        TableColumn<DemandItem, String> colPart = new TableColumn<>("Part Name");
        colPart.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getPart().getName()));

        TableColumn<DemandItem, String> colQty = new TableColumn<>("Qty Needed");
        colQty.setCellValueFactory(e -> new SimpleStringProperty(String.valueOf(e.getValue().getQuantityNeeded())));

        table.getColumns().addAll(colPart, colQty);

        //---------------------Load Data
        if (demandManager.getDemandList() != null) {
            table.getItems().setAll(demandManager.getDemandList());
        }

        // ------------------------------ADDING TO GRID

        //Back Button
        layout.add(btnBack, 0, 0);
        GridPane.setHalignment(btnBack, HPos.LEFT);

        //Header
        layout.add(lblHeader, 0, 1);
        GridPane.setHalignment(lblHeader, HPos.CENTER);

        //Table
        layout.add(table, 0, 2);
    }

    // -------------------Method to return view
    public GridPane getView() {
        return layout;
    }
}