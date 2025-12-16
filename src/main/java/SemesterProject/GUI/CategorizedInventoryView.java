package SemesterProject.GUI;

import SemesterProject.Part;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

/**
 * SAME functionality as CategorizedInventoryView
 * BUT implemented using GridPane instead of StackPane
 */
public class CategorizedInventoryView extends GridPane {

    private final MainApp app;
    private final List<Part> masterPartList;

    public CategorizedInventoryView(List<Part> masterPartList, MainApp app) {
        this.masterPartList = masterPartList;
        this.app = app;

        setPadding(new Insets(20));
        setHgap(20);
        setVgap(20);
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #ecf0f1;");

        showLevel1_Categories();
    }

    /* ==============================
       CLEAR GRID (REPLACES StackPane clear)
       ============================== */
    private void clearGrid() {
        getChildren().clear();
    }

    /* ==============================
       LEVEL 1 : BODY / ENGINE
       ============================== */
    public void showLevel1_Categories() {
        clearGrid();

        Label title = new Label("Select Category");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        Button btnBody = createMainButton("Body Parts");
        Button btnEngine = createMainButton("Engine Parts");

        btnBody.setOnAction(e -> showLevel2_SubTypes("Body"));
        btnEngine.setOnAction(e -> showLevel2_SubTypes("Engine"));

        add(title, 0, 0, 2, 1);
        setHalignment(title, javafx.geometry.HPos.CENTER);

        add(btnBody, 0, 1);
        add(btnEngine, 1, 1);
    }

    /* ==============================
       LEVEL 2 : SUB CATEGORIES
       ============================== */
    public void showLevel2_SubTypes(String category) {
        clearGrid();

        Label title = new Label(category + " Parts");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        add(title, 0, 0, 2, 1);
        setHalignment(title, javafx.geometry.HPos.CENTER);

        List<String> subTypes = new ArrayList<>();

        if (category.equals("Body")) {
            subTypes = List.of(
                    "FrontLaminatedGlass",
                    "FrontGlass",
                    "RearGlass",
                    "DoorGlass",
                    "FrontBumper",
                    "RearBumper"
            );
        } else {
            subTypes = List.of("Pistons", "SparkPlugs");
        }

        int row = 1;
        for (String type : subTypes) {
            String displayName = type.replaceAll("(.)([A-Z])", "$1 $2");
            Button btn = new Button(displayName);
            btn.setPrefWidth(300);
            btn.setFont(Font.font(16));

            btn.setOnAction(e -> showLevel3_PartTable(type, displayName, category));
            add(btn, 0, row++, 2, 1);
            setHalignment(btn, javafx.geometry.HPos.CENTER);
        }

        Button btnBack = new Button("⬅ Back");
        btnBack.setOnAction(e -> showLevel1_Categories());
        add(btnBack, 0, row, 2, 1);
        setHalignment(btnBack, javafx.geometry.HPos.CENTER);
    }

    /* ==============================
       LEVEL 3 : PARTS TABLE
       ============================== */
    public void showLevel3_PartTable(String rawType, String displayName, String category) {
        clearGrid();

        Label title = new Label(displayName + " Inventory");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        TableView<Part> table = new TableView<>();
        table.setPrefHeight(350);

        TableColumn<Part, String> nameCol = new TableColumn<>("Part Name");
        nameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getName()));
        nameCol.setPrefWidth(200);

        TableColumn<Part, Number> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getCurrentStock()));
        qtyCol.setPrefWidth(100);

        TableColumn<Part, Number> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getUnitPrice()));
        priceCol.setPrefWidth(120);

        TableColumn<Part, Number> totalCol = new TableColumn<>("Total Value");
        totalCol.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(
                d.getValue().getCurrentStock() * d.getValue().getUnitPrice()));
        totalCol.setPrefWidth(150);

        table.getColumns().addAll(nameCol, qtyCol, priceCol, totalCol);

        int totalQty = 0;
        double totalValue = 0;

        for (Part p : masterPartList) {
            if (p.getClass().getSimpleName().equalsIgnoreCase(rawType)) {
                table.getItems().add(p);
                totalQty += p.getCurrentStock();
                totalValue += p.getCurrentStock() * p.getUnitPrice();
            }
        }

        Label summary = new Label("Total Qty: " + totalQty + " | Total Value: $" + String.format("%.2f", totalValue));
        summary.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        table.setRowFactory(tv -> {
            TableRow<Part> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty()) {
                    showQuickStockDialog(row.getItem(), rawType, category);
                }
            });
            return row;
        });

        Button btnBack = new Button("⬅ Back");
        btnBack.setOnAction(e -> showLevel2_SubTypes(category));

        add(title, 0, 0, 2, 1);
        add(table, 0, 1, 2, 1);
        add(summary, 0, 2, 2, 1);
        add(btnBack, 0, 3, 2, 1);

        setHalignment(title, javafx.geometry.HPos.CENTER);
        setHalignment(summary, javafx.geometry.HPos.CENTER);
        setHalignment(btnBack, javafx.geometry.HPos.CENTER);
    }

    /* ==============================
       QUICK + / - DIALOG
       ============================== */
    private void showQuickStockDialog(Part part, String rawType, String category) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Stock");
        dialog.setHeaderText(part.getName());

        Label qty = new Label("Current Qty: " + part.getCurrentStock());
        qty.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Button plus = new Button("➕ Add 1");
        Button minus = new Button("➖ Remove 1");

        plus.setOnAction(e -> {
            app.increaseStock(part);
            qty.setText("Current Qty: " + part.getCurrentStock());
        });

        minus.setOnAction(e -> {
            if (part.getCurrentStock() <= 0) {
                new Alert(Alert.AlertType.ERROR, "Out of stock").show();
                return;
            }
            app.recordSale(part);
            qty.setText("Current Qty: " + part.getCurrentStock());
        });

        HBox buttons = new HBox(15, minus, plus);
        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(15, qty, buttons);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();

        showLevel3_PartTable(rawType, rawType, category);
    }

    private Button createMainButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(260, 70);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        return btn;
    }
}
