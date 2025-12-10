package SemesterProject.GUI;
import javafx.scene.layout.BorderPane;

public class DashboardView extends BorderPane {
    public DashboardView(DashboardManager manager) {
        manager.updateDashboardData();
        this.setCenter(manager.getView());
    }
}