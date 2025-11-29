package SemesterProject.Dashboard;

public class DashboardData {

    private int totalCategories;
    private int totalParts;
    private int todaysUsage;
    private int lowStockCount;
    private int supplierAlerts;
    private int demandListCount;

    public DashboardData(int totalCategories, int totalParts, int todaysUsage,
                         int lowStockCount, int supplierAlerts, int demandListCount) {
        this.totalCategories = totalCategories;
        this.totalParts = totalParts;
        this.todaysUsage = todaysUsage;
        this.lowStockCount = lowStockCount;
        this.supplierAlerts = supplierAlerts;
        this.demandListCount = demandListCount;
    }

    public int getTotalCategories() { return totalCategories; }
    public int getTotalParts() { return totalParts; }
    public int getTodaysUsage() { return todaysUsage; }
    public int getLowStockCount() { return lowStockCount; }
    public int getSupplierAlerts() { return supplierAlerts; }
    public int getDemandListCount() { return demandListCount; }
}
