package SemesterProject.GUI;

import SemesterProject.Dashboard.DashboardManager;
import SemesterProject.Demand.DemandManager;
import SemesterProject.InventoryManagment.InventoryManager;
import SemesterProject.Login.Admin;
import SemesterProject.Part;
import SemesterProject.Sales.Sale;
import SemesterProject.User;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class MainLayout extends BorderPane {

    private MainApp app;
    private User user;

    // Managers
    private InventoryManager inventoryManager;
    private DemandManager demandManager;
    private DashboardManager dashboardManager;

    // Lists (Shared Data)
    private List<Part> masterPartList;
    private List<Sale> masterSaleList;

    // Views (Cached if needed, or recreated on click)
    private UserManagementView userView;
    private SalesView salesView;
    // Note: InventoryView is recreated on click to ensure fresh data filtering

    public MainLayout(MainApp app, User user,
                      InventoryManager invManager,
                      DemandManager demandManager,
                      DashboardManager dashManager,
                      List<Part> masterPartList) {
        this.app = app;
        this.user = user;
        this.inventoryManager = invManager;
        this.demandManager = demandManager;
        this.dashboardManager = dashManager;
        this.masterPartList = masterPartList;

        // CRITICAL: Get the sales list from the dashboard manager
        // This ensures the Sales Tab and the Dashboard Cards see the same data
        this.masterSaleList = dashManager.getSalesList();

        // 1. Create Sidebar
        VBox sidebar = createSidebar();
        this.setLeft(sidebar);

        // 2. Default View (Center) - Show the Dashboard Tiles by default
        this.setCenter(dashboardManager.getView());
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2c3e50;"); // Dark Blue Sidebar
        sidebar.setPrefWidth(220);

        // --- App Title ---
        Label lblTitle = new Label("IMS SYSTEM");
        lblTitle.setTextFill(Color.WHITE);
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // --- Current User ---
        Label lblUser = new Label("User: " + user.getUsername());
        lblUser.setTextFill(Color.LIGHTGRAY);

        // --- NAVIGATION BUTTONS ---

        // 1. Dashboard Button
        Button btnDashboard = createNavButton("Dashboard");
        btnDashboard.setOnAction(e -> {
            // Refresh calculations before showing
            dashboardManager.updateDashboardData();
            this.setCenter(dashboardManager.getView());
        });

        // 2. Inventory Button (Links to InventoryView.java)
        Button btnInventory = createNavButton("Inventory");
        btnInventory.setOnAction(e -> {
            // Create the Inventory View, passing the shared lists
            // This enables the "Sell" button inside InventoryView to work
            InventoryView invView = new InventoryView(masterPartList, masterSaleList);
            this.setCenter(invView);
        });

        // 3. Sales History Button (Links to SalesView.java)
        Button btnSales = createNavButton("Sales History");
        btnSales.setOnAction(e -> {
            // Pass the sales list to view history
            salesView = new SalesView(masterSaleList);
            this.setCenter(salesView);
        });

        // 4. Logout Button
        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // Red
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> app.showLoginScreen());

        // Add standard elements to Sidebar
        sidebar.getChildren().addAll(lblTitle, lblUser, new Label(""), btnDashboard, btnInventory, btnSales);

        // --- ADMIN ONLY BUTTON ---
        // Only add "Manage Users" if the logged-in user is an Admin
        if (user instanceof Admin) {
            Button btnUsers = createNavButton("Manage Users");
            btnUsers.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white;"); // Purple
            btnUsers.setOnAction(e -> {
                if (userView == null) userView = new UserManagementView(app.getUsers());
                userView.refreshTable();
                this.setCenter(userView);
            });
            sidebar.getChildren().add(btnUsers);
        }

        // Add Spacer and Logout at the bottom
        sidebar.getChildren().add(new Label(""));
        sidebar.getChildren().add(btnLogout);

        return sidebar;
    }

    // Helper method to style buttons consistently
    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        btn.setPadding(new Insets(10));

        // Hover effect (optional)
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;"));

        return btn;
    }
}