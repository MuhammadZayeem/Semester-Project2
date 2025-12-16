package SemesterProject.GUI;

import SemesterProject.Demand.DemandManager;
import SemesterProject.Part;
import SemesterProject.Sales.Sale;
import SemesterProject.User;
import javafx.scene.layout.GridPane; // Import GridPane
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DashboardManager {

    private MainApp app;
    private User currentUser;
    private List<Part> PartList;
    private List<Sale> SaleList;
    private DemandManager demandManager;

    // The View Wrapper Class
    private MainLayout view;

    public DashboardManager(MainApp app, User user, List<Part> PartList, List<Sale> SaleList, DemandManager demandManager) {
        this.app = app;
        this.currentUser = user;
        this.PartList = PartList;
        this.SaleList = SaleList;
        this.demandManager = demandManager;

        // Instantiate the Layout Wrapper
        this.view = new MainLayout(app, user);
    }

    public void updateDashboardData() {
        // 1. Calculate Total Stock
        int totalStock = 0;
        for (Part p : PartList) {
            totalStock += p.getCurrentStock();
        }

        // 2. Calculate Low Stock
        int lowStock = 0;
        for (Part p : PartList) {
            if (p.getCurrentStock() <= p.getMinThreshold()) {
                lowStock++;
            }
        }

        // 3. Calculate Sold Today
        int soldToday = 0;
        LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        for (Sale sale : SaleList) {
            if (sale.getSaleDateTime() != null && sale.getSaleDateTime().isAfter(today)) {
                soldToday += sale.getQuantitySold();
            }
        }

        // 4. Get Demand Count
        int demandCount = demandManager.getDemandList().size();

        // 5. PUSH Data to the View
        view.updateMetrics(totalStock, lowStock, soldToday, demandCount);
    }

    // --- FIX: Return the actual GridPane, NOT the MainLayout object ---
    public GridPane getView() {
        updateDashboardData(); // Refresh data before showing
        return view.getView(); // Calls MainLayout.getView() which returns the GridPane
    }

    public List<Sale> getSalesList() { return SaleList; }
}