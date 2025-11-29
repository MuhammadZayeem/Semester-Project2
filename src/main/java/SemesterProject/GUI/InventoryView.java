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

        // 1. Header
        Label lblHeader = new Label("Categorized Inventory");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblHeader.setPadding(new Insets(0, 0, 15, 0));

        // ---------------------------------------------------------
        // LEVEL 1 TABS: [ Body Parts ] [ Engine Parts ]
        // ---------------------------------------------------------
        TabPane mainTabs = new TabPane();
        mainTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabBody = new Tab("Body Parts");
        Tab tabEngine = new Tab("Engine Parts");
        tabEngine.setContent(new Label("Engine Parts Module Placeholder"));

        // ---------------------------------------------------------
        // LEVEL 2 TABS (Inside Body): [ Glass ] [ Bumper ]
        // ---------------------------------------------------------
        TabPane bodyTabs = new TabPane();
        bodyTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        bodyTabs.setStyle("-fx-tab-min-width: 100px;");

        Tab tabGlass = new Tab("Glass");
        Tab tabBumper = new Tab("Bumpers");

        // ---------------------------------------------------------
        // LEVEL 3 TABS (Inside Glass): [ Front ] [ Rear ] [ Door ] ...
        // ---------------------------------------------------------
        TabPane glassTabs = new TabPane();
        glassTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabFrontGlass = new Tab("Front Glass");
        tabFrontGlass.setContent(createTable(FrontGlass.class));

        Tab tabRearGlass = new Tab("Rear Glass");
        tabRearGlass.setContent(createTable(RearGlass.class));

        Tab tabDoorGlass = new Tab("Door Glass");
        tabDoorGlass.setContent(createTable(DoorGlass.class));

        Tab tabLaminated = new Tab("Front Laminated");
        tabLaminated.setContent(createTable(FrontLaminatedGlass.class));

        glassTabs.getTabs().addAll(tabFrontGlass, tabRearGlass, tabDoorGlass, tabLaminated);
        tabGlass.setContent(glassTabs);

        // ---------------------------------------------------------
        // LEVEL 3 TABS (Inside Bumper): [ Front Bumper ]
        // ---------------------------------------------------------
        TabPane bumperTabs = new TabPane();
        bumperTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabFrontBumper = new Tab("Front Bumper");
        tabFrontBumper.setContent(createTable(FrontBumper.class));

        bumperTabs.getTabs().add(tabFrontBumper);
        tabBumper.setContent(bumperTabs);

        // Assembly
        bodyTabs.getTabs().addAll(tabGlass, tabBumper);
        tabBody.setContent(bodyTabs);

        mainTabs.getTabs().addAll(tabBody, tabEngine);

        VBox topLayout = new VBox(10);
        topLayout.getChildren().addAll(lblHeader, mainTabs);

        this.setCenter(topLayout);
    }

    /**
     * Creates the Table with the +/- Buttons AND Row Highlighting
     */
    private VBox createTable(Class<?> categoryClass) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 0, 0, 0));

        // --- Data Setup ---
        ObservableList<Part> masterData = FXCollections.observableArrayList(partList);
        FilteredList<Part> filteredData = new FilteredList<>(masterData, p -> categoryClass.isInstance(p));

        // --- Table View ---
        TableView<Part> table = new TableView<>();
        table.setItems(filteredData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setEditable(false);

        // *** NEW: ROW FACTORY FOR LOW STOCK ALERT ***
        table.setRowFactory(tv -> new TableRow<Part>() {
            @Override
            protected void updateItem(Part item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    // Check if Quantity is below Threshold
                    if (item.getQuantity() <= item.getThreshold()) {
                        // Highlight Red
                        setStyle("-fx-background-color: #ffcccc; -fx-text-background-color: black;");
                    } else {
                        // Reset to default
                        setStyle("");
                    }
                }
            }
        });

        // 1. Part Name
        TableColumn<Part, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        // 2. Car Model
        TableColumn<Part, String> colModel = new TableColumn<>("Car Model");
        colModel.setCellValueFactory(data -> {
            if (data.getValue() instanceof BodyPart) {
                return new SimpleStringProperty(((BodyPart) data.getValue()).getCarModel());
            }
            return new SimpleStringProperty("-");
        });

        // 3. Unit Price
        TableColumn<Part, Number> colPrice = new TableColumn<>("Unit Price");
        colPrice.setCellValueFactory(data -> {
            if (data.getValue() instanceof BodyPart) {
                return new SimpleDoubleProperty(((BodyPart) data.getValue()).getUnitPrice());
            }
            return new SimpleDoubleProperty(0);
        });

        // 4. Quantity
        TableColumn<Part, Number> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()));

        // 5. Total Value
        TableColumn<Part, Number> colTotalVal = new TableColumn<>("Total Value");
        colTotalVal.setCellValueFactory(data -> {
            if (data.getValue() instanceof BodyPart) {
                double price = ((BodyPart) data.getValue()).getUnitPrice();
                int qty = data.getValue().getQuantity();
                return new SimpleDoubleProperty(price * qty);
            }
            return new SimpleDoubleProperty(0);
        });

        // 6. Update Stock Column (Buttons)
        TableColumn<Part, Void> colAction = new TableColumn<>("Update Stock");
        colAction.setCellFactory(createActionCellFactory(table));

        table.getColumns().addAll(colName, colModel, colPrice, colQty, colTotalVal, colAction);

        layout.getChildren().add(table);
        return layout;
    }

    /**
     * Helper to create the cell with [-] and [+] buttons.
     */
    private Callback<TableColumn<Part, Void>, TableCell<Part, Void>> createActionCellFactory(TableView<Part> table) {
        return param -> new TableCell<>() {
            private final Button btnDecrease = new Button("-");
            private final Button btnIncrease = new Button("+");
            private final HBox pane = new HBox(10, btnDecrease, btnIncrease);

            {
                pane.setAlignment(Pos.CENTER);

                btnDecrease.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 30px;");
                btnIncrease.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 30px;");

                // --- ACTION: DECREASE (SELL) ---
                btnDecrease.setOnAction(event -> {
                    Part part = getTableView().getItems().get(getIndex());
                    if (part.getQuantity() > 0) {
                        part.setQuantity(part.getQuantity() - 1);
                        salesList.add(new Sale(part, 1));
                        table.refresh();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Out of Stock!");
                        alert.show();
                    }
                });

                // --- ACTION: INCREASE (RESTOCK) ---
                btnIncrease.setOnAction(event -> {
                    Part part = getTableView().getItems().get(getIndex());
                    part.setQuantity(part.getQuantity() + 1);
                    table.refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        };
    }
}