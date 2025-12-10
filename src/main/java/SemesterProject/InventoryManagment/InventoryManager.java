package SemesterProject.InventoryManagment;

import SemesterProject.Part;
import SemesterProject.DatabaseManager;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class InventoryManager {

    private List<Part> inventory;
    private DatabaseManager dbManager;

    public InventoryManager(DatabaseManager dbManager) {
        this.inventory = new ArrayList<>();
        this.dbManager = dbManager;
    }

    //------------------------------------------------Default constructor
    public InventoryManager() {
        this.inventory = new ArrayList<>();
        this.dbManager = null;
    }

    public void updateStock(Part part, int quantityChange) throws SQLException {
        if (dbManager != null) {
            dbManager.updatePart(part);
            //Stock updated in DB for:part.getName()
        } else {
            //throw new IllegalStateException("InventoryManager not connected to DatabaseManager.");
        }
    }
    public List<Part> getInventory() {
        return inventory;
    }
}