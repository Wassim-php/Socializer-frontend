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

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final ApiClient api = new ApiClient() {}; // using anonymous subclass

    @FXML
    public void handleLogin(ActionEvent event) {
        // Handle login button click: validate fields and perform login
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        boolean success = api.login(username, password);

        if (success) {
            statusLabel.setText("✅ Login successful!");
            openMainWindow();
        } else {
            statusLabel.setText("❌ Invalid username or password");
        }
    }

    private void openMainWindow() {
        try {
            // Open the main application window (home view) after successful login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/home-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main Application");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openRegister(ActionEvent event) {
        try {
            // Open the registration view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/register-view.fxml"));
            Parent root = loader.load();

            // Get current stage
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Create a new scene
            Scene scene = new Scene(root);

            // Add your CSS file (same as login)
            scene.getStylesheets().add(getClass().getResource("/com/example/demo/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Register");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
