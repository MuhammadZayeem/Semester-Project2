package SemesterProject.GUI;

import SemesterProject.Body.*;
import SemesterProject.Part;
import SemesterProject.Sales.Sale;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

import java.util.List;

public class InventoryView extends BorderPane {

    private List<Part> partList;
    private List<Sale> salesList;

    public InventoryView(List<Part> partList, List<Sale> salesList) {
        this.partList = partList;
        this.salesList = salesList;

        this.setPadding(new Insets(20));

        Label lblHeader = new Label("Categorized Inventory");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblHeader.setPadding(new Insets(0, 0, 15, 0));

        // --- TABS SETUP ---
        TabPane mainTabs = new TabPane();
        mainTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabBody = new Tab("Body Parts");
        Tab tabEngine = new Tab("Engine Parts");
        tabEngine.setContent(new Label("Engine Parts Placeholder"));

        TabPane bodyTabs = new TabPane();
        bodyTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        bodyTabs.setStyle("-fx-tab-min-width: 100px;");

        Tab tabGlass = new Tab("Glass");
        Tab tabBumper = new Tab("Bumpers");

        // Glass Sub-Tabs
        TabPane glassTabs = new TabPane();
        glassTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Defined Tabs
        Tab tabLaminated = new Tab("Front Laminated");
        tabLaminated.setContent(createTable(FrontLaminatedGlass.class));

        Tab tabFrontGlass = new Tab("Front Glass");
        tabFrontGlass.setContent(createTable(FrontGlass.class));

        Tab tabRearGlass = new Tab("Rear Glass");
        tabRearGlass.setContent(createTable(RearGlass.class));

        Tab tabDoorGlass = new Tab("Door Glass");
        tabDoorGlass.setContent(createTable(DoorGlass.class));

        glassTabs.getTabs().addAll(tabLaminated, tabFrontGlass, tabRearGlass, tabDoorGlass);
        tabGlass.setContent(glassTabs);

        // Bumper Sub-Tabs
        TabPane bumperTabs = new TabPane();
        bumperTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab tabFrontBumper = new Tab("Front Bumper");
        tabFrontBumper.setContent(createTable(FrontBumper.class));
        bumperTabs.getTabs().add(tabFrontBumper);
        tabBumper.setContent(bumperTabs);

        bodyTabs.getTabs().addAll(tabGlass, tabBumper);
        tabBody.setContent(bodyTabs);
        mainTabs.getTabs().addAll(tabBody, tabEngine);

        VBox topLayout = new VBox(10);
        topLayout.getChildren().addAll(lblHeader, mainTabs);
        this.setCenter(topLayout);
    }

    private VBox createTable(Class<?> categoryClass) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 0, 0, 0));

        ObservableList<Part> masterData = FXCollections.observableArrayList(partList);
        FilteredList<Part> filteredData = new FilteredList<>(masterData, p -> categoryClass.isInstance(p));

        TableView<Part> table = new TableView<>();
        table.setItems(filteredData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setEditable(false);

        // Row Highlight (Low Stock)
        table.setRowFactory(tv -> new TableRow<Part>() {
            @Override
            protected void updateItem(Part item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (item.getQuantity() <= item.getThreshold()) {
                        setStyle("-fx-background-color: #ffcccc; -fx-text-background-color: black;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // --- COLUMNS ---
        TableColumn<Part, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

        TableColumn<Part, String> colModel = new TableColumn<>("Car Model");
        colModel.setCellValueFactory(d -> {
            if (d.getValue() instanceof BodyPart) return new SimpleStringProperty(((BodyPart) d.getValue()).getCarModel());
            return new SimpleStringProperty("-");
        });

        TableColumn<Part, Number> colPrice = new TableColumn<>("Unit Price");
        colPrice.setCellValueFactory(d -> {
            if (d.getValue() instanceof BodyPart) return new SimpleDoubleProperty(((BodyPart) d.getValue()).getUnitPrice());
            return new SimpleDoubleProperty(0);
        });

        TableColumn<Part, Number> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQuantity()));

        TableColumn<Part, Number> colTotalVal = new TableColumn<>("Total Value");
        colTotalVal.setCellValueFactory(d -> {
            if (d.getValue() instanceof BodyPart) {
                double price = ((BodyPart) d.getValue()).getUnitPrice();
                return new SimpleDoubleProperty(price * d.getValue().getQuantity());
            }
            return new SimpleDoubleProperty(0);
        });

        // --- BOTTOM SUMMARY LABELS ---

        // 1. Total Quantity Label
        Label lblTotalQty = new Label("Total Items: 0");
        lblTotalQty.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblTotalQty.setTextFill(Color.DARKSLATEGRAY);

        // 2. Total Value Label
        Label lblGrandTotal = new Label("Total Value: PKR 0.0");
        lblGrandTotal.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblGrandTotal.setTextFill(Color.DARKBLUE);

        // Container for bottom labels
        HBox totalContainer = new HBox(20); // 20px spacing
        totalContainer.setAlignment(Pos.CENTER_RIGHT);
        totalContainer.setPadding(new Insets(5, 10, 5, 0));
        totalContainer.getChildren().addAll(lblTotalQty, lblGrandTotal);

        // Initial Calculation
        updateGrandTotal(filteredData, lblGrandTotal, lblTotalQty);

        // Buttons Column
        TableColumn<Part, Void> colAction = new TableColumn<>("Update Stock");
        // Pass both labels so they update on click
        colAction.setCellFactory(createActionCellFactory(table, filteredData, lblGrandTotal, lblTotalQty));

        table.getColumns().addAll(colName, colModel, colPrice, colQty, colTotalVal, colAction);

        layout.getChildren().addAll(table, totalContainer);
        return layout;
    }

    /**
     * Helper to Calculate Sums for visible rows
     */
    private void updateGrandTotal(List<Part> items, Label lblTotalVal, Label lblTotalQty) {
        double totalVal = 0;
        int totalQty = 0;
        for (Part p : items) {
            if (p instanceof BodyPart) {
                totalVal += ((BodyPart) p).getUnitPrice() * p.getQuantity();
                totalQty += p.getQuantity();
            }
        }
        lblTotalVal.setText("Total Value: PKR " + totalVal);
        lblTotalQty.setText("Total Items: " + totalQty);
    }

    private Callback<TableColumn<Part, Void>, TableCell<Part, Void>> createActionCellFactory(TableView<Part> table, List<Part> currentList, Label totalLabel, Label qtyLabel) {
        return param -> new TableCell<>() {
            private final Button btnDecrease = new Button("-");
            private final Button btnIncrease = new Button("+");
            private final HBox pane = new HBox(10, btnDecrease, btnIncrease);

            {
                pane.setAlignment(Pos.CENTER);
                btnDecrease.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 30px;");
                btnIncrease.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 30px;");

                btnDecrease.setOnAction(event -> {
                    Part part = getTableView().getItems().get(getIndex());
                    if (part.getQuantity() > 0) {
                        part.setQuantity(part.getQuantity() - 1);
                        salesList.add(new Sale(part, 1));
                        table.refresh();
                        // UPDATE TOTALS
                        updateGrandTotal(currentList, totalLabel, qtyLabel);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Out of Stock!");
                        alert.show();
                    }
                });

                btnIncrease.setOnAction(event -> {
                    Part part = getTableView().getItems().get(getIndex());
                    part.setQuantity(part.getQuantity() + 1);
                    table.refresh();
                    // UPDATE TOTALS
                    updateGrandTotal(currentList, totalLabel, qtyLabel);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(pane);
            }
        };
    }
}