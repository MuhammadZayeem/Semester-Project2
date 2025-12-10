package SemesterProject.GUI;

import javafx.scene.layout.BorderPane;

public class DashboardView extends BorderPane {

    public DashboardView(DashboardManager manager) {
        // Just delegate to the manager's new view method
        manager.updateDashboardData();
        this.setCenter(manager.getView());
    }
}