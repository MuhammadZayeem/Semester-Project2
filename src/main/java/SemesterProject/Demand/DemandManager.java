package SemesterProject.Demand;

import java.util.*;
import SemesterProject.Part;
import SemesterProject.Supplier.Supplier;
public class DemandManager {

    private ArrayList<DemandItem> demandList = new ArrayList<>();

    // Add demand automatically or manually
    private void addDemandInternal(Part part, int qty, boolean auto) {
        demandList.add(new DemandItem(part, qty, auto));
    }

    // ------------------------------------
    // ✔ MANUAL DEMAND
    // ------------------------------------
    public void createManualDemand(Part part, int qty) {
        addDemandInternal(part, qty, false);
        System.out.println("Manual demand added for: " + part.getName());
    }

    // ------------------------------------
    // ✔ AUTO-DEMAND (when quantity < threshold)
    // ------------------------------------
    public void generateAutoDemand(List<Part> parts) {
        for (Part p : parts) {
            if (p.getQuantity() <= p.getThreshold()) {

                int need = (p.getThreshold() * 2) - p.getQuantity();
                if (need < 1) need = 1;

                addDemandInternal(p, need, true);

                System.out.println("AUTO demand created for: " + p.getName() +
                        " (Need: " + need + ")");
            }
        }
    }

    // ------------------------------------
    // ✔ SHOW ALL DEMANDS
    // ------------------------------------
    public void showAllDemands() {
        if (demandList.isEmpty()) {
            System.out.println("No demand items.");
            return;
        }

        System.out.println("\n======== DEMAND LIST ========");
        for (DemandItem item : demandList) {
            System.out.println(item);
        }
    }

    // ------------------------------------
    // ✔ GROUP BY SUPPLIER
    // ------------------------------------
    public void showDemandBySupplier() {

        HashMap<Supplier, ArrayList<DemandItem>> supplierMap = new HashMap<>();

        for (DemandItem item : demandList) {
            Supplier s = item.getPart().getSupplier();
            supplierMap.putIfAbsent(s, new ArrayList<>());
            supplierMap.get(s).add(item);
        }

        System.out.println("\n======== SUPPLIER-WISE DEMAND ========");

        for (Supplier sup : supplierMap.keySet()) {
            System.out.println("\nSupplier: " + sup.getName());

            for (DemandItem item : supplierMap.get(sup)) {
                System.out.println(" - " + item);
            }
        }
    }

    // ------------------------------------
    // ✔ TOTAL COST CALCULATION
    // ------------------------------------
    public double calculateTotalCost() {
        double total = 0;

        for (DemandItem item : demandList) {
            total += item.getTotalCost();
        }

        return total;
    }

    // ------------------------------------
    // ✔ PURCHASE ORDER (PO) GENERATION
    // ------------------------------------
    public void generatePOforSupplier(Supplier supplier) {

        System.out.println("\n========= PURCHASE ORDER =========");
        System.out.println("Supplier: " + supplier.getName());
        System.out.println("----------------------------------");

        double total = 0;

        for (DemandItem item : demandList) {
            if (item.getPart().getSupplier().equals(supplier)) {
                System.out.println(item.getPart().getName() +
                        " | Qty: " + item.getRequiredQuantity() +
                        " | Cost: " + item.getTotalCost());
                total += item.getTotalCost();
            }
        }

        System.out.println("----------------------------------");
        System.out.println("TOTAL COST: " + total);
    }

    public int getDemandCount() {
        return demandList.size();
    }

}
