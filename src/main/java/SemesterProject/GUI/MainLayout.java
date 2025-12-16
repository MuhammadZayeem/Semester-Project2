package SemesterProject.GUI;

import SemesterProject.Login.Admin;
import SemesterProject.User;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainLayout {

    // The main layout container (Now a GridPane)
    private GridPane layout;

    // Labels for Data
    private Label lblTotalStock;
    private Label lblLowStock;
    private Label lblSoldToday;
    private Label lblPendingDemands;

    public MainLayout(MainApp app, User currentUser) {
        // Initialize the layout container as GridPane
        layout = new GridPane();
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(40));
        layout.setVgap(30); // Spacing between rows (Title, Stats, Menu)
        layout.setStyle("-fx-background-color: #ecf0f1;");

        // --- 1. HEADER ---
        Label lblTitle = new Label("IMS SYSTEM - Main Dashboard");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        lblTitle.setStyle("-fx-text-fill: #2c3e50;");

        Label lblUser = new Label("Logged in as: " + currentUser.getUsername());
        lblUser.setFont(Font.font("Arial", 16));
        lblUser.setStyle("-fx-text-fill: #7f8c8d;");

        // --- 2. STATS TILES (Nested Grid) ---
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);

        lblTotalStock = createDataLabel("0");
        lblLowStock = createDataLabel("0");
        lblSoldToday = createDataLabel("0");
        lblPendingDemands = createDataLabel("0");

        statsGrid.add(createStatsTile("Total Parts", lblTotalStock, "#3498db"), 0, 0); // Blue
        statsGrid.add(createStatsTile("Low Stock", lblLowStock, "#e74c3c"), 1, 0);     // Red
        statsGrid.add(createStatsTile("Sold Today", lblSoldToday, "#27ae60"), 2, 0);   // Green
        statsGrid.add(createStatsTile("Demands", lblPendingDemands, "#f39c12"), 3, 0); // Orange

        // --- 3. NAVIGATION MENU (Nested Grid) ---
        GridPane navGrid = new GridPane();
        navGrid.setHgap(20);
        navGrid.setVgap(20);
        navGrid.setAlignment(Pos.CENTER);

        Button btnInventory = createNavButton("📦 Inventory", "#34495e");
        btnInventory.setOnAction(e -> app.showInventoryView());

        Button btnSales = createNavButton("💰 Sales History", "#34495e");
        btnSales.setOnAction(e -> app.showSalesHistory());

        Button btnDemand = createNavButton("📋 Demand List", "#34495e");
        btnDemand.setOnAction(e -> app.showDemandList());

        Button btnLogout = createNavButton("🚪 Logout", "#e74c3c");
        btnLogout.setOnAction(e -> app.showLoginScreen());

        navGrid.add(btnInventory, 0, 0);
        navGrid.add(btnSales, 1, 0);
        navGrid.add(btnDemand, 0, 1);
        navGrid.add(btnLogout, 1, 1);

        if (currentUser instanceof Admin) {
            Button btnUsers = createNavButton("👤 Manage Users", "#8e44ad");
            btnUsers.setOnAction(e -> app.showUserManagement());
            // Span across 2 columns
            navGrid.add(btnUsers, 0, 2, 2, 1);
            btnUsers.setMaxWidth(Double.MAX_VALUE);
        }

        // --- ADDING TO ROOT GRIDPANE ---
        // Row 0: Title
        layout.add(lblTitle, 0, 0);
        GridPane.setHalignment(lblTitle, HPos.CENTER);

        // Row 1: User Info
        layout.add(lblUser, 0, 1);
        GridPane.setHalignment(lblUser, HPos.CENTER);

        // Row 2: Stats Tiles
        layout.add(statsGrid, 0, 2);
        GridPane.setHalignment(statsGrid, HPos.CENTER);

        // Row 3: Navigation Menu
        layout.add(navGrid, 0, 3);
        GridPane.setHalignment(navGrid, HPos.CENTER);
    }

    // --- Critical Method to return the view ---
    public GridPane getView() {
        return layout;
    }

    public void updateMetrics(int total, int low, int sold, int demands) {
        lblTotalStock.setText(String.valueOf(total));
        lblLowStock.setText(String.valueOf(low));
        lblSoldToday.setText(String.valueOf(sold));
        lblPendingDemands.setText(String.valueOf(demands));
    }

    // --- UI Helpers ---

    private VBox createStatsTile(String title, Label dataLabel, String colorHex) {
        VBox tile = new VBox(10);
        tile.setAlignment(Pos.CENTER);
        tile.setPadding(new Insets(15));
        tile.setPrefSize(200, 120);
        tile.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Arial", 16));
        lblTitle.setStyle("-fx-text-fill: white;");
        dataLabel.setStyle("-fx-text-fill: white;");

        tile.getChildren().addAll(lblTitle, dataLabel);
        return tile;
    }

    private Button createNavButton(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setPrefSize(250, 80);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + colorHex + ", -20%); -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;"));

        return btn;
    }

    private Label createDataLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        return lbl;
    }
}