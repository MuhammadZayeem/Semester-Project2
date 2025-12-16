package SemesterProject.GUI;

import SemesterProject.Demand.DemandManager;
import SemesterProject.Part;
import SemesterProject.Sales.Sale;
import SemesterProject.User;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DashboardManager {

    private List<Part> PartList;
    private List<Sale> SaleList;
    private DemandManager demandManager;

    // The View Class
    private MainLayout view;

    public DashboardManager(MainApp app, User user, List<Part> PartList, List<Sale> SaleList, DemandManager demandManager) {
        this.PartList = PartList;
        this.SaleList = SaleList;
        this.demandManager = demandManager;

        // Instantiate the View
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

    public MainLayout getView() {
        updateDashboardData(); // Refresh before showing
        return view;
    }

    public List<Sale> getSalesList() { return SaleList; }
}