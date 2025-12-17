package SemesterProject.GUI;

import SemesterProject.Body.*;
import SemesterProject.Exception.UserCreationException;
import SemesterProject.Login.Admin;
import SemesterProject.Login.LoginManager;
import SemesterProject.Data;
import SemesterProject.Part;
import SemesterProject.Sales.Sale;
import SemesterProject.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import SemesterProject.Demand.DemandManager;
import SemesterProject.Login.PasswordResetRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainApp extends Application {

    private Stage primaryStage;
    private User currentUser;

    // Managers
    private DemandManager demandManager;
    private DashboardManager dashboardManager;
    private LoginManager loginManager;
    private Data dbManager;

    // Lists
    private List<Part> masterPartList = new ArrayList<>();
    private List<Sale> masterSaleList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws UserCreationException {
        this.primaryStage = primaryStage;
        initializeData();
        showLoginScreen();
    }

    private void initializeData() throws UserCreationException {
        dbManager = new Data();
        loginManager = new LoginManager(dbManager);
        demandManager = new DemandManager();
        loadAndPopulateData();
    }

    private void loadAndPopulateData() {
        masterPartList.clear();
        masterPartList.addAll(dbManager.getAllParts());
        if (masterPartList.isEmpty()) {
            try {
                loadMockInventoryAndSaveToData();
                masterPartList.clear();
                masterPartList.addAll(dbManager.getAllParts());
            } catch (Exception e) {
                System.err.println("Error loading mock data: " + e.getMessage());
            }
        }
        masterSaleList.clear();
        masterSaleList.addAll(dbManager.getAllSales());

        // Initial Demand Check
        demandManager.addDemands(masterPartList);
    }

    private int getRandomStock() {

        return new Random().nextInt(46) + 5;
    }

    private void loadMockInventoryAndSaveToData() throws Exception {
        String t = null;
        dbManager.addPart(new FrontLaminatedGlass(t, "Corolla 2000 FLG", "Corolla 2002", getRandomStock(), 5, 18000));
        dbManager.addPart(new FrontGlass(t, "Civic X FG", "Civic X", getRandomStock(), 5, 9500));
        dbManager.addPart(new RearGlass(t, "Corolla 2022 RG", "Corolla 2022", getRandomStock(), 5, 22000));
        dbManager.addPart(new DoorGlass(t, "Civic X DG", "Civic X", getRandomStock(), 5, 7500));
        dbManager.addPart(new FrontBumper(t, "Corolla Bumper", "Corolla 2022", getRandomStock(), 5, 35000));
    }

    public void showLoginScreen() {
        LoginView loginView = new LoginView(this);
        Scene scene = new Scene(loginView.getView(), 400, 300);
        primaryStage.setTitle("IMS Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public boolean authenticate(String username, String password) {
        User authenticatedUser = loginManager.login(username, password);
        if (authenticatedUser != null) {
            this.currentUser = authenticatedUser;
            dashboardManager = new DashboardManager(this, currentUser, masterPartList, masterSaleList, demandManager);
            showMainDashboard();
            return true;
        }
        return false;
    }


    public void showMainDashboard() {
        dashboardManager.updateDashboardData();
        Scene scene = new Scene(dashboardManager.getView(), 1100, 750);
        primaryStage.setTitle("IMS Dashboard - " + currentUser.getUsername());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public void showInventory() {
        CategorizedInventoryView view = new CategorizedInventoryView(masterPartList, this);
        primaryStage.getScene().setRoot(view);
    }

    public void showSalesHistory() {
        SalesView view = new SalesView(this, masterSaleList);
        primaryStage.getScene().setRoot(view.getView());
    }

    public void showDemandList() {
        demandManager.addDemands(masterPartList);
        DemandView view = new DemandView(this, demandManager);
        primaryStage.getScene().setRoot(view.getView());
    }

/*    public void showUserManagement() {
        UserManagementView view = new UserManagementView(this, currentUser);
        primaryStage.getScene().setRoot(view);
    }*/


    public void recordSale(Part part) {
        try {
            int newStock = part.getCurrentStock() - 1;
            if (newStock < 0) return;

            String userId = currentUser != null ? currentUser.getUserId() : "U00";
            Sale newSale = new Sale(part.getName(), 1, part.getUnitPrice());

            dbManager.addSale(newSale);
            masterSaleList.add(newSale);

            part.setCurrentStock(newStock);

            demandManager.addDemands(masterPartList);
            dashboardManager.updateDashboardData();

        } catch (Exception e) {
            System.err.println("Error recording sale: " + e.getMessage());
        }
    }

    // Getters & Delegation
    public List<User> getActiveUsersList() { return loginManager.getActiveUsers(); }

    public String requestPasswordReset(String username) {
        User u = findUser(username);
        if (u == null) return "User not found.";
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

    public static void main(String[] args)
    { launch(args); }
}