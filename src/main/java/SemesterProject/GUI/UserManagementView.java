package SemesterProject.GUI;

import SemesterProject.Login.Admin;
import SemesterProject.Login.PasswordResetRequest;
import SemesterProject.Login.Staff;
import SemesterProject.Login.UserRoles;
import SemesterProject.User;
import SemesterProject.Exception.UserAlreadyExistsException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class UserManagementView extends StackPane {

    private MainApp app;
    private Admin adminUser;

    // Main Container
    private VBox contentArea;

    // Tables
    private TableView<User> userTable;
    private TableView<PasswordResetRequest> resetTable;
    private Label lblStatus;

    public UserManagementView(MainApp app, User currentUser) {
        this.app = app;
        this.setStyle("-fx-background-color: #ecf0f1;"); // Standard Inventory Grey Background
        this.setPadding(new Insets(20));

        if (!(currentUser instanceof Admin)) {
            this.getChildren().add(new Label("Access Denied. Admin privileges required."));
            return;
        }
        this.adminUser = (Admin) currentUser;

        lblStatus = new Label("");
        lblStatus.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblStatus.setStyle("-fx-text-fill: #2980b9;");

        // Main layout container
        contentArea = new VBox(20);
        contentArea.setAlignment(Pos.CENTER);

        this.getChildren().add(contentArea);

        showMainMenu();
    }

    // =================================================================================
    // 1. MAIN MENU (VERTICAL LIST - Like Inventory View)
    // =================================================================================
    private void showMainMenu() {
        contentArea.getChildren().clear();
        lblStatus.setText("");

        Label lblTitle = new Label("Admin Center");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        lblTitle.setStyle("-fx-text-fill: #2c3e50;");

        // --- GRID PANE FOR VERTICAL LIST ---
        GridPane menuGrid = new GridPane();
        menuGrid.setAlignment(Pos.CENTER);
        menuGrid.setVgap(15); // Space between buttons

        // 1. Registered Users
        Button btnList = createWideMenuButton("Registered Users");
        btnList.setOnAction(e -> showRegisteredUsers());

        // 2. Password Reset Requests
        Button btnRequests = createWideMenuButton("Password Reset Requests");
        btnRequests.setOnAction(e -> showResetRequests());

        // 3. Direct Password Reset
        Button btnDirect = createWideMenuButton("Direct Password Reset");
        btnDirect.setOnAction(e -> showDirectResetForm());

        // 4. Remove User
        Button btnRemove = createWideMenuButton("Remove User");
        btnRemove.setOnAction(e -> showRemoveUserForm());

        // 5. Add User
        Button btnAdd = createWideMenuButton("Add New User");
        btnAdd.setOnAction(e -> showAddUserForm());

        // Adding to Grid (Single Column)
        menuGrid.add(btnList, 0, 0);
        menuGrid.add(btnRequests, 0, 1);
        menuGrid.add(btnDirect, 0, 2);
        menuGrid.add(btnRemove, 0, 3);
        menuGrid.add(btnAdd, 0, 4);

        // Center Alignment for buttons in grid
        for (javafx.scene.Node node : menuGrid.getChildren()) {
            GridPane.setHalignment(node, javafx.geometry.HPos.CENTER);
        }

        // --- BACK BUTTON ---
        Button btnBack = new Button("⬅ Back to Dashboard");
/*
        btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-cursor: hand;");
*/
        btnBack.setOnAction(e -> app.showMainDashboard());

        contentArea.getChildren().addAll(lblTitle, menuGrid, btnBack, lblStatus);
    }

    // =================================================================================
    // SCENE 1: REGISTERED USERS
    // =================================================================================
    private void showRegisteredUsers() {
        contentArea.getChildren().clear();

        Label lblTitle = new Label("Registered Users");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, String> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserId()));

        TableColumn<User, String> colUser = new TableColumn<>("Username");
        colUser.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<User, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole().name()));

        userTable.getColumns().addAll(colID, colUser, colRole);
        VBox.setVgrow(userTable, Priority.ALWAYS);

        refreshUserTable();

        contentArea.getChildren().addAll(lblTitle, userTable, createBackButton());
    }

    // =================================================================================
    // SCENE 2: RESET REQUESTS
    // =================================================================================
    private void showResetRequests() {
        contentArea.getChildren().clear();

        Label lblTitle = new Label("Password Reset Requests");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        resetTable = new TableView<>();
        resetTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PasswordResetRequest, String> colReqUser = new TableColumn<>("Staff Username");
        colReqUser.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<PasswordResetRequest, Void> colActions = new TableColumn<>("Action");
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnApprove = new Button("Approve");
            {
                btnApprove.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
                btnApprove.setOnAction(event -> {
                    PasswordResetRequest req = getTableView().getItems().get(getIndex());
                    promptForNewPassword(req.getUsername());
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnApprove);
            }
        });

        resetTable.getColumns().addAll(colReqUser, colActions);
        VBox.setVgrow(resetTable, Priority.ALWAYS);

        Button btnRefresh = new Button("Refresh List");
        btnRefresh.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        btnRefresh.setOnAction(e -> refreshResetTable());

        refreshResetTable();

        contentArea.getChildren().addAll(lblTitle, btnRefresh, resetTable, lblStatus, createBackButton());
    }

    // =================================================================================
    // SCENE 3: DIRECT RESET FORM
    // =================================================================================
    private void showDirectResetForm() {
        contentArea.getChildren().clear();

        GridPane formGrid = createFormGrid();

        Label lblTitle = new Label("Direct Password Reset");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        TextField txtUser = new TextField(); txtUser.setPromptText("Target Username");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("New Password");

        Button btnReset = new Button("Reset Password");
        btnReset.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnReset.setMaxWidth(Double.MAX_VALUE);

        btnReset.setOnAction(e -> {
            if (txtUser.getText().isEmpty() || txtPass.getText().isEmpty()) {
                lblStatus.setText("All fields required.");
                lblStatus.setStyle("-fx-text-fill: red;");
                return;
            }
            adminUser.resetUserPassword(app, txtUser.getText(), txtPass.getText());
            lblStatus.setText("Password reset for: " + txtUser.getText());
            lblStatus.setStyle("-fx-text-fill: green;");
            txtUser.clear(); txtPass.clear();
        });

        formGrid.add(new Label("Username:"), 0, 0);
        formGrid.add(txtUser, 1, 0);
        formGrid.add(new Label("New Password:"), 0, 1);
        formGrid.add(txtPass, 1, 1);
        formGrid.add(btnReset, 1, 2);

        VBox container = wrapFormInBox(lblTitle, formGrid);
        contentArea.getChildren().addAll(container, lblStatus, createBackButton());
    }

    // =================================================================================
    // SCENE 4: REMOVE USER FORM
    // =================================================================================
    private void showRemoveUserForm() {
        contentArea.getChildren().clear();

        GridPane formGrid = createFormGrid();

        Label lblTitle = new Label("Remove User");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        TextField txtUser = new TextField(); txtUser.setPromptText("Username to Remove");

        Button btnRemove = new Button("Delete User");
        btnRemove.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnRemove.setMaxWidth(Double.MAX_VALUE);

        btnRemove.setOnAction(e -> {
            try {
                adminUser.removeUser(app, txtUser.getText().trim());
                lblStatus.setText("User removed: " + txtUser.getText());
                lblStatus.setStyle("-fx-text-fill: green;");
                txtUser.clear();
            } catch (Exception ex) {
                lblStatus.setText("Error: " + ex.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        });

        formGrid.add(new Label("Username:"), 0, 0);
        formGrid.add(txtUser, 1, 0);
        formGrid.add(btnRemove, 1, 1);

        VBox container = wrapFormInBox(lblTitle, formGrid);
        contentArea.getChildren().addAll(container, lblStatus, createBackButton());
    }

    // =================================================================================
    // SCENE 5: ADD USER FORM
    // =================================================================================
    private void showAddUserForm() {
        contentArea.getChildren().clear();

        GridPane formGrid = createFormGrid();

        Label lblTitle = new Label("Add New User");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        TextField txtUser = new TextField(); txtUser.setPromptText("Username");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("Password");
        ComboBox<UserRoles> cmbRole = new ComboBox<>(FXCollections.observableArrayList(UserRoles.ADMIN, UserRoles.STAFF));
        cmbRole.setPromptText("Select Role");
        cmbRole.setMaxWidth(Double.MAX_VALUE);

        Button btnAdd = new Button("Create User");
        btnAdd.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnAdd.setMaxWidth(Double.MAX_VALUE);

        btnAdd.setOnAction(e -> {
            if (txtUser.getText().isEmpty() || txtPass.getText().isEmpty() || cmbRole.getValue() == null) {
                lblStatus.setText("All fields required.");
                lblStatus.setStyle("-fx-text-fill: red;");
                return;
            }
            try {
                User newUser;
                if (cmbRole.getValue() == UserRoles.ADMIN) {
                    newUser = new Admin(null, txtUser.getText(), txtPass.getText());
                } else {
                    newUser = new Staff(null, txtUser.getText(), txtPass.getText());
                }
                adminUser.addUser(app, newUser);
                lblStatus.setText("User created successfully!");
                lblStatus.setStyle("-fx-text-fill: green;");
                txtUser.clear(); txtPass.clear(); cmbRole.getSelectionModel().clearSelection();
            } catch (UserAlreadyExistsException ex) {
                lblStatus.setText(ex.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            } catch (Exception ex) {
                lblStatus.setText("System Error: " + ex.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        });

        formGrid.add(new Label("Username:"), 0, 0);
        formGrid.add(txtUser, 1, 0);
        formGrid.add(new Label("Password:"), 0, 1);
        formGrid.add(txtPass, 1, 1);
        formGrid.add(new Label("Role:"), 0, 2);
        formGrid.add(cmbRole, 1, 2);
        formGrid.add(btnAdd, 1, 3);

        VBox container = wrapFormInBox(lblTitle, formGrid);
        contentArea.getChildren().addAll(container, lblStatus, createBackButton());
    }

    // =================================================================================
    // HELPERS & STYLING (MATCHING INVENTORY VIEW)
    // =================================================================================

    private Button createWideMenuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(300, 50); // Wide like Inventory list
        btn.setStyle(
                "-fx-background-color: #ecf0f1;" + // Light grey like inventory buttons
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        // Hover
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #d5dbdb; -fx-text-fill: #2c3e50; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;"));
        return btn;
    }

    private Button createBackButton() {
        Button btn = new Button("⬅ Back");
        btn.setStyle(
                "-fx-background-color: #bdc3c7;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        btn.setPadding(new Insets(8, 20, 8, 20));
        btn.setOnAction(e -> showMainMenu());
        VBox.setMargin(btn, new Insets(20, 0, 0, 0));
        return btn;
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private VBox wrapFormInBox(Label title, GridPane grid) {
        VBox box = new VBox(20);
        box.setMaxWidth(450);
        box.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(title, grid);
        return box;
    }

    public void refreshUserTable() {
        if (userTable != null) {
            List<User> activeUsers = app.getActiveUsersList();
            ObservableList<User> data = FXCollections.observableArrayList(activeUsers);
            userTable.setItems(data);
        }
    }

    public void refreshResetTable() {
        if (resetTable != null) {
            List<PasswordResetRequest> requests = app.getPendingPasswordResetRequests();
            ObservableList<PasswordResetRequest> data = FXCollections.observableArrayList(requests);
            resetTable.setItems(data);
        }
    }

    private void promptForNewPassword(String staffUsername) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Approve Reset");
        dialog.setHeaderText("Set new password for: " + staffUsername);
        dialog.setContentText("New Password:");
        dialog.showAndWait().ifPresent(pass -> {
            if (!pass.isEmpty()) {
                if (app.adminApprovePasswordReset(staffUsername, pass)) {
                    lblStatus.setText("Password updated for " + staffUsername);
                    lblStatus.setStyle("-fx-text-fill: green;");
                    refreshResetTable();
                } else {
                    lblStatus.setText("Update failed.");
                    lblStatus.setStyle("-fx-text-fill: red;");
                }
            }
        });
    }
}