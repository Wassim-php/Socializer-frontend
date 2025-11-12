package com.example.demo.controllers;

import com.example.demo.api.ApiClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import java.io.IOException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    private final ApiClient api = new ApiClient() {}; // dummy client

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠️ Please fill in all fields.");
            return;
        }

        boolean success = api.register(username, password);

        if (success) {
            statusLabel.setText("✅ Registration successful! Redirecting to login...");
            openLogin();
        } else {
            statusLabel.setText("❌ Registration failed, try again.");
        }
    }


    @FXML
    public void openLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);

            // ✅ Add CSS again
            scene.getStylesheets().add(getClass().getResource("/com/example/demo/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
