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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public class UserManagementView extends VBox {

    private TableView<User> userTable;
    private TableView<PasswordResetRequest> resetTable;
    private MainApp app;
    private Admin adminUser;

    private Label lblStatus;

    public UserManagementView(MainApp app, User currentUser) {
        this.app = app;
        // Verify only Admin can access
        if (!(currentUser instanceof Admin)) {
            this.getChildren().add(new Label("Access Denied. Admin privileges required."));
            return;
        }
        this.adminUser = (Admin) currentUser;

        this.setPadding(new Insets(20));
        this.setSpacing(10);

        Label lblHeader = new Label("Admin Center: User & Access Management");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        lblStatus = new Label("");
        lblStatus.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(
                createUserManagementTab(),
                createResetRequestTab()
        );

        this.getChildren().addAll(lblHeader, lblStatus, tabPane);
        refreshUserTable();
        refreshResetTable();
    }
    private Tab createUserManagementTab() {
        userTable = new TableView<>();

        //--------------------------------------------------------------Columns
        TableColumn<User, String> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserId()));

        TableColumn<User, String> colUser = new TableColumn<>("Username");
        colUser.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        TableColumn<User, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole().name()));

        userTable.getColumns().addAll(colID, colUser, colRole);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox actionBox = createAdminActionBox();
        actionBox.setSpacing(15);
        actionBox.setPadding(new Insets(10, 0, 0, 0));

        VBox content = new VBox(10, userTable, actionBox);
        content.setPadding(new Insets(10));
        VBox.setVgrow(userTable, Priority.ALWAYS);

        Tab userTab = new Tab("Registered Users", content);
        return userTab;
    }

    // -----------------------------------------------------------GUI for Admin Actions
    private VBox createAdminActionBox() {
        VBox container = new VBox(15);
        container.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-padding: 15;");

        Label actionTitle = new Label("User Actions");
        actionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        container.getChildren().add(actionTitle);

        //---------------------------------------------------Add New User
        VBox addUserBox = new VBox(5);
        addUserBox.setStyle("-fx-padding: 10; -fx-background-color: #d1f2eb; -fx-border-radius: 3;");

        Label lblAdd = new Label("1. Add New User");
        lblAdd.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        TextField txtNewUser = new TextField();
        txtNewUser.setPromptText("Username (required)");

        PasswordField txtNewPass = new PasswordField();
        txtNewPass.setPromptText("Password (required)");
        TextField txtNewName = new TextField();
        TextField txtNewContact = new TextField();

        ComboBox<UserRoles> cmbRole = new ComboBox<>(FXCollections.observableArrayList(UserRoles.ADMIN, UserRoles.STAFF));
        cmbRole.setPromptText("Select Role (required)");
        cmbRole.setMaxWidth(Double.MAX_VALUE);

        Button btnAddUser = new Button("Add User");
        btnAddUser.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAddUser.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        grid.addRow(0, new Label("Username:"), txtNewUser);
        grid.addRow(1, new Label("Password:"), txtNewPass);
        grid.addRow(2, new Label("Role:"), cmbRole);

        //----------------------------------------------------Save Action
        btnAddUser.setOnAction(e -> {
            String username = txtNewUser.getText().trim();
            String password = txtNewPass.getText();
            UserRoles role = cmbRole.getValue();
            if (username.isEmpty() || password.isEmpty() || role == null) {
                lblStatus.setText("Error: Username, Password, and Role are required.");
                lblStatus.setStyle("-fx-text-fill: red;");
                return;
            }
            try {
                User newUser;
                String defaultName = username + " User";
                String defaultContact = "N/A";
                if (role == UserRoles.ADMIN) {
                    newUser = new Admin(null, username, password);
                } else {
                    newUser = new Staff(null, username, password, defaultName, defaultContact);
                }
                adminUser.addUser(app, newUser);
                lblStatus.setText("User '" + username + "' added successfully as " + role + ".");
                lblStatus.setStyle("-fx-text-fill: #27ae60;");
                refreshUserTable();

            //--------------------------------------Inputs
                txtNewUser.clear(); txtNewPass.clear(); cmbRole.getSelectionModel().clearSelection();
            } catch (UserAlreadyExistsException ex) {
                lblStatus.setText("Error adding user: " + ex.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            } catch (Exception ex) {
                lblStatus.setText("System Error adding user: " + ex.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        });

        addUserBox.getChildren().addAll(lblAdd, grid, btnAddUser);
        container.getChildren().add(addUserBox);

        //-------------------------------------------------------------------Remove User
        VBox removeBoxContainer = new VBox(5);
        removeBoxContainer.setStyle("-fx-padding: 10 0 10 0; -fx-border-width: 1 0 1 0; -fx-border-color: #bdc3c7;");
        Label lblRemove = new Label("2. Remove User:");
        lblRemove.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        TextField txtRemoveUser = new TextField();
        txtRemoveUser.setPromptText("Username to Remove");

        Button btnRemove = new Button("Remove User");
        btnRemove.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRemove.setMaxWidth(Double.MAX_VALUE);

        btnRemove.setOnAction(e -> {
            try {
                adminUser.removeUser(app, txtRemoveUser.getText().trim());
                lblStatus.setText("User '" + txtRemoveUser.getText().trim() + "' removed successfully.");
                lblStatus.setStyle("-fx-text-fill: #27ae60;");
                refreshUserTable();
                txtRemoveUser.clear();
            } catch (Exception ex) {
                lblStatus.setText("Error removing user: " + ex.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        });

        removeBoxContainer.getChildren().addAll(lblRemove, txtRemoveUser, btnRemove);
        container.getChildren().add(removeBoxContainer);


        //----------------------------------------------------------------Reset Password
        VBox resetBoxContainer = new VBox(5);
        Label lblReset = new Label("3. Direct Password Reset:");
        lblReset.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        TextField txtResetUser = new TextField();
        txtResetUser.setPromptText("Username to Reset");

        PasswordField txtNewPassReset = new PasswordField();
        txtNewPassReset.setPromptText("New Password");

        Button btnReset = new Button("Direct Reset Password");
        btnReset.setStyle("-fx-background-color: #3499db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnReset.setMaxWidth(Double.MAX_VALUE);

        btnReset.setOnAction(e -> {
            String username = txtResetUser.getText().trim();
            String newPass = txtNewPassReset.getText();

            if (username.isEmpty() || newPass.isEmpty()) {
                lblStatus.setText("Error: Username and New Password fields are required.");
                lblStatus.setStyle("-fx-text-fill: red;");
                return;
            }

            adminUser.resetUserPassword(app, username, newPass);

            lblStatus.setText("Password reset initiated for '" + username + "'. Check console for status.");
            lblStatus.setStyle("-fx-text-fill: #27ae60;");
            txtResetUser.clear();
            txtNewPassReset.clear();
        });

        resetBoxContainer.getChildren().addAll(lblReset, txtResetUser, txtNewPassReset, btnReset);
        container.getChildren().add(resetBoxContainer);

        return container;
    }

    //-------------------------------------------------------- TAB 2: PASSWORD RESET REQUESTS
    private Tab createResetRequestTab() {
        resetTable = new TableView<>();

        TableColumn<PasswordResetRequest, String> colReqUser = new TableColumn<>("Staff Username");
        colReqUser.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<PasswordResetRequest, Void> colActions = new TableColumn<>("Action");
        colActions.setPrefWidth(200);
        colActions.setCellFactory(param -> new TableCell<PasswordResetRequest, Void>() {
            private final Button btnApprove = new Button("Approve & Set Password");
            {
                btnApprove.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
                btnApprove.setOnAction(event -> {
                    PasswordResetRequest req = getTableView().getItems().get(getIndex());
                    promptForNewPassword(req.getUsername());
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnApprove);
                }
            }
        });

        resetTable.getColumns().addAll(colReqUser, colActions);
        resetTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnRefreshRequests = new Button("Refresh Requests");
        btnRefreshRequests.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        btnRefreshRequests.setOnAction(e -> refreshResetTable());

        VBox content = new VBox(10, new Label("Pending requests from Staff users:"), resetTable, btnRefreshRequests);
        content.setPadding(new Insets(10));
        VBox.setVgrow(resetTable, Priority.ALWAYS);

        Tab resetTab = new Tab("Password Reset Requests", content);
        return resetTab;
    }

    // HELPER METHODs
    private void promptForNewPassword(String staffUsername) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Approve Password Reset");
        dialog.setHeaderText("Set New Password for User: " + staffUsername);

        ButtonType approveButtonType = new ButtonType("Approve & Reset", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(approveButtonType, ButtonType.CANCEL);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter New Password");

        VBox content = new VBox(10, new Label("Enter a strong new temporary password:"), passwordField);
        dialog.getDialogPane().setContent(content);

        Button approveButton = (Button) dialog.getDialogPane().lookupButton(approveButtonType);
        approveButton.disableProperty().bind(passwordField.textProperty().isEmpty());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == approveButtonType) {
                return passwordField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newPassword -> {
            if (app.adminApprovePasswordReset(staffUsername, newPassword)) {
                lblStatus.setText("Password reset for '" + staffUsername + "' approved and updated!");
                lblStatus.setStyle("-fx-text-fill: #27ae60;");
                refreshResetTable(); // Refresh requests list
                refreshUserTable(); // Optional: Refresh user list if necessary
            } else {
                lblStatus.setText("Error: Failed to approve password reset for '" + staffUsername + "'.");
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        });
    }
    public void refreshUserTable() {
        List<User> activeUsers = app.getActiveUsersList(); // Fetch data from DB
        ObservableList<User> data = FXCollections.observableArrayList(activeUsers);
        userTable.setItems(data);
    }
    public void refreshResetTable() {
        List<PasswordResetRequest> requests = app.getPendingPasswordResetRequests();
        ObservableList<PasswordResetRequest> data = FXCollections.observableArrayList(requests);
        resetTable.setItems(data);
    }
}