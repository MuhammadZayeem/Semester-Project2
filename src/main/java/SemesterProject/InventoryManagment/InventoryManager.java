package SemesterProject.InventoryManagment;

import SemesterProject.Part;
import SemesterProject.Data;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    private List<Part> inventory;
    private Data dbManager;

    public InventoryManager(Data dbManager) {
        this.inventory = new ArrayList<>();
        this.dbManager = dbManager;
    }

/*    public void updateStock(Part part, int quantityChange) {
        if (dbManager != null) {
            dbManager.updatePart(part);
        } else {
            System.err.println("InventoryManager not connected to DBManager.");
        }
    }*/

    public List<Part> getInventory() {
        return inventory;
    }
}