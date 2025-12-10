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
        // Initialize inventory list
        this.inventory = new ArrayList<>();
        this.dbManager = dbManager;
    }

    // Default constructor (should ideally not be used in DB mode)
    public InventoryManager() {
        this.inventory = new ArrayList<>();
        this.dbManager = null;
    }

   /* public void addPart(Part part) {
        if (inventory.stream().anyMatch(p -> p.getName().equalsIgnoreCase(part.getName()))) {
            return;
        }
        inventory.add(part);

        if (dbManager != null) {
            try {
                dbManager.addPart(part);
            } catch (SQLException e) {
                System.err.println("Database error adding part: " + e.getMessage());
            }
        }
    }*/

    public void updateStock(Part part, int quantityChange) throws SQLException {
        // NOTE: The stock level in the local 'part' object is usually updated by the caller (MainApp)
        // before calling this method to ensure synchronization.

        // Persist change to database if available
        if (dbManager != null) {
            dbManager.updatePart(part);
            System.out.println("Stock updated in DB for: " + part.getName());
        } else {
            // Throw exception or handle error if manager isn't linked to DB
            throw new IllegalStateException("InventoryManager not connected to DatabaseManager.");
        }
    }

    public Part getPartByName(String name) {
        return inventory.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * FIX: Re-implementing the missing method to satisfy dependencies (e.g., MainApp).
     * This returns the internal, database-loaded list of parts.
     */
    public List<Part> getInventory() {
        return inventory;
    }
}