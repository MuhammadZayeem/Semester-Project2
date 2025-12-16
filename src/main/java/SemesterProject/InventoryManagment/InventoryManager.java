package SemesterProject.InventoryManagment;

import SemesterProject.Part;
import SemesterProject.DatabaseManager;
import java.util.ArrayList;
import java.util.List;
// SQL Import removed

public class InventoryManager {

    private List<Part> inventory;
    private DatabaseManager dbManager;

    public InventoryManager(DatabaseManager dbManager) {
        this.inventory = new ArrayList<>();
        this.dbManager = dbManager;
    }

    // Default constructor
    public InventoryManager() {
        this.inventory = new ArrayList<>();
        this.dbManager = null;
    }

    // REMOVED "throws SQLException"
    public void updateStock(Part part, int quantityChange) {
        if (dbManager != null) {
            dbManager.updatePart(part);
            // Stock updated in Memory
        } else {
            // Handle disconnected state if necessary
            System.err.println("InventoryManager not connected to DBManager.");
        }
    }

    public List<Part> getInventory() {
        return inventory;
    }
}