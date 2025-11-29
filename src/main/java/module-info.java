module com.example.demo { // Keep your project name here (whatever was there before)
    requires javafx.controls;
    requires javafx.fxml;

    // Give JavaFX permission to access your GUI folder
    exports SemesterProject.GUI;

    // Allow access to the rest of your project
    exports SemesterProject;
    exports SemesterProject.Body;
    exports SemesterProject.Dashboard;
    exports SemesterProject.Demand;
    exports SemesterProject.InventoryManagment;
    exports SemesterProject.Login;
    exports SemesterProject.Sales;
    exports SemesterProject.Supplier;
}