package SemesterProject.GUI;

import SemesterProject.Part;
import SemesterProject.Sales.Sale;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public class InventoryView extends VBox {

    private TableView<Part> table;
    private List<Part> masterPartList;
    private List<Sale> masterSaleList;

    private Label lblStatus;
    private Spinner<Integer> spinnerQuantity;
    private Button btnSell;

    public InventoryView(List<Part> masterPartList, List<Sale> masterSaleList) {
        this.masterPartList = masterPartList;
        this.masterSaleList = masterSaleList;
        this.setPadding(new Insets(20));
        this.setSpacing(10);

        Label lblHeader = new Label("Inventory Management");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Columns
        TableColumn<Part, String> colName = new TableColumn<>("Part Name");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Part, String> colModel = new TableColumn<>("Car Model");
        colModel.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCarModel()));

        TableColumn<Part, String> colStock = new TableColumn<>("Current Stock");
        colStock.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCurrentStock())));

        TableColumn<Part, String> colMin = new TableColumn<>("Min Threshold");
        colMin.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getMinThreshold())));

        TableColumn<Part, String> colPrice = new TableColumn<>("Unit Price");
        colPrice.setCellValueFactory(data -> new SimpleStringProperty(String.format("$%.2f", data.getValue().getUnitPrice())));
        colPrice.setStyle("-fx-alignment: CENTER-RIGHT;");

        table.getColumns().addAll(colName, colModel, colStock, colMin, colPrice);

        // Sale Controls
        lblStatus = new Label();
        lblStatus.setStyle("-fx-text-fill: red;");

        spinnerQuantity = new Spinner<>(1, 100, 1);
        spinnerQuantity.setEditable(true);
        spinnerQuantity.setMaxWidth(80);

        btnSell = new Button("Record Sale");
        btnSell.setDisable(true); // Initially disabled until a part is selected
        btnSell.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox controls = new HBox(15, new Label("Quantity:"), spinnerQuantity, btnSell);
        controls.setPadding(new Insets(10, 0, 0, 0));

        // --- Event Handling ---
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            btnSell.setDisable(!isSelected);
            if (isSelected) {
                // Set max quantity for spinner based on current stock
                spinnerQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, newSelection.getCurrentStock(), 1));
                lblStatus.setText("");
            }
        });

        btnSell.setOnAction(e -> handleRecordSale());

        this.getChildren().addAll(lblHeader, table, controls, lblStatus);
        refreshTable();
    }

    private void handleRecordSale() {
        Part selectedPart = table.getSelectionModel().getSelectedItem();
        int quantity = spinnerQuantity.getValue();

        if (selectedPart == null) {
            lblStatus.setText("Please select a part to sell.");
            return;
        }

        if (quantity <= 0 || quantity > selectedPart.getCurrentStock()) {
            lblStatus.setText("Invalid quantity or stock too low.");
            // Reset spinner to safe value
            spinnerQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, selectedPart.getCurrentStock(), 1));
            return;
        }

        try {
            // 1. Calculate cost
            double totalCost = selectedPart.getUnitPrice() * quantity;

            // 2. Create Sale object with correct arguments (String, int, double)
            Sale newSale = new Sale(selectedPart.getName(), quantity, totalCost); // FIX APPLIED HERE

            // 3. Update inventory list (temporarily) and persist stock change (DatabaseManager handles this via MainApp)
            selectedPart.setCurrentStock(selectedPart.getCurrentStock() - quantity);

            // 4. Add to master sales list (temporarily)
            masterSaleList.add(newSale);

            // NOTE: In a complete, persistent system, steps 3 and 4 should delegate to InventoryManager,
            // which handles the database updates (Update Stock & Add Sale Record).
            // Assuming this delegation happens correctly elsewhere, we update the local view:

            lblStatus.setText(String.format("Sale recorded: %d x %s for $%.2f", quantity, selectedPart.getName(), totalCost));
            lblStatus.setStyle("-fx-text-fill: green;");

            refreshTable();
            table.getSelectionModel().clearSelection(); // Clear selection after sale

        } catch (Exception ex) {
            lblStatus.setText("Error recording sale: " + ex.getMessage());
            lblStatus.setStyle("-fx-text-fill: red;");
            ex.printStackTrace();
        }
    }

    public void refreshTable() {
        ObservableList<Part> data = FXCollections.observableArrayList(masterPartList);
        table.setItems(data);
    }
}