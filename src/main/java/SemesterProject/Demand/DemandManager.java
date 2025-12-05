package SemesterProject.Demand;

import SemesterProject.Part;
import java.util.ArrayList;
import java.util.List;

public class DemandManager {

    private ArrayList<DemandItem> demandList;

    public DemandManager() {
        this.demandList = new ArrayList<>();
    }

    public void generateAutoDemands(List<Part> masterInventory) {
        demandList.clear();

        for (Part p : masterInventory) {
            // FIX: Updated method names to match the new Part class structure
            if (p.getCurrentStock() <= p.getMinThreshold()) {

                // Logic: Order enough to reach threshold + 10 buffer
                int orderQty = (p.getMinThreshold() - p.getCurrentStock()) + 10;

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