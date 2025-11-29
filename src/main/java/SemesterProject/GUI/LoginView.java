package SemesterProject.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView extends VBox {

    public LoginView(MainApp app) {
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(40));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: #f0f2f5;");

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

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        btnLogin.setOnAction(e -> {
            boolean success = app.authenticate(txtUser.getText(), txtPass.getText());
            if (!success) {
                lblError.setText("Invalid credentials!");
            }
        });

        this.getChildren().addAll(title, txtUser, txtPass, btnLogin, lblError);
    }
}