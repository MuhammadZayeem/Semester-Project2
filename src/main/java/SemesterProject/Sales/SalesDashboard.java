package SemesterProject.Sales;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SalesDashboard extends Application {

    private ObservableList<Sale> salesData = FXCollections.observableArrayList();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        generateMockData();

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    // ----------------------------------------------------------------Mock Data
    private void generateMockData() {
        String p1Name = "Civic Front Glass";
        double p1Price = 5000.0;
        int q1 = 2;
        Sale s1 = new Sale(p1Name, q1, p1Price * q1);

        String p2Name = "Corolla Front Laminated";
        double p2Price = 12000.0;
        int q2 = 1;
        Sale s2 = new Sale(p2Name, q2, p2Price * q2);

        salesData.addAll(s1, s2);
    }
}