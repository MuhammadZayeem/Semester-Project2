package SemesterProject.GUI;

import SemesterProject.InventoryManagment.InventoryManager;
import SemesterProject.Login.Admin;
import SemesterProject.Part;
import SemesterProject.Sales.Sale;
import SemesterProject.User;
import SemesterProject.Demand.DemandManager;
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

    // Shared Data Lists
    private List<Part> masterPartList;
    private List<Sale> masterSaleList;

    // Views
    private UserManagementView userView;
    private SalesView salesView;
    private CategorizedInventoryView categorizedInventoryView; // Reference to the new Inventory View

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

        // Link the sales list from Dashboard Manager
        this.masterSaleList = dashManager.getSalesList();

        // FIX 1: Initialize CategorizedInventoryView here, passing the essential this.app reference.
        this.categorizedInventoryView = new CategorizedInventoryView(masterPartList, this.app);


        // 1. Create Sidebar
        VBox sidebar = createSidebar();
        this.setLeft(sidebar);

        // 2. Default View: Show Dashboard Tiles
        dashboardManager.updateDashboardData();
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

        // 2. Inventory Button (Now shows CategorizedInventoryView)
        Button btnInventory = createNavButton("Inventory");
        btnInventory.setOnAction(e -> {
            // FIX 2: Call refresh and set the center. No need to re-instantiate.
            categorizedInventoryView.refreshTable(); // Force reload data
            this.setCenter(categorizedInventoryView);
        });

        // 3. Sales History Button
        Button btnSales = createNavButton("Sales History");
        btnSales.setOnAction(e -> {
            // View past transactions
            salesView = new SalesView(masterSaleList);
            this.setCenter(salesView);
        });

        // 4. Demand List Button (NEW)
        Button btnDemand = createNavButton("Demand List");
        btnDemand.setOnAction(e -> {
            // 1. Update the demand list based on current inventory levels
            demandManager.generateAutoDemands(masterPartList);

            // 2. Open the View using the manager
            SemesterProject.GUI.DemandView demandView = new DemandView(demandManager);
            this.setCenter(demandView);
        });

        // 5. Logout Button
        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // Red
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> app.showLoginScreen());

        // --- ADD BUTTONS TO SIDEBAR ---
        sidebar.getChildren().addAll(
                lblTitle,
                lblUser,
                new Label(""), // Spacer
                btnDashboard,
                btnInventory,
                btnSales,
                btnDemand
        );

        // --- ADMIN ONLY BUTTON ---
        if (user instanceof Admin) {
            Button btnUsers = createNavButton("Manage Users");
            btnUsers.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white;"); // Purple
            btnUsers.setOnAction(e -> {
                if (userView == null) userView = new UserManagementView(this.app, this.user);
                userView.refreshUserTable();
                userView.refreshResetTable();
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
        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER-LEFT;");
        btn.setPadding(new Insets(10));

        // Hover effects
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-alignment: CENTER-LEFT;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER-LEFT;"));

        return btn;
    }
}