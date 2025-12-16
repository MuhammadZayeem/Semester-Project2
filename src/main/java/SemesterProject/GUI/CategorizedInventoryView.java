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

        setPadding(new Insets(20));
        setHgap(20);
        setVgap(20);
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #ecf0f1;");

        showLevel1_Categories();
    }

    private void clearGrid() { getChildren().clear(); }

    // LEVEL 1: CATEGORY
    public void showLevel1_Categories() {
        clearGrid();

        Label title = new Label("Select Category");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        Button btnBody = createMainButton("Body Parts");
        btnBody.setOnAction(e -> showLevel2_SubTypes("Body"));

        // --- ADDED BACK BUTTON HERE ---
        Button btnBack = new Button("⬅ Back to Dashboard");
        btnBack.setOnAction(e -> app.showMainDashboard());

        add(title, 0, 0);
        setHalignment(title, javafx.geometry.HPos.CENTER);

        add(btnBody, 0, 1);
        setHalignment(btnBody, javafx.geometry.HPos.CENTER);

        // Add Back Button at Row 2
        add(btnBack, 0, 2);
        setHalignment(btnBack, javafx.geometry.HPos.CENTER);
    }

    // LEVEL 2: SUB-CATEGORIES
    public void showLevel2_SubTypes(String category) {
        clearGrid();

        Label title = new Label(category + " Parts");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        add(title, 0, 0);
        setHalignment(title, javafx.geometry.HPos.CENTER);

        List<String> subTypes = List.of(
                "FrontLaminatedGlass","FrontGlass","RearGlass","DoorGlass","FrontBumper","RearBumper"
        );

        int row = 1;
        for (String type : subTypes) {
            String displayName = type.replaceAll("(.)([A-Z])", "$1 $2");
            Button btn = new Button(displayName);
            btn.setPrefWidth(300);
            btn.setFont(Font.font(16));
            btn.setOnAction(e -> showLevel3_PartTable(type, displayName, category));
            add(btn, 0, row++);
            setHalignment(btn, javafx.geometry.HPos.CENTER);
        }

        Button btnBack = new Button("⬅ Back");
        btnBack.setOnAction(e -> showLevel1_Categories());
        add(btnBack, 0, row);
        setHalignment(btnBack, javafx.geometry.HPos.CENTER);
    }

    // LEVEL 3: PART TABLE
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
        totalCol.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getCurrentStock()*d.getValue().getUnitPrice()));
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
            row.setOnMouseClicked(e -> { if (!row.isEmpty()) showQuickStockDialog(row.getItem(), rawType, category); });
            return row;
        });

        Button btnBack = new Button("⬅ Back");
        btnBack.setOnAction(e -> showLevel2_SubTypes(category));

        Button btnAdd = new Button("➕ Add New Part");
        btnAdd.setOnAction(e -> showAddPartDialog(category));

        add(title, 0, 0);
        add(table, 0, 1);
        add(summary, 0, 2);
        add(btnBack, 0, 3);
        add(btnAdd, 0, 4);

        setHalignment(title, javafx.geometry.HPos.CENTER);
        setHalignment(summary, javafx.geometry.HPos.CENTER);
        setHalignment(btnBack, javafx.geometry.HPos.CENTER);
        setHalignment(btnAdd, javafx.geometry.HPos.CENTER);
    }

    // ADD NEW PART (using reflection)
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
                    int threshold = 5; // default

                    // Reflection to create object dynamically
                    String className = "SemesterProject.Body." + type;
                    Class<?> clazz = Class.forName(className);
                    Constructor<?> constructor = clazz.getConstructor(
                            String.class, String.class, String.class, int.class, int.class, double.class
                    );
                    Part newPart = (Part) constructor.newInstance(type + "-" + name, name, model, qty, threshold, price);

                    masterPartList.add(newPart);

                    // Refresh table for this exact type
                    showLevel3_PartTable(type, type.replaceAll("(.)([A-Z])", "$1 $2"), "Body");

                } catch (Exception ex){
                    new Alert(Alert.AlertType.ERROR, "Invalid input!").showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // QUICK STOCK ADJUST DIALOG
    private void showQuickStockDialog(Part part, String rawType, String category) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Stock");
        dialog.setHeaderText(part.getName());

        Label qty = new Label("Current Qty: " + part.getCurrentStock());
        qty.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Button plus = new Button("➕ Add 1");
        Button minus = new Button("➖ Remove 1");
        plus.setOnAction(e -> {
            part.addQuantity(1);
            qty.setText("Current Qty: " + part.getCurrentStock());


            });
        minus.setOnAction(e -> {
            if(part.getCurrentStock() > 0){
                part.addQuantity(-1);
                qty.setText("Current Qty: " + part.getCurrentStock());
                app.recordSale(part);

                } });

        HBox buttons = new HBox(15, minus, plus); buttons.setAlignment(Pos.CENTER);
        VBox content = new VBox(15, qty, buttons); content.setAlignment(Pos.CENTER); content.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();

        showLevel3_PartTable(rawType, rawType.replaceAll("(.)([A-Z])", "$1 $2"), category);
    }

    private Button createMainButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(260, 70);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        return btn;
    }
}