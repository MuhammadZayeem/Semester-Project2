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

public class CategorizedInventoryView extends StackPane {

    private MainApp app;
    private List<Part> masterPartList;

    private VBox currentView;

    public CategorizedInventoryView(List<Part> masterPartList, MainApp app) {
        this.masterPartList = masterPartList;
        this.app = app;
        this.setStyle("-fx-background-color: #ecf0f1;");
        this.setPadding(new Insets(20));
        showLevel1_Categories();
    }

    // LEVEL 1: CATEGORIES
    public void showLevel1_Categories() {
        this.getChildren().clear();
        Label lblTitle = new Label("Select Category");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        HBox tilesContainer = new HBox(40);
        tilesContainer.setAlignment(Pos.CENTER);

        Button btnBody = createTile("🚗 Body Parts", "#3498db");
        btnBody.setOnAction(e -> showLevel2_SubTypes("Body"));

        Button btnEngine = createTile("⚙️ Engine Parts", "#e67e22");
        btnEngine.setOnAction(e -> showLevel2_SubTypes("Engine"));

        tilesContainer.getChildren().addAll(btnBody, btnEngine);

        currentView = new VBox(40, lblTitle, tilesContainer);
        currentView.setAlignment(Pos.CENTER);
        this.getChildren().add(currentView);
    }

    // LEVEL 2: SUB-CATEGORIES
    public void showLevel2_SubTypes(String category) {
        this.getChildren().clear();
        Label lblTitle = new Label(category + " Categories");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        FlowPane tilesPane = new FlowPane();
        tilesPane.setHgap(20); tilesPane.setVgap(20);
        tilesPane.setAlignment(Pos.CENTER);

        List<String> subTypes = new ArrayList<>();
        if (category.equals("Body")) {
            subTypes.add("FrontLaminatedGlass"); subTypes.add("FrontGlass");
            subTypes.add("RearGlass"); subTypes.add("DoorGlass");
            subTypes.add("FrontBumper"); subTypes.add("RearBumper");
        } else {
            subTypes.add("Pistons"); subTypes.add("SparkPlugs");
        }

        for (String type : subTypes) {
            String displayName = type.replaceAll("(.)([A-Z])", "$1 $2");
            Button btnType = createTile(displayName, "#9b59b6");
            btnType.setOnAction(e -> showLevel3_PartList(type, displayName));
            tilesPane.getChildren().add(btnType);
        }

        Button btnBack = new Button("⬅ Back to Home");
        btnBack.setOnAction(e -> showLevel1_Categories());

        currentView = new VBox(20, btnBack, lblTitle, tilesPane);
        currentView.setAlignment(Pos.TOP_CENTER);
        this.getChildren().add(currentView);
    }

    // LEVEL 3: PART LIST
    public void showLevel3_PartList(String rawTypeClass, String displayName) {
        this.getChildren().clear();
        Label lblTitle = new Label(displayName + " Inventory");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        FlowPane tilesPane = new FlowPane();
        tilesPane.setHgap(20); tilesPane.setVgap(20);
        tilesPane.setAlignment(Pos.CENTER);

        List<Part> filteredParts = new ArrayList<>();
        for (Part p : masterPartList) {
            if (p.getClass().getSimpleName().equalsIgnoreCase(rawTypeClass)) filteredParts.add(p);
        }

        if (filteredParts.isEmpty()) {
            tilesPane.getChildren().add(new Label("No items found."));
        } else {
            for (Part p : filteredParts) {
                String labelText = p.getName() + "\n(Qty: " + p.getCurrentStock() + ")";
                Button btnPart = createTile(labelText, "#2ecc71");
                btnPart.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center; -fx-wrap-text: true;");
                btnPart.setPrefSize(180, 100);
                btnPart.setOnAction(e -> showLevel4_PartDetail(p));
                tilesPane.getChildren().add(btnPart);
            }
        }

        Button btnBack = new Button("⬅ Back to Categories");
        btnBack.setOnAction(e -> showLevel2_SubTypes("Body"));

        currentView = new VBox(20, btnBack, lblTitle, tilesPane);
        currentView.setAlignment(Pos.TOP_CENTER);
        this.getChildren().add(currentView);
    }

    // LEVEL 4: DETAIL (UPDATED Logic)
    public void showLevel4_PartDetail(Part part) {
        this.getChildren().clear();

        Label lblName = new Label(part.getName());
        lblName.setFont(Font.font("Arial", FontWeight.BOLD, 32));

        Label lblModel = new Label("Model: " + part.getCarModel());
        lblModel.setFont(Font.font("Arial", 18));

        Label lblPrice = new Label("Price: $" + part.getUnitPrice());
        lblPrice.setFont(Font.font("Arial", 18));

        Label lblQtyTitle = new Label("Current Quantity");
        Label lblQty = new Label(String.valueOf(part.getCurrentStock()));
        lblQty.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        lblQty.setStyle("-fx-text-fill: #34495e;");

        HBox actionBox = new HBox(30);
        actionBox.setAlignment(Pos.CENTER);

        Button btnDecrease = new Button("➖ Sale (-1)");
        btnDecrease.setPrefSize(160, 60);
        btnDecrease.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnIncrease = new Button("➕ Stock (+1)");
        btnIncrease.setPrefSize(160, 60);
        btnIncrease.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        // INCREASE
        btnIncrease.setOnAction(e -> {
            app.increaseStock(part);
            lblQty.setText(String.valueOf(part.getCurrentStock()));
        });

        // DECREASE (Updated: No Dialog, Automatic Logic)
        btnDecrease.setOnAction(e -> {
            if (part.getCurrentStock() <= 0) {
                new Alert(Alert.AlertType.ERROR, "Out of Stock!").showAndWait();
                return;
            }
            // 1. Directly Record Sale
            app.recordSale(part);

            // 2. Update Label
            lblQty.setText(String.valueOf(part.getCurrentStock()));

            // 3. Optional: Visual Confirmation
            if (part.getCurrentStock() <= 5) {
                lblQty.setStyle("-fx-text-fill: red;"); // Turn red if low stock
            }
        });

        actionBox.getChildren().addAll(btnDecrease, btnIncrease);

        Button btnBack = new Button("⬅ Back to List");
        btnBack.setOnAction(e -> showLevel3_PartList(part.getClass().getSimpleName(), part.getClass().getSimpleName()));

        currentView = new VBox(20, btnBack, lblName, lblModel, lblPrice, new Separator(), lblQtyTitle, lblQty, actionBox);
        currentView.setAlignment(Pos.CENTER);
        currentView.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        currentView.setMaxSize(600, 500);

        this.getChildren().add(currentView);
    }

    private Button createTile(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setPrefSize(220, 150);
        btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 10;");
        return btn;
    }

    public void refreshTable() { showLevel1_Categories(); }
}