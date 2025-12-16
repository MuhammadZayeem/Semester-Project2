package SemesterProject.GUI;

import SemesterProject.Part;
import SemesterProject.Body.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.lang.reflect.Constructor;
import java.util.List;

public class CategorizedInventoryView extends GridPane {

    private final MainApp app;
    private final List<Part> masterPartList;

    public CategorizedInventoryView(List<Part> masterPartList, MainApp app) {
        this.masterPartList = masterPartList;
        this.app = app;

        setPadding(new Insets(30));
        setHgap(20);
        setVgap(20);
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #ecf0f1;"); // Light grey background

        showLevel1_Categories();
    }

    private void clearGrid() { getChildren().clear(); }

    // =================================================================================
    // LEVEL 1: CATEGORY SELECTION (Updated to use Colored Tiles)
    // =================================================================================
    public void showLevel1_Categories() {
        clearGrid();

        Label title = new Label("Select Category");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: #2c3e50;");

        // Create Colored Tiles
        Button btnBody = createTileButton("🚗 Body Parts", "#3498db"); // Blue
        btnBody.setOnAction(e -> showLevel2_SubTypes("Body"));

        Button btnEngine = createTileButton("⚙️ Engine Parts", "#e67e22"); // Orange
        btnEngine.setOnAction(e -> showLevel2_SubTypes("Engine")); // Placeholder for now

        // Back Button
        Button btnBack = createBackButton("⬅ Back to Dashboard");
        btnBack.setOnAction(e -> app.showMainDashboard());

        // Layout
        add(title, 0, 0, 2, 1); // Span 2 columns
        setHalignment(title, javafx.geometry.HPos.CENTER);

        add(btnBody, 0, 1);
        add(btnEngine, 1, 1);

        add(btnBack, 0, 2, 2, 1); // Span 2 columns
        setHalignment(btnBack, javafx.geometry.HPos.CENTER);
    }

    // =================================================================================
    // LEVEL 2: SUB-CATEGORIES
    // =================================================================================
    public void showLevel2_SubTypes(String category) {
        clearGrid();

        Label title = new Label(category + " Parts");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #2c3e50;");

        add(title, 0, 0);
        setHalignment(title, javafx.geometry.HPos.CENTER);

        List<String> subTypes = List.of(
                "FrontLaminatedGlass","FrontGlass","RearGlass","DoorGlass","FrontBumper","RearBumper"
        );

        int row = 1;
        for (String type : subTypes) {
            String displayName = type.replaceAll("(.)([A-Z])", "$1 $2");
            // Use a distinct color for sub-items (Purple)
            Button btn = createWideButton(displayName, "#9b59b6");
            btn.setOnAction(e -> showLevel3_PartTable(type, displayName, category));

            add(btn, 0, row++);
            setHalignment(btn, javafx.geometry.HPos.CENTER);
        }

        Button btnBack = createBackButton("⬅ Back");
        btnBack.setOnAction(e -> showLevel1_Categories());
        add(btnBack, 0, row);
        setHalignment(btnBack, javafx.geometry.HPos.CENTER);
    }

    // =================================================================================
    // LEVEL 3: PART TABLE
    // =================================================================================
    public void showLevel3_PartTable(String rawType, String displayName, String category) {
        clearGrid();

        Label title = new Label(displayName + " Inventory");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        TableView<Part> table = new TableView<>();
        table.setPrefHeight(400);
        table.setPrefWidth(600);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Part, String> nameCol = new TableColumn<>("Part Name");
        nameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getName()));

        TableColumn<Part, Number> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getCurrentStock()));
        qtyCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Part, Number> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getUnitPrice()));
        priceCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Part, Number> totalCol = new TableColumn<>("Total Value");
        totalCol.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getCurrentStock()*d.getValue().getUnitPrice()));
        totalCol.setStyle("-fx-alignment: CENTER-RIGHT;");

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
        summary.setStyle("-fx-text-fill: #7f8c8d;");

        table.setRowFactory(tv -> {
            TableRow<Part> row = new TableRow<>();
            row.setOnMouseClicked(e -> { if (!row.isEmpty()) showQuickStockDialog(row.getItem(), rawType, category); });
            return row;
        });

        Button btnBack = createBackButton("⬅ Back");
        btnBack.setOnAction(e -> showLevel2_SubTypes(category));

        Button btnAdd = createWideButton("➕ Add New Part", "#27ae60"); // Green
        btnAdd.setOnAction(e -> showAddPartDialog(category));

        add(title, 0, 0);
        add(table, 0, 1);
        add(summary, 0, 2);
        add(btnAdd, 0, 3);
        add(btnBack, 0, 4);

        setHalignment(title, javafx.geometry.HPos.CENTER);
        setHalignment(summary, javafx.geometry.HPos.CENTER);
        setHalignment(btnBack, javafx.geometry.HPos.CENTER);
        setHalignment(btnAdd, javafx.geometry.HPos.CENTER);
    }

    // =================================================================================
    // STYLING HELPERS (THIS MAKES THEM LOOK LIKE TILES)
    // =================================================================================

    // 1. Square/Rectangular Tile Button (For Categories)
    private Button createTileButton(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setPrefSize(200, 120); // Big Tile Size
        btn.setStyle(
                "-fx-background-color: " + colorHex + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);"
        );

        // Hover
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + colorHex + ", -10%); -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);"));

        return btn;
    }

    // 2. Wide Button (For Lists)
    private Button createWideButton(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setPrefSize(300, 50);
        btn.setStyle(
                "-fx-background-color: " + colorHex + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + colorHex + ", -10%); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        return btn;
    }

    // 3. Simple Back Button
    private Button createBackButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: #bdc3c7;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        btn.setPadding(new Insets(8, 20, 8, 20));
        return btn;
    }

    // =================================================================================
    // DIALOGS & LOGIC
    // =================================================================================

    private void showAddPartDialog(String category) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Part");

        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll(
                "FrontLaminatedGlass","FrontGlass","RearGlass","DoorGlass","FrontBumper","RearBumper"
        );
        cbType.getSelectionModel().selectFirst();

        TextField tfName = new TextField();
        TextField tfModel = new TextField();
        TextField tfPrice = new TextField();
        TextField tfQty = new TextField();

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(20));
        form.add(new Label("Type:"),0,0); form.add(cbType,1,0);
        form.add(new Label("Part Name:"),0,1); form.add(tfName,1,1);
        form.add(new Label("Car Model:"),0,2); form.add(tfModel,1,2);
        form.add(new Label("Unit Price:"),0,3); form.add(tfPrice,1,3);
        form.add(new Label("Quantity:"),0,4); form.add(tfQty,1,4);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if(btn == ButtonType.OK){
                try{
                    String type = cbType.getValue();
                    String name = tfName.getText();
                    String model = tfModel.getText();
                    double price = Double.parseDouble(tfPrice.getText());
                    int qty = Integer.parseInt(tfQty.getText());
                    int threshold = 5;

                    String className = "SemesterProject.Body." + type;
                    Class<?> clazz = Class.forName(className);
                    Constructor<?> constructor = clazz.getConstructor(
                            String.class, String.class, String.class, int.class, int.class, double.class
                    );
                    Part newPart = (Part) constructor.newInstance(type + "-" + name, name, model, qty, threshold, price);

                    masterPartList.add(newPart);
                    showLevel3_PartTable(type, type.replaceAll("(.)([A-Z])", "$1 $2"), "Body");

                } catch (Exception ex){
                    new Alert(Alert.AlertType.ERROR, "Invalid input!").showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showQuickStockDialog(Part part, String rawType, String category) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Stock");
        dialog.setHeaderText(part.getName());

        Label qty = new Label("Current Qty: " + part.getCurrentStock());
        qty.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Button plus = new Button("➕ Add 1");
        Button minus = new Button("➖ Sale 1");

        // --- ACTION LOGIC LINKED TO MAIN APP ---
        plus.setOnAction(e -> {
            app.increaseStock(part);
            qty.setText("Current Qty: " + part.getCurrentStock());
        });

        minus.setOnAction(e -> {
            if(part.getCurrentStock() > 0){
                app.recordSale(part); // Calls the logic that handles demand automatically
                qty.setText("Current Qty: " + part.getCurrentStock());
            } else {
                new Alert(Alert.AlertType.ERROR, "Out of Stock!").showAndWait();
            }
        });

        HBox buttons = new HBox(15, minus, plus); buttons.setAlignment(Pos.CENTER);
        VBox content = new VBox(15, qty, buttons); content.setAlignment(Pos.CENTER); content.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();

        showLevel3_PartTable(rawType, rawType.replaceAll("(.)([A-Z])", "$1 $2"), category);
    }
}