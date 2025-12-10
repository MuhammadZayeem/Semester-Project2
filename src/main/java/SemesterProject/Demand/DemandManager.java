package SemesterProject.Demand;
import SemesterProject.Part;
import java.util.ArrayList;
import java.util.List;

public class DemandManager {

    private  ArrayList<DemandItem> demandList;

    public DemandManager() {
        this.demandList = new ArrayList<>();
    }

    public void generateAutoDemands(List<Part> Inventory) {
        demandList.clear();
        for (Part p : Inventory) {
            if (p.getCurrentStock() <= p.getMinThreshold()) {
                int orderQty = (p.getMinThreshold() - p.getCurrentStock()) + 5;
                demandList.add(new DemandItem(p, orderQty, true));
            }
        }
    }

    public ArrayList<DemandItem> getDemandList() {
        return demandList;
    }
}