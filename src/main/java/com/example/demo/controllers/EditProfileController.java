package com.example.demo.controllers;

import com.example.demo.api.ApiClient;
import com.example.demo.api.UserClient;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.User;
import com.example.demo.model.UserUpdate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.paint.Color;

public class EditProfileController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label errorLabel;
    @FXML
    private ColorPicker avatarColorPicker;

    private User currentUser;
    private final UserClient userClient = new UserClient();
    private ProfileController profileController; // NEW FIELD: Reference to the calling controller

    
    // Store reference to ProfileController so it can be refreshed after edits
    public void setProfileController(ProfileController profileController) {
        this.profileController = profileController;
    }

   
    public void setUser(User user) {
        this.currentUser = user;
        loadUserData();
    }

   
    private void loadUserData() {
        if (currentUser != null) {
            usernameField.setText(currentUser.getUserName());
            if (currentUser.getAvatarColorHex() != null) {
                try {
                    avatarColorPicker.setValue(Color.web(currentUser.getAvatarColorHex()));
                } catch (IllegalArgumentException e) {
                    avatarColorPicker.setValue(Color.web("#BB86FC"));
                }
            } else {
                avatarColorPicker.setValue(Color.web("#BB86FC"));
            }
        }
        System.out.println("Current User IDDDD: " + currentUser.getId());
    }

    @FXML
    
    // Initialize form state
    public void initialize() {
        errorLabel.setText("");
    }

   
    // Validate and save profile changes (may update password)
    @FXML
    private void handleSave() {
        try {
            errorLabel.setText("");

            String newUsername = usernameField.getText().trim();
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            Color selectedColor = avatarColorPicker.getValue();
            // Convert to #RRGGBB format
            String newColorHex = "#" + selectedColor.toString().substring(2, 8).toUpperCase();

            if (newUsername.isEmpty()) {
                showError("Username cannot be empty.");
                return;
            }

            if (newUsername.length() < 3) {
                showError("Username must be at least 3 characters.");
                return;
            }

            boolean changingPassword = !newPassword.isEmpty() || !confirmPassword.isEmpty();

            if (changingPassword) {
                if (currentPassword.isEmpty()) {
                    showError("Current password is required to change password.");
                    return;
                }

                if (newPassword.isEmpty()) {
                    showError("New password cannot be empty.");
                    return;
                }

                if (newPassword.length() < 6) {
                    showError("New password must be at least 6 characters.");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    showError("New passwords do not match.");
                    return;
                }
            }

            if (changingPassword) {
                updateUserWithPassword(newUsername, currentPassword, newPassword, newColorHex);
            } else {
                updateUsernameAndColorOnly(newUsername, newColorHex);
            }

        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

   
    private void updateUsernameAndColorOnly(String newUsername, String newColorHex) throws Exception {
        UserUpdate userUpdate = new UserUpdate(newUsername, null, null, newColorHex);

        ApiResponse<User> response = userClient.updateUserProfile(currentUser.getId(), userUpdate);

        if (response.isState()) {
            showSuccess("Profile updated successfully!");
            currentUser.setUserName(newUsername);
            currentUser.setAvatarColorHex(newColorHex);
            delayAndReturn();
        } else {
            showError("Failed to update profile: " + response.getMessage());
        }
    }

   
    // Update username and password together
    private void updateUserWithPassword(String newUsername, String currentPassword, String newPassword, String newColorHex) throws Exception {
        UserUpdate userUpdate = new UserUpdate(newUsername, currentPassword, newPassword, newColorHex);

        ApiResponse<User> response = userClient.updateUserProfile(currentUser.getId(), userUpdate);

        if (response.isState()) {
            showSuccess("Profile updated successfully!");
            currentUser.setUserName(newUsername);
            currentUser.setAvatarColorHex(newColorHex);
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            delayAndReturn();
        } else {
            showError("Failed to update profile: " + response.getMessage());
        }
    }

    
    // Pause briefly to show success message then return to profile/login
    private void delayAndReturn() {
        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> handleReturnToProfile());
        pause.play();
    }

    
    // Return to login view and clear session
    private void handleReturnToProfile() {
        try {
            ApiClient client = new ApiClient() {}; // anonymous subclass to access method
            client.logout();

            // 2️⃣ Load the login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());

            // 3️⃣ Get current stage
            Stage stage = (Stage) usernameField.getScene().getWindow(); // or any node in your scene

            // 4️⃣ Set new scene
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println("Error going back to profile: " + e.getMessage());
            e.printStackTrace();
        }
        // Remove the 'if (profileController != null)' block entirely
    }

    @FXML
    private void handleCancel() {
        // If the user cancels, we still return to the profile view using the same logic
        handleReturnToProfile();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #CF6679; -fx-font-size: 14;");
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #03DAC6; -fx-font-size: 14;");
    }
}