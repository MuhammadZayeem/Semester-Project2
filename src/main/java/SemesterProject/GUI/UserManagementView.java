package SemesterProject.GUI;

// FIX: Ensure all necessary imports are present for the database architecture
import SemesterProject.Login.Admin;
import SemesterProject.Login.PasswordResetRequest;
import SemesterProject.Login.Staff;
import SemesterProject.Login.UserRoles;
import SemesterProject.User;
import SemesterProject.Exception.UserAlreadyExistsException;
import SemesterProject.Exception.UserNotFoundException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException; // Although not directly used, good for related DB exceptions

public class UserManagementView extends VBox {

    private TableView<User> userTable;
    private TableView<PasswordResetRequest> resetTable;
    private MainApp app;
    private Admin adminUser;

    // Status label for feedback
    private Label lblStatus;

    // FIX: This constructor signature matches the one required by MainLayout.java
    public UserManagementView(MainApp app, User currentUser) {
        this.app = app;
        // FIX: Ensure Admin class is correctly imported and cast is valid (checked by compiler if Admin.java is correct)
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

    // ===============================================
    // TAB 1: USER MANAGEMENT
    // ===============================================
    private Tab createUserManagementTab() {
        userTable = new TableView<>();

        // Columns Setup
        TableColumn<User, String> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserId()));

        TableColumn<User, String> colUser = new TableColumn<>("Username");
        colUser.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<User, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));

        TableColumn<User, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole().name()));

        TableColumn<User, String> colLogin = new TableColumn<>("Last Login");
        colLogin.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().ShowLastLogin()));
        colLogin.setPrefWidth(150);

        userTable.getColumns().addAll(colID, colUser, colName, colRole, colLogin);
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

    // UI for Admin Actions - CLARIFIED LAYOUT
    private VBox createAdminActionBox() {
        VBox container = new VBox(15);
        container.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-padding: 15;");

        Label actionTitle = new Label("User Actions");
        actionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        container.getChildren().add(actionTitle);

        // --- 1. Add New User Section ---
        VBox addUserBox = new VBox(5);
        addUserBox.setStyle("-fx-padding: 10; -fx-background-color: #d1f2eb; -fx-border-radius: 3;");

        Label lblAdd = new Label("1. Add New User");
        lblAdd.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        TextField txtNewUser = new TextField();
        txtNewUser.setPromptText("Username");

        PasswordField txtNewPass = new PasswordField();
        txtNewPass.setPromptText("Password");

        TextField txtNewName = new TextField();
        txtNewName.setPromptText("Full Name");

        TextField txtNewContact = new TextField();
        txtNewContact.setPromptText("Contact Number (e.g., 0000)");

        ComboBox<UserRoles> cmbRole = new ComboBox<>(FXCollections.observableArrayList(UserRoles.ADMIN, UserRoles.STAFF));
        cmbRole.setPromptText("Select Role");
        cmbRole.setMaxWidth(Double.MAX_VALUE);

        Button btnAddUser = new Button("Add User");
        btnAddUser.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAddUser.setMaxWidth(Double.MAX_VALUE);

        btnAddUser.setOnAction(e -> {
            String username = txtNewUser.getText().trim();
            String password = txtNewPass.getText();
            String fullName = txtNewName.getText().trim();
            String contact = txtNewContact.getText().trim();
            UserRoles role = cmbRole.getValue();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || role == null) {
                lblStatus.setText("Error: All fields must be filled to add user.");
                lblStatus.setStyle("-fx-text-fill: red;");
                return;
            }

            try {
                User newUser;
                if (role == UserRoles.ADMIN) {
                    newUser = new Admin(null, username, password, fullName, contact);
                } else {
                    newUser = new Staff(null, username, password, fullName, contact);
                }

                adminUser.addUser(app, newUser);
                lblStatus.setText("User '" + username + "' added successfully as " + role + ".");
                lblStatus.setStyle("-fx-text-fill: #27ae60;");
                refreshUserTable();
                txtNewUser.clear(); txtNewPass.clear(); txtNewName.clear(); txtNewContact.clear(); cmbRole.getSelectionModel().clearSelection();
            } catch (UserAlreadyExistsException ex) {
                lblStatus.setText("Error adding user: " + ex.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            } catch (Exception ex) {
                lblStatus.setText("System Error adding user: " + ex.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        });

        addUserBox.getChildren().addAll(lblAdd, txtNewUser, txtNewPass, txtNewName, txtNewContact, cmbRole, btnAddUser);
        container.getChildren().add(addUserBox);

        // --- 2. Update Details Section (Edit Name/Username) ---
        VBox updateDetailsBox = new VBox(5);
        updateDetailsBox.setStyle("-fx-padding: 10 0 10 0; -fx-border-width: 1 0 1 0; -fx-border-color: #bdc3c7;");

        Label lblUpdate = new Label("2. Edit User Details (Name/Username)");
        lblUpdate.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        TextField txtTargetUser = new TextField();
        txtTargetUser.setPromptText("Target Username (Current)");

        TextField txtNewUsername = new TextField();
        txtNewUsername.setPromptText("New Username");

        TextField txtNewFullName = new TextField();
        txtNewFullName.setPromptText("New Full Name");

        Button btnUpdateDetails = new Button("Update Details");
        btnUpdateDetails.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnUpdateDetails.setMaxWidth(Double.MAX_VALUE);

        btnUpdateDetails.setOnAction(e -> {
            String targetUser = txtTargetUser.getText().trim();
            String newUsername = txtNewUsername.getText().trim();
            String newFullName = txtNewFullName.getText().trim();

            if (targetUser.isEmpty() || newUsername.isEmpty() || newFullName.isEmpty()) {
                lblStatus.setText("Error: All update fields must be filled.");
                lblStatus.setStyle("-fx-text-fill: red;");
                return;
            }

            try {
                adminUser.updateUserDetails(app, targetUser, newUsername, newFullName);
                lblStatus.setText("Details for '" + targetUser + "' updated successfully to '" + newUsername + "'.");
                lblStatus.setStyle("-fx-text-fill: #27ae60;");
                refreshUserTable();
                txtTargetUser.clear();
                txtNewUsername.clear();
                txtNewFullName.clear();
            } catch (UserNotFoundException ex) {
                lblStatus.setText("Error: User '" + targetUser + "' not found.");
                lblStatus.setStyle("-fx-text-fill: red;");
            } catch (UserAlreadyExistsException ex) {
                lblStatus.setText("Error: " + ex.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            } catch (Exception ex) {
                lblStatus.setText("System Error during update: " + ex.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        });

        updateDetailsBox.getChildren().addAll(lblUpdate, txtTargetUser, txtNewUsername, txtNewFullName, btnUpdateDetails);
        container.getChildren().add(updateDetailsBox);


        // --- 3. Remove User Section ---
        VBox removeBoxContainer = new VBox(5);
        removeBoxContainer.setStyle("-fx-padding: 10 0 10 0; -fx-border-width: 1 0 1 0; -fx-border-color: #bdc3c7;");
        Label lblRemove = new Label("3. Remove User:");
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


        // --- 4. Reset Password Section (Direct Admin Reset) ---
        VBox resetBoxContainer = new VBox(5);
        Label lblReset = new Label("4. Direct Password Reset:");
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


    // ===============================================
    // TAB 2: PASSWORD RESET REQUESTS
    // ===============================================
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

    // ===============================================
    // HELPER METHODS
    // ===============================================

    // Dialog to prompt Admin for a new password
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
            // FIX: Calling the correct method in MainApp with the new signature
            if (app.adminApprovePasswordReset(staffUsername, newPassword)) {
                lblStatus.setText("Password reset for '" + staffUsername + "' approved and updated!");
                lblStatus.setStyle("-fx-text-fill: #27ae60;");
                refreshResetTable();
                refreshUserTable();
            } else {
                lblStatus.setText("Error: Failed to approve password reset for '" + staffUsername + "'.");
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        });
    }

    /**
     * Retrieves users from MainApp (which calls the DB).
     */
    public void refreshUserTable() {
        // Calls the correct DB-integrated method in MainApp
        List<User> activeUsers = app.getActiveUsersList();
        ObservableList<User> data = FXCollections.observableArrayList(activeUsers);
        userTable.setItems(data);
    }

    // Refresh the Password Reset Request Table
    public void refreshResetTable() {
        List<PasswordResetRequest> requests = app.getPendingPasswordResetRequests();
        ObservableList<PasswordResetRequest> data = FXCollections.observableArrayList(requests);
        resetTable.setItems(data);
    }
}