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

    //--------------------------------Managers
    private InventoryManager inventoryManager;
    private DemandManager demandManager;
    private DashboardManager dashboardManager;

    //------------------------------------------Data Lists
    private List<Part> masterPartList;
    private List<Sale> masterSaleList;

    //--------------------------------------------Views
    private UserManagementView userView;
    private SalesView salesView;
    private CategorizedInventoryView categorizedInventoryView;

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

        this.masterSaleList = dashManager.getSalesList();

        this.categorizedInventoryView = new CategorizedInventoryView(masterPartList, this.app);


        VBox sidebar = createSidebar();
        this.setLeft(sidebar);
        dashboardManager.updateDashboardData();
        this.setCenter(dashboardManager.getView());
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2c3e50;");
        sidebar.setPrefWidth(220);

        Label lblTitle = new Label("IMS SYSTEM");
        lblTitle.setTextFill(Color.WHITE);
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label lblUser = new Label("User: " + user.getUsername());
        lblUser.setTextFill(Color.LIGHTGRAY);

        Button btnDashboard = createNavButton("Dashboard");
        btnDashboard.setOnAction(e -> {
            dashboardManager.updateDashboardData();
            this.setCenter(dashboardManager.getView());
        });

        Button btnInventory = createNavButton("Inventory");
        btnInventory.setOnAction(e -> {
            this.setCenter(categorizedInventoryView);
        });

        Button btnSales = createNavButton("Sales History");
        btnSales.setOnAction(e -> {
            salesView = new SalesView(masterSaleList);
            this.setCenter(salesView);
        });

        Button btnDemand = createNavButton("Demand List");
        btnDemand.setOnAction(e -> {
            demandManager.generateAutoDemands(masterPartList);
            SemesterProject.GUI.DemandView demandView = new DemandView(demandManager);
            this.setCenter(demandView);
        });

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> app.showLoginScreen());

        //---------------------------------------ADD BUTTONS TO SIDEBAR
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
            btnUsers.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white;");
            btnUsers.setOnAction(e -> {
                if (userView == null) userView = new UserManagementView(this.app, this.user);
                userView.refreshUserTable();
                userView.refreshResetTable();
                this.setCenter(userView);
            });
            sidebar.getChildren().add(btnUsers);
        }

        sidebar.getChildren().add(new Label(""));
        sidebar.getChildren().add(btnLogout);

        return sidebar;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER-LEFT;");
        btn.setPadding(new Insets(10));
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-alignment: CENTER-LEFT;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER-LEFT;"));

        return btn;
    }
}