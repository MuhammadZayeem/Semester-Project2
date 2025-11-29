package SemesterProject.GUI;

import SemesterProject.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;

public class UserManagementView extends VBox {

    private TableView<User> table;
    private User[] userArray; // Reference to the main array

    public UserManagementView(User[] userArray) {
        this.userArray = userArray;
        this.setPadding(new Insets(20));
        this.setSpacing(10);

        Label lblHeader = new Label("User Management (Admin Access)");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        table = new TableView<>();

        // Columns
        TableColumn<User, String> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserId()));

        TableColumn<User, String> colUser = new TableColumn<>("Username");
        colUser.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<User, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));

        TableColumn<User, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClass().getSimpleName()));

        TableColumn<User, String> colLogin = new TableColumn<>("Last Login");
        colLogin.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().ShowLastLogin()));
        colLogin.setPrefWidth(200);

        table.getColumns().addAll(colID, colUser, colName, colRole, colLogin);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        this.getChildren().addAll(lblHeader, table);
    }

    public void refreshTable() {
        // Convert the fixed array User[] to a List for the TableView
        ArrayList<User> activeUsers = new ArrayList<>();
        for (User u : userArray) {
            if (u != null) {
                activeUsers.add(u);
            }
        }
        ObservableList<User> data = FXCollections.observableArrayList(activeUsers);
        table.setItems(data);
    }
}