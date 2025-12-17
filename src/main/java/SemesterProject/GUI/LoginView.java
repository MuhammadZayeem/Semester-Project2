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
        rootLayout = new GridPane();
        rootLayout.setAlignment(Pos.CENTER);
        rootLayout.setPadding(new Insets(40));
        rootLayout.setVgap(15);
        rootLayout.setHgap(10);
        rootLayout.setStyle("-fx-background-color: #f0f2f5;");
        showLoginForm();
    }

    // ------------------------------------------Method to return the view
    public Parent getView() {
        return rootLayout;
    }


    private void showLoginForm() {
        rootLayout.getChildren().clear();

        Label title = new Label("System Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        TextField User = new TextField();
        User.setPromptText("Username");
        User.setMaxWidth(250);

        PasswordField Pass = new PasswordField();
        Pass.setPromptText("Password");
        Pass.setMaxWidth(250);

        Button Login = new Button("Login");
        Login.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-weight: bold;");
        Login.setPrefWidth(250);

        Hyperlink linkForgot = new Hyperlink("Forgot Password?");
        linkForgot.setStyle("-fx-font-size: 11px; -fx-text-fill: #3498db;");

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        // -------------------------------------------------------------Login Actions
        Login.setOnAction(e -> {
            boolean success = app.authenticate(User.getText(), Pass.getText());
            System.out.println("clicked");
            if (!success) {
                lblError.setText("Invalid credentials!");
            }
        });

        linkForgot.setOnAction(e -> showResetForm());

        // --------------------------------------------Add to Grid
        rootLayout.add(title, 0, 0);
        rootLayout.add(User, 0, 1);
        rootLayout.add(Pass, 0, 2);
        rootLayout.add(Login, 0, 3);
        rootLayout.add(linkForgot, 0, 4);
        rootLayout.add(lblError, 0, 5);
        // Center Align
        for (javafx.scene.Node node : rootLayout.getChildren()) {
            GridPane.setHalignment(node, HPos.CENTER);
        }
    }

    private void showResetForm() {
        rootLayout.getChildren().clear();

        Label title = new Label("Password Reset Request");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TextField User = new TextField();
        User.setPromptText("Enter your Username (Staff Only)");
        User.setMaxWidth(250);

        Button Request = new Button("Request Reset");
        Request.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-weight: bold;");
        Request.setPrefWidth(250);

        Button Back = new Button("Back to Login");
        Back.setStyle("-fx-font-size: 11px; -fx-text-fill: red;");

        Label lblStatus = new Label();

        // ------------------------------------------------------Request Actions
        Request.setOnAction(e -> {
            String username = User.getText().trim();
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
            User.clear();
        });

        Back.setOnAction(e -> showLoginForm());

        // --------------------------------------------------------Add to Grid
        rootLayout.add(title, 0, 0);
        rootLayout.add(User, 0, 1);
        rootLayout.add(Request, 0, 2);
        rootLayout.add(Back, 0, 3);
        rootLayout.add(lblStatus, 0, 4);

        // Center Align
        for (javafx.scene.Node node : rootLayout.getChildren()) {
            GridPane.setHalignment(node, HPos.CENTER);
        }
    }
}