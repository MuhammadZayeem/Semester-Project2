package SemesterProject.GUI;

import SemesterProject.Body.FrontLaminatedGlass;
import SemesterProject.Dashboard.DashboardManager;
import SemesterProject.Demand.DemandManager;
import SemesterProject.InventoryManagment.InventoryManager;
import SemesterProject.Login.Admin;
import SemesterProject.Login.Staff;
import SemesterProject.Part;
import SemesterProject.Sales.Sale;
import SemesterProject.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    private Stage primaryStage;
    private User currentUser;

    // Data Stores
    private User[] users = new User[10]; // Fixed array per your
    private InventoryManager inventoryManager;
    private DemandManager demandManager;
    private DashboardManager dashboardManager;
    private List<Part> masterPartList = new ArrayList<>();
    private List<Sale> masterSaleList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        initializeData();

        // Start with Login Screen
        showLoginScreen();
    }

    private void initializeData() {
        // 1. Create Dummy Users
        // Admin: username="admin", password="123"
        users[0] = new Admin("U01", "admin", "123", "System Admin", "0000");
        // Staff: username="staff", password="123"
        users[1] = new Staff("U02", "staff", "123", "John Doe", "1111");

        // 2. Init Managers
        inventoryManager = new InventoryManager();
        demandManager = new DemandManager();

        // 3. Add some dummy parts
        Part p1 = new FrontLaminatedGlass("Mehran-2009", 5, 2, 3500);
        inventoryManager.addPart(p1);
        masterPartList.add(p1);

        // 4. Init Dashboard
        dashboardManager = new DashboardManager(masterPartList, masterSaleList, demandManager);
    }

    // --- Navigation Methods ---

    public void showLoginScreen() {
        LoginView loginView = new LoginView(this);
        Scene scene = new Scene(loginView, 400, 300);
        primaryStage.setTitle("IMS Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showMainDashboard(User user) {
        this.currentUser = user;
        user.setLastLogin(); // Update login time

        // Pass the Current User and all Managers to the Layout
        MainLayout mainLayout = new MainLayout(
                this,
                user,
                inventoryManager,
                demandManager,
                dashboardManager,
                masterPartList
        );

        Scene scene = new Scene(mainLayout, 1100, 750);
        primaryStage.setTitle("Auto Parts IMS - Logged in as: " + user.getUsername());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }
    // --- Authentication Logic ---

    public boolean authenticate(String username, String password) {
        for (User u : users) {
            if (u != null && u.getUsername().equalsIgnoreCase(username)) {
                if (u.PasswordValidation(password)) {
                    showMainDashboard(u); // Success
                    return true;
                }
            }
        }
        showAlert("Login Failed", "Invalid Username or Password");
        return false;
    }

    // --- Helper for User Management View ---
    public User[] getUsers() { return users; }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}