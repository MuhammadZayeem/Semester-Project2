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

    // Default constructor
   /* public InventoryManager() {
        this.inventory = new ArrayList<>();
        this.dbManager = null;
    }*/


    public List<Part> getInventory() {
        return inventory;
    }
}