package SemesterProject.GUI;

import SemesterProject.Body.*;
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

import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    private Stage primaryStage;
    private User currentUser;

    // Data Stores
    private User[] users = new User[10];
    private InventoryManager inventoryManager;
    private DemandManager demandManager;
    private DashboardManager dashboardManager;

    // Master Lists (The Database)
    private List<Part> masterPartList = new ArrayList<>();
    private List<Sale> masterSaleList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        initializeData(); // <--- This loads the Corolla/Civic data

        // Start with Login Screen
        showLoginScreen();
    }

    private void initializeData() {
        // 1. Create Users
        users[0] = new Admin("U01", "admin", "123", "System Admin", "0000");
        users[1] = new Staff("U02", "staff", "123", "John Doe", "1111");

        // 2. Init Managers
        inventoryManager = new InventoryManager();
        demandManager = new DemandManager();

        // 3. --- LOAD MOCK DATA (COROLLA, CIVIC, CITY) ---
        loadInventoryData();

        // 4. Init Dashboard with the populated lists
        dashboardManager = new DashboardManager(masterPartList, masterSaleList, demandManager);
    }

    private void loadInventoryData() {
        // --- TOYOTA COROLLA DATA ---
        masterPartList.add(new FrontLaminatedGlass("Corolla 2022", 15, 5, 18000));
        masterPartList.add(new FrontGlass("Corolla 2022", 8, 3, 9500));
        masterPartList.add(new RearGlass("Corolla 2022", 12, 4, 22000));
        masterPartList.add(new DoorGlass("Corolla 2022", 30, 10, 7500));
        masterPartList.add(new FrontBumper("Corolla Bumper", "Corolla 2022", 6, 2, 35000));

        // --- HONDA CIVIC DATA ---
        masterPartList.add(new FrontLaminatedGlass("Civic X", 10, 5, 25000));
        masterPartList.add(new FrontGlass("Civic X", 5, 2, 12000)); // Low stock example
        masterPartList.add(new RearGlass("Civic X", 8, 3, 28000));
        masterPartList.add(new DoorGlass("Civic X", 20, 8, 9000));
        masterPartList.add(new FrontBumper("Civic X Bumper", "Civic X", 4, 2, 45000));

        // --- SUZUKI CULTUS DATA ---
        masterPartList.add(new FrontLaminatedGlass("Cultus 2019", 20, 5, 12000));
        masterPartList.add(new FrontGlass("Cultus 2019", 15, 5, 6000));
        masterPartList.add(new DoorGlass("Cultus 2019", 40, 10, 4500));

        // Add all to manager as well (to keep them synced if you use manager logic later)
        for(Part p : masterPartList) {
            inventoryManager.addPart(p);
        }
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
        user.setLastLogin();

        // Pass the populated lists to the Layout
        MainLayout mainLayout = new MainLayout(
                this,
                user,
                inventoryManager,
                demandManager,
                dashboardManager,
                masterPartList // <--- Contains the Corolla data
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
                    showMainDashboard(u);
                    return true;
                }
            }
        }
        showAlert("Login Failed", "Invalid Username or Password");
        return false;
    }

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