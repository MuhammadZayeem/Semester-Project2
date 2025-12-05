package SemesterProject.GUI;

import SemesterProject.Part;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow; // Import TableRow
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.stream.Collectors;

public class CategorizedInventoryView extends VBox {

    private List<Part> masterPartList;
    private MainApp app;

    // UI Elements for dynamic display
    private TableView<Part> inventoryTable;
    private TabPane majorSubCategoryTabs;

    // Footer Summary Labels
    private Label lblTotalCount;
    private Label lblTotalValue;

    public CategorizedInventoryView(List<Part> masterPartList, MainApp app) {
        this.masterPartList = masterPartList;
        this.app = app;
        this.setPadding(new Insets(20));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: white;");

        // 1. Initialize the TableView
        inventoryTable = createInventoryTable();
        VBox.setVgrow(inventoryTable, Priority.ALWAYS);

        // 2. Create the main layout
        VBox categorizedLayout = createCategorizedLayout();

        // 3. Create Footer Summary
        HBox footer = createSummaryFooter();

        this.getChildren().addAll(categorizedLayout, inventoryTable, footer);

        // Load initial data for the default view
        loadInventoryDataBySpecificType("Front Laminated");
    }

    private HBox createSummaryFooter() {
        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10));
        footer.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1px 0 0 0;");

        Label lblCountTitle = new Label("Total Parts:");
        lblCountTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblTotalCount = new Label("0");
        lblTotalCount.setFont(Font.font("Arial", 14));

        Label lblValueTitle = new Label("Total Inventory Value:");
        lblValueTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblTotalValue = new Label("PKR 0.00");
        lblTotalValue.setFont(Font.font("Arial", 14));
        lblTotalValue.setStyle("-fx-text-fill: #27ae60;");

        footer.getChildren().addAll(lblCountTitle, lblTotalCount, lblValueTitle, lblTotalValue);
        return footer;
    }

    private void updateSummary(List<Part> displayedParts) {
        int count = 0;
        double value = 0.0;

        for (Part p : displayedParts) {
            count += p.getCurrentStock();
            value += (p.getCurrentStock() * p.getUnitPrice());
        }

        lblTotalCount.setText(String.valueOf(count));
        lblTotalValue.setText(String.format("PKR %.2f", value));
    }

    private VBox createCategorizedLayout() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(0));

        Label lblTitle = new Label("Categorized Inventory");
        lblTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        TabPane categoryTabs = new TabPane();
        categoryTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        categoryTabs.setStyle("-fx-background-color: white;");

        VBox bodyContent = createBodyPartsContent();
        Tab bodyTab = new Tab("Body Parts", bodyContent);

        Tab engineTab = new Tab("Engine Parts", new Label("Engine Parts Inventory Management"));

        categoryTabs.getTabs().addAll(bodyTab, engineTab);

        container.getChildren().addAll(lblTitle, categoryTabs);

        return container;
    }

    private VBox createBodyPartsContent() {
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10, 0, 0, 0));

        majorSubCategoryTabs = new TabPane();
        majorSubCategoryTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox glassContent = createGlassSubCategory();
        Tab glassTab = new Tab("Glass", glassContent);

        VBox bumpersContent = createBumpersSubCategory();
        Tab bumpersTab = new Tab("Bumpers", bumpersContent);

        majorSubCategoryTabs.getTabs().addAll(glassTab, bumpersTab);

        majorSubCategoryTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && newTab != oldTab) {
                VBox deepestContent = (VBox) newTab.getContent();
                if (deepestContent.getChildren().isEmpty() || !(deepestContent.getChildren().get(0) instanceof TabPane)) return;

                TabPane deepestTabs = (TabPane) deepestContent.getChildren().get(0);

                if (!deepestTabs.getTabs().isEmpty()) {
                    deepestTabs.getSelectionModel().select(0);
                    loadInventoryDataBySpecificType(deepestTabs.getSelectionModel().getSelectedItem().getText());
                }
            }
        });

        contentBox.getChildren().add(majorSubCategoryTabs);
        return contentBox;
    }

    private VBox createGlassSubCategory() {
        VBox glassBox = new VBox(5);
        glassBox.setPadding(new Insets(5));

        TabPane glassTabs = new TabPane();
        glassTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab frontLaminatedTab = new Tab("Front Laminated", new Label());
        Tab frontGlassTab = new Tab("Front Glass", new Label());
        Tab rearGlassTab = new Tab("Rear Glass", new Label());
        Tab doorGlassTab = new Tab("Door Glass", new Label());

        glassTabs.getTabs().addAll(frontLaminatedTab, frontGlassTab, rearGlassTab, doorGlassTab);

        glassTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                loadInventoryDataBySpecificType(newTab.getText());
            }
        });

        glassTabs.getSelectionModel().select(frontLaminatedTab);

        glassBox.getChildren().add(glassTabs);
        return glassBox;
    }

    private VBox createBumpersSubCategory() {
        VBox bumperBox = new VBox(5);
        bumperBox.setPadding(new Insets(5));

        TabPane bumperTabs = new TabPane();
        bumperTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab frontBumperTab = new Tab("Front Bumper", new Label());
        Tab rearBumperTab = new Tab("Rear Bumper", new Label());

        bumperTabs.getTabs().addAll(frontBumperTab, rearBumperTab);

        bumperTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                loadInventoryDataBySpecificType(newTab.getText());
            }
        });

        bumperTabs.getSelectionModel().select(frontBumperTab);

        bumperBox.getChildren().add(bumperTabs);
        return bumperBox;
    }

    private TableView<Part> createInventoryTable() {
        TableView<Part> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- FIX: Row Factory for Red Alert ---
        table.setRowFactory(tv -> new TableRow<Part>() {
            @Override
            protected void updateItem(Part item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getCurrentStock() <= item.getMinThreshold()) {
                    // Alert Color: Light Red background if stock is low
                    setStyle("-fx-background-color: #ffcccc;");
                } else {
                    setStyle(""); // Default
                }
            }
        });

        TableColumn<Part, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Part, String> colModel = new TableColumn<>("Car Model");
        colModel.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCarModel()));

        TableColumn<Part, String> colPrice = new TableColumn<>("Unit Price");
        colPrice.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getUnitPrice())));
        colPrice.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Part, Number> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCurrentStock()));
        colQty.setStyle("-fx-alignment: CENTER;");

        TableColumn<Part, String> colTotal = new TableColumn<>("Total Value");
        colTotal.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getUnitPrice() * data.getValue().getCurrentStock())));
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Part, Void> colActions = new TableColumn<>("Update Stock");
        colActions.setPrefWidth(120);
        colActions.setStyle("-fx-alignment: CENTER;");
        colActions.setCellFactory(param -> new TableCell<Part, Void>() {
            private final Button btnMinus = new Button("-");
            private final Button btnPlus = new Button("+");
            private final HBox pane = new HBox(5, btnMinus, btnPlus);

            {
                btnMinus.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                btnPlus.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

                btnMinus.setOnAction(event -> {
                    Part part = getTableView().getItems().get(getIndex());
                    if (part.getCurrentStock() > 0) {
                        app.updatePartStock(part, -1);
                        getTableView().refresh();
                        updateSummary(getTableView().getItems()); // Refresh footer
                    }
                });

                btnPlus.setOnAction(event -> {
                    Part part = getTableView().getItems().get(getIndex());
                    app.updatePartStock(part, 1);
                    getTableView().refresh();
                    updateSummary(getTableView().getItems()); // Refresh footer
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });


        table.getColumns().addAll(colName, colModel, colPrice, colQty, colTotal, colActions);
        return table;
    }

    private void loadInventoryDataBySpecificType(String specificType) {
        final String filterKeyword = specificType.toLowerCase().replaceAll(" ", "");
        String targetClassName = "";
        if (filterKeyword.contains("laminated")) targetClassName = "FrontLaminatedGlass".toLowerCase();
        else if (filterKeyword.contains("frontglass")) targetClassName = "FrontGlass".toLowerCase();
        else if (filterKeyword.contains("rearglass")) targetClassName = "RearGlass".toLowerCase();
        else if (filterKeyword.contains("doorglass")) targetClassName = "DoorGlass".toLowerCase();
        else if (filterKeyword.contains("frontbumper")) targetClassName = "FrontBumper".toLowerCase();
        else if (filterKeyword.contains("rearbumper")) targetClassName = "RearBumper".toLowerCase();

        final String finalTarget = targetClassName;
        List<Part> filteredList = masterPartList.stream()
                .filter(p -> p.getClass().getSimpleName().toLowerCase().contains(finalTarget))
                .collect(Collectors.toList());

        inventoryTable.setItems(FXCollections.observableArrayList(filteredList));

        // Update the summary footer whenever data is loaded
        updateSummary(filteredList);
    }

    public void refreshTable() {
        Tab selectedMajorTab = majorSubCategoryTabs.getSelectionModel().getSelectedItem();
        if (selectedMajorTab != null) {
            VBox content = (VBox) selectedMajorTab.getContent();
            if (!content.getChildren().isEmpty() && content.getChildren().get(0) instanceof TabPane) {
                TabPane deepestTabs = (TabPane) content.getChildren().get(0);
                Tab selectedDeepTab = deepestTabs.getSelectionModel().getSelectedItem();
                if (selectedDeepTab != null) {
                    loadInventoryDataBySpecificType(selectedDeepTab.getText());
                    return;
                }
            }
        }
        loadInventoryDataBySpecificType("Front Laminated");
    }
}