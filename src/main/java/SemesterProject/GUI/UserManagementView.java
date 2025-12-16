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
        this.setStyle("-fx-background-color: #f4f6f7;"); // Light grey background
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
    // 1. MAIN MENU (GRID PANE)
    // =================================================================================
    private void showMainMenu() {
        contentArea.getChildren().clear();
        lblStatus.setText("");

        Label lblTitle = new Label("Admin Center");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        lblTitle.setStyle("-fx-text-fill: #2c3e50;");

        // --- GRID PANE FOR MENU BUTTONS ---
        GridPane menuGrid = new GridPane();
        menuGrid.setAlignment(Pos.CENTER);
        menuGrid.setHgap(20);
        menuGrid.setVgap(20);

        // 1. Registered Users
        Button btnList = createMenuButton("Registered Users", "#ecf0f1", "#2c3e50");
        btnList.setOnAction(e -> showRegisteredUsers());

        // 2. Password Reset Requests
        Button btnRequests = createMenuButton("Reset Requests", "#ecf0f1", "#2c3e50");
        btnRequests.setOnAction(e -> showResetRequests());

        // 3. Direct Password Reset
        Button btnDirect = createMenuButton("Direct Reset", "#ecf0f1", "#2c3e50");
        btnDirect.setOnAction(e -> showDirectResetForm());

        // 4. Remove User
        Button btnRemove = createMenuButton("Remove User", "#ecf0f1", "#2c3e50"); // Red
        btnRemove.setOnAction(e -> showRemoveUserForm());

        // 5. Add User
        Button btnAdd = createMenuButton("Add New User", "#2ecc71", "white"); // Green
        btnAdd.setOnAction(e -> showAddUserForm());

        // Adding to Grid (Col, Row)
        // Row 0
        menuGrid.add(btnList, 0, 0);
        menuGrid.add(btnRequests, 1, 0);

        // Row 1
        menuGrid.add(btnDirect, 0, 1);
        menuGrid.add(btnRemove, 1, 1);

        // Row 2 (Spanning 2 columns for "Add User")
        menuGrid.add(btnAdd, 0, 2, 2, 1);
        btnAdd.setMaxWidth(Double.MAX_VALUE); // Stretch to fill

        // --- ADDED: Back to Dashboard Button ---
        Button btnBack = new Button("⬅ Back to Dashboard");
        btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-cursor: hand;");
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
        btnRefresh.setOnAction(e -> refreshResetTable());

        refreshResetTable();

        contentArea.getChildren().addAll(lblTitle, btnRefresh, resetTable, lblStatus, createBackButton());
    }

    // =================================================================================
    // SCENE 3: DIRECT RESET FORM (GRID PANE)
    // =================================================================================
    private void showDirectResetForm() {
        contentArea.getChildren().clear();

        GridPane formGrid = createFormGrid();

        Label lblTitle = new Label("Direct Password Reset");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        TextField txtUser = new TextField(); txtUser.setPromptText("Target Username");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("New Password");

        Button btnReset = new Button("Reset Password");
        btnReset.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
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

        // Add to Grid
        formGrid.add(new Label("Username:"), 0, 0);
        formGrid.add(txtUser, 1, 0);
        formGrid.add(new Label("New Password:"), 0, 1);
        formGrid.add(txtPass, 1, 1);
        formGrid.add(btnReset, 1, 2);

        VBox container = wrapFormInBox(lblTitle, formGrid);
        contentArea.getChildren().addAll(container, lblStatus, createBackButton());
    }

    // =================================================================================
    // SCENE 4: REMOVE USER FORM (GRID PANE)
    // =================================================================================
    private void showRemoveUserForm() {
        contentArea.getChildren().clear();

        GridPane formGrid = createFormGrid();

        Label lblTitle = new Label("Remove User");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        TextField txtUser = new TextField(); txtUser.setPromptText("Username to Remove");

        Button btnRemove = new Button("Delete User");
        btnRemove.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
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

        // Add to Grid
        formGrid.add(new Label("Username:"), 0, 0);
        formGrid.add(txtUser, 1, 0);
        formGrid.add(btnRemove, 1, 1);

        VBox container = wrapFormInBox(lblTitle, formGrid);
        contentArea.getChildren().addAll(container, lblStatus, createBackButton());
    }

    // =================================================================================
    // SCENE 5: ADD USER FORM (GRID PANE)
    // =================================================================================
    private void showAddUserForm() {
        contentArea.getChildren().clear();

        GridPane formGrid = createFormGrid();

        Label lblTitle = new Label("Add New User");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        TextField txtUser = new TextField(); txtUser.setPromptText("Username");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("Password");
        ComboBox<UserRoles> cmbRole = new ComboBox<>(FXCollections.observableArrayList(UserRoles.ADMIN, UserRoles.STAFF));
        cmbRole.setPromptText("Select Role");
        cmbRole.setMaxWidth(Double.MAX_VALUE);

        Button btnAdd = new Button("Create User");
        btnAdd.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
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

        // Add to Grid (Label -> Field alignment)
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
    // HELPERS & STYLING
    // =================================================================================

    // Helper to create the standard 2-column Grid for forms
    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    // Helper to wrap the title and the grid inside a pretty white box
    private VBox wrapFormInBox(Label title, GridPane grid) {
        VBox box = new VBox(20);
        box.setMaxWidth(450);
        box.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(title, grid);
        return box;
    }

    private Button createMenuButton(String text, String bgColor, String textColor) {
        Button btn = new Button(text);
        btn.setPrefSize(200, 80); // Rectangular Tile shape
        btn.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: " + textColor + ";" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #d5dbdb; -fx-text-fill: " + textColor + "; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;"));
        return btn;
    }

    private Button createBackButton() {
        Button btn = new Button("⬅ Back");
        btn.setStyle(
                "-fx-background-color: #e0e0e0;" +
                        "-fx-text-fill: #333;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-color: #ccc;" +
                        "-fx-border-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        btn.setPadding(new Insets(5, 20, 5, 20));
        btn.setOnAction(e -> showMainMenu());

        VBox.setMargin(btn, new Insets(20, 0, 0, 0));
        return btn;
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