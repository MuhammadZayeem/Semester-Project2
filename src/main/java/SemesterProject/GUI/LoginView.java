package SemesterProject.GUI;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {

    private MainApp app;
    private GridPane rootLayout;

    public LoginView(MainApp app) {
        this.app = app;

        // Initialize Root Layout (GridPane)
        rootLayout = new GridPane();
        rootLayout.setAlignment(Pos.CENTER);
        rootLayout.setPadding(new Insets(40));
        rootLayout.setVgap(15); // Vertical spacing between rows
        rootLayout.setHgap(10);
        rootLayout.setStyle("-fx-background-color: #f0f2f5;");

        // Start with Login Form
        showLoginForm();
    }

    // --- Critical Method to return the view to the Scene ---
    public Parent getView() {
        return rootLayout;
    }

    // =================================================================================
    // FORM BUILDERS
    // =================================================================================

    private void showLoginForm() {
        rootLayout.getChildren().clear(); // Clear previous view

        Label title = new Label("System Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        TextField txtUser = new TextField();
        txtUser.setPromptText("Username");
        txtUser.setMaxWidth(250);

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Password");
        txtPass.setMaxWidth(250);

        Button btnLogin = new Button("Login");
        btnLogin.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLogin.setPrefWidth(250);

        Hyperlink linkForgot = new Hyperlink("Forgot Password?");
        linkForgot.setStyle("-fx-font-size: 11px; -fx-text-fill: #3498db;");

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        // --- Actions ---
        btnLogin.setOnAction(e -> {
            boolean success = app.authenticate(txtUser.getText(), txtPass.getText());
            if (!success) {
                lblError.setText("Invalid credentials!");
            }
        });

        linkForgot.setOnAction(e -> showResetForm());

        // --- Add to Grid (Column 0, Row X) ---
        rootLayout.add(title, 0, 0);
        rootLayout.add(txtUser, 0, 1);
        rootLayout.add(txtPass, 0, 2);
        rootLayout.add(btnLogin, 0, 3);
        rootLayout.add(linkForgot, 0, 4);
        rootLayout.add(lblError, 0, 5);

        // Center Align Everything
        for (javafx.scene.Node node : rootLayout.getChildren()) {
            GridPane.setHalignment(node, HPos.CENTER);
        }
    }

    private void showResetForm() {
        rootLayout.getChildren().clear(); // Clear Login view

        Label title = new Label("Password Reset Request");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TextField txtUser = new TextField();
        txtUser.setPromptText("Enter your Username (Staff Only)");
        txtUser.setMaxWidth(250);

        Button btnRequest = new Button("Request Reset");
        btnRequest.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRequest.setPrefWidth(250);

        Hyperlink linkBack = new Hyperlink("Back to Login");
        linkBack.setStyle("-fx-font-size: 11px; -fx-text-fill: red;");

        Label lblStatus = new Label();

        // --- Actions ---
        btnRequest.setOnAction(e -> {
            String username = txtUser.getText().trim();
            if (username.isEmpty()) {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("Username cannot be empty.");
                return;
            }
            String resultMessage = app.requestPasswordReset(username);

            if (resultMessage.contains("not found") || resultMessage.contains("Only Staff") || resultMessage.contains("already exists")) {
                lblStatus.setStyle("-fx-text-fill: red;");
            } else {
                lblStatus.setStyle("-fx-text-fill: green;");
            }
            lblStatus.setText(resultMessage);
            txtUser.clear();
        });

        linkBack.setOnAction(e -> showLoginForm());

        // --- Add to Grid ---
        rootLayout.add(title, 0, 0);
        rootLayout.add(txtUser, 0, 1);
        rootLayout.add(btnRequest, 0, 2);
        rootLayout.add(linkBack, 0, 3);
        rootLayout.add(lblStatus, 0, 4);

        // Center Align Everything
        for (javafx.scene.Node node : rootLayout.getChildren()) {
            GridPane.setHalignment(node, HPos.CENTER);
        }
    }
}