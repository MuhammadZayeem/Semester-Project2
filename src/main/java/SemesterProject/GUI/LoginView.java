package SemesterProject.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView extends VBox {

    // Main VBox container to switch between login and reset views
    private VBox loginContainer;
    private VBox resetContainer;
    private MainApp app;

    public LoginView(MainApp app) {
        this.app = app;
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(40));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: #f0f2f5;");

        // Initialize the two main views
        loginContainer = createLoginContainer();
        resetContainer = createResetContainer();
        resetContainer.setVisible(false);
        resetContainer.setManaged(false); // Hide and remove from layout

        this.getChildren().addAll(loginContainer, resetContainer);
    }

    // --- Login Container (Original GUI) ---
    private VBox createLoginContainer() {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(0)); // No extra padding

        Label title = new Label("System Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        TextField txtUser = new TextField();
        txtUser.setPromptText("Username");
        txtUser.setMaxWidth(250);

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Password");
        txtPass.setMaxWidth(250);

        Button btnLogin = new Button("Login");
        btnLogin.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLogin.setPrefWidth(250);

        // ADDED: Forgot Password Link
        Hyperlink linkForgot = new Hyperlink("Forgot Password?");
        linkForgot.setStyle("-fx-font-size: 11px; -fx-text-fill: #3498db;");

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        // Action for Login Button
        btnLogin.setOnAction(e -> {
            boolean success = app.authenticate(txtUser.getText(), txtPass.getText());
            if (!success) {
                lblError.setText("Invalid credentials!");
            }
        });

        // Action for Forgot Password Link
        linkForgot.setOnAction(e -> {
            toggleView(false); // Switch to reset view
            lblError.setText(""); // Clear any previous error
        });

        // The order is important to maintain the look of the original GUI
        container.getChildren().addAll(title, txtUser, txtPass, btnLogin, linkForgot, lblError);
        return container;
    }

    // --- Password Reset Request Container (New GUI) ---
    private VBox createResetContainer() {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(0));

        Label title = new Label("Password Reset Request");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TextField txtUser = new TextField();
        txtUser.setPromptText("Enter your Username (Staff Only)");
        txtUser.setMaxWidth(250);

        Button btnRequest = new Button("Request Reset");
        btnRequest.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRequest.setPrefWidth(250);

        Hyperlink linkBack = new Hyperlink("Back to Login");
        linkBack.setStyle("-fx-font-size: 11px; -fx-text-fill: #3498db;");

        Label lblStatus = new Label();

        // Action for Request Reset Button
        btnRequest.setOnAction(e -> {
            String username = txtUser.getText().trim();
            if (username.isEmpty()) {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("Username cannot be empty.");
                return;
            }

            // Call the MainApp method which delegates to LoginManager
            String resultMessage = app.requestPasswordReset(username);

            // Set style based on success or failure message
            if (resultMessage.contains("not found") || resultMessage.contains("Only Staff") || resultMessage.contains("already exists")) {
                lblStatus.setStyle("-fx-text-fill: red;");
            } else {
                lblStatus.setStyle("-fx-text-fill: #27ae60;"); // Green for success/pending
            }
            lblStatus.setText(resultMessage);
            txtUser.clear();
        });

        // Action for Back Link
        linkBack.setOnAction(e -> {
            toggleView(true); // Switch to login view
            lblStatus.setText(""); // Clear status
            txtUser.clear();
        });

        container.getChildren().addAll(title, txtUser, btnRequest, linkBack, lblStatus);
        return container;
    }

    // Helper method to toggle between the two VBoxes
    private void toggleView(boolean showLogin) {
        if (showLogin) {
            loginContainer.setVisible(true);
            loginContainer.setManaged(true);
            resetContainer.setVisible(false);
            resetContainer.setManaged(false);
            // Since the title label is outside the containers, we can't change it easily.
            // If the size is adjusted in the container, the MainApp's scene size might need adjustment.
            // For now, we rely on the VBox to manage the space for the visible view.
        } else {
            loginContainer.setVisible(false);
            loginContainer.setManaged(false);
            resetContainer.setVisible(true);
            resetContainer.setManaged(true);
        }
    }
}