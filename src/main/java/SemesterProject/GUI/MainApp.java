package SemesterProject.GUI;

import SemesterProject.Body.*;
import SemesterProject.Demand.DemandManager;
import SemesterProject.InventoryManagment.InventoryManager;
import SemesterProject.Login.Admin;
import SemesterProject.Login.LoginManager;
import SemesterProject.Login.Staff;
import SemesterProject.DatabaseManager;
import SemesterProject.Part;
import SemesterProject.Sales.Sale;
import SemesterProject.User;
import SemesterProject.Supplier.LocalSupplier;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import SemesterProject.Login.PasswordResetRequest;
import SemesterProject.Exception.UserAlreadyExistsException;
import SemesterProject.Exception.UserNotFoundException;
import java.sql.SQLException;
import java.util.Random;

public class MainApp extends Application {

    private Stage primaryStage;
    private User currentUser;

    // Managers
    private InventoryManager inventoryManager;
    private DemandManager demandManager;
    private DashboardManager dashboardManager;
    private LoginManager loginManager;
    private DatabaseManager dbManager;

    // Master Lists
    private List<Part> masterPartList = new ArrayList<>();
    private List<Sale> masterSaleList = new ArrayList<>();

    private final Random random = new Random();
    private final LocalSupplier mockSupplier = new LocalSupplier("MockSupplier", "N/A", "N/A", "N/A", "N/A");

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeData();
        showLoginScreen();
    }

    private void initializeData() {
        dbManager = new DatabaseManager();
        loginManager = new LoginManager(dbManager);
        inventoryManager = new InventoryManager(dbManager);
        demandManager = new DemandManager();
        loadAndPopulateData();
        dashboardManager = new DashboardManager(masterPartList, masterSaleList, demandManager);
    }

    private void loadAndPopulateData() {
        if (dbManager.getAllUsers().isEmpty()) {
            try {
                dbManager.addUser(new Admin(null, "admin", "123", "System Admin", "0000"));
                dbManager.addUser(new Staff(null, "staff", "123", "John Doe", "1111"));
            } catch (Exception e) { System.err.println("Failed to insert default users: " + e.getMessage()); }
        }

        masterPartList.clear();
        masterPartList.addAll(dbManager.getAllParts());

        if (masterPartList.isEmpty()) {
            try {
                dbManager.clearAllPartsData();
                loadMockInventoryAndSaveToDB();
                masterPartList.clear();
                masterPartList.addAll(dbManager.getAllParts());
            } catch (Exception e) { System.err.println("Error loading mock data: " + e.getMessage()); }
        }

        masterSaleList.clear();
        masterSaleList.addAll(dbManager.getAllSales());
        inventoryManager.getInventory().clear();
        inventoryManager.getInventory().addAll(masterPartList);

        // FIX: Generate demands initially based on loaded data
        demandManager.generateAutoDemands(masterPartList);
    }

    private int getRandomStock() { return random.nextInt(46) + 5; }

    private void loadMockInventoryAndSaveToDB() throws Exception {
        String t = null;
        // FIX: Set Min Threshold to 5 for ALL items so demand triggers at 5
        dbManager.addPart(new FrontLaminatedGlass(t, "Corolla 2022 FLG", "Corolla 2022", getRandomStock(), 5, 18000, mockSupplier));
        dbManager.addPart(new FrontGlass(t, "Civic X FG", "Civic X", getRandomStock(), 5, 9500, mockSupplier));
        dbManager.addPart(new RearGlass(t, "Corolla 2022 RG", "Corolla 2022", getRandomStock(), 5, 22000, mockSupplier));
        dbManager.addPart(new DoorGlass(t, "Civic X DG", "Civic X", getRandomStock(), 5, 7500, mockSupplier));
        dbManager.addPart(new FrontBumper(t, "Corolla Bumper", "Corolla 2022", getRandomStock(), 5, 35000, mockSupplier));

        dbManager.addPart(new FrontLaminatedGlass(t, "Civic X FLG", "Civic X", getRandomStock(), 5, 25000, mockSupplier));
        dbManager.addPart(new FrontGlass(t, "Civic X FG Low Stock", "Civic X", getRandomStock(), 5, 12000, mockSupplier));
        dbManager.addPart(new RearGlass(t, "Civic X RG", "Civic X", getRandomStock(), 5, 28000, mockSupplier));
        dbManager.addPart(new DoorGlass(t, "Corolla 2022 DG", "Corolla 2022", getRandomStock(), 5, 9000, mockSupplier));
        dbManager.addPart(new FrontBumper(t, "Civic X Bumper", "Civic X", getRandomStock(), 5, 45000, mockSupplier));

        dbManager.addPart(new FrontLaminatedGlass(t, "Cultus 2019 FLG", "Cultus 2019", getRandomStock(), 5, 12000, mockSupplier));
        dbManager.addPart(new FrontGlass(t, "Cultus 2019 FG", "Cultus 2019", getRandomStock(), 5, 6000, mockSupplier));
        dbManager.addPart(new DoorGlass(t, "Cultus 2019 DG", "Cultus 2019", getRandomStock(), 5, 4500, mockSupplier));
    }

    public void showLoginScreen() {
        LoginView loginView = new LoginView(this);
        Scene scene = new Scene(loginView, 400, 300);
        primaryStage.setTitle("IMS Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showMainDashboard(User user) {
        this.currentUser = user;
        MainLayout mainLayout = new MainLayout(this, user, inventoryManager, demandManager, dashboardManager, masterPartList);
        Scene scene = new Scene(mainLayout, 1100, 750);
        primaryStage.setTitle("Auto Parts IMS - Logged in as: " + user.getUsername());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public boolean authenticate(String username, String password) {
        User authenticatedUser = loginManager.login(username, password);
        if (authenticatedUser != null) { showMainDashboard(authenticatedUser); return true; }
        showAlert("Login Failed", "Invalid Username or Password"); return false;
    }

    public void updatePartStock(Part part, int quantityChange) {
        try {
            // 1. Update Stock
            part.setCurrentStock(part.getCurrentStock() + quantityChange);
            inventoryManager.updateStock(part, quantityChange);

            // 2. FIX: Automatically refresh the demand list.
            // This checks all parts and adds/removes from demand list based on new stock levels.
            demandManager.generateAutoDemands(masterPartList);

            // 3. Refresh Dashboard
            dashboardManager.updateDashboardData();

            // 4. Refresh the Inventory View if it's currently open (via MainLayout reference if needed,
            // but CategorizedInventoryView updates its table on button click anyway).

        } catch (SQLException e) {
            part.setCurrentStock(part.getCurrentStock() - quantityChange);
            showAlert("Database Error", "Failed to update stock: " + e.getMessage());
        }
    }

    public List<Part> getMasterPartList() { return masterPartList; }
    public List<Sale> getMasterSaleList() { return masterSaleList; }
    public List<User> getActiveUsersList() { return loginManager.getActiveUsers(); }
    public String requestPasswordReset(String username) {
        User u = findUser(username);
        if (u == null) return "Username not found.";
        if (u.getRole() != SemesterProject.Login.UserRoles.STAFF) return "Only Staff can reset.";
        loginManager.requestPasswordReset(username);
        return "Request sent.";
    }
    public List<PasswordResetRequest> getPendingPasswordResetRequests() { return loginManager.getPendingRequests(); }
    public User findUser(String username) { return loginManager.findUser(username); }
    public boolean adminApprovePasswordReset(String staff, String pass) {
        if (currentUser instanceof Admin) return loginManager.approvePasswordReset(currentUser.getUsername(), staff, pass);
        return false;
    }
    public boolean removeUser(String username) throws UserNotFoundException { return dbManager.removeUser(username); }
    public void addUser(User newUser) throws Exception { dbManager.addUser(newUser); }
    public boolean updateUserDetails(String old, String newU, String newN) throws UserAlreadyExistsException {
        return dbManager.updateUserDetails(old, newU, newN);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}