package SemesterProject.Demand;

import SemesterProject.Part;
import java.util.ArrayList;
import java.util.List;

public class DemandManager {

    // The View uses Reflection to read this specific variable name: "demandList"
    private ArrayList<DemandItem> demandList;

    public DemandManager() {
        this.demandList = new ArrayList<>();
    }

    // THIS IS THE MISSING METHOD CAUSING THE FIRST ERROR
    public void generateAutoDemands(List<Part> masterInventory) {
        demandList.clear(); // Clear old list

        for (Part p : masterInventory) {
            // Check if quantity is at or below threshold
            if (p.getQuantity() <= p.getThreshold()) {
                // Logic: Order enough to reach threshold + 10 buffer
                int orderQty = (p.getThreshold() - p.getQuantity()) + 10;

                // Add to list
                demandList.add(new DemandItem(p, orderQty, true));
            }
        }
    }

    public ArrayList<DemandItem> getDemandList() {
        return demandList;
    }

    public int getDemandCount() {
        return demandList.size();
    }
}