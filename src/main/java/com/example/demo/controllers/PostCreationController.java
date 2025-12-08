package com.example.demo.controllers;

import com.example.demo.api.PostClient;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PostCreationController {

    @FXML private TextArea contentTextArea;
    @FXML private ImageView postImageView;
    @FXML private Label filenameLabel;
    @FXML private Button createButton;
    @FXML private Button cancelButton;
    @FXML private Button chooseImageButton;
    @FXML private Button removeImageButton;

    private File selectedImageFile;
    private final PostClient postClient = new PostClient();

    @FXML
    // Initialize UI sizing and wrapping
    public void initialize() {
        postImageView.setFitWidth(300);
        postImageView.setFitHeight(300);
        postImageView.setPreserveRatio(true);
        contentTextArea.setWrapText(true);
    }

    @FXML
    private void handleChooseImage() {
    // Open a file chooser to select an image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Post Image");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"
        );
        fileChooser.getExtensionFilters().add(imageFilter);

        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            try {
                Image image = new Image(new FileInputStream(selectedImageFile));
                postImageView.setImage(image);
                filenameLabel.setText("Selected: " + selectedImageFile.getName());
                removeImageButton.setDisable(false);
            } catch (FileNotFoundException e) {
                showAlert("Error", "Could not load the selected image.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
   
    // Remove the selected image from the UI
    private void handleRemoveImage() {
        postImageView.setImage(null);
        selectedImageFile = null;
        filenameLabel.setText("No image selected");
        removeImageButton.setDisable(true);
    }

    @FXML
    
    // Create a new post; upload image first if present. Runs in background thread.
    private void handleCreatePost() {
        createButton.setDisable(true);
        new Thread(() -> {
            try {
                String content = contentTextArea.getText().trim();

                if (content.isEmpty()) {
                    javafx.application.Platform.runLater(() -> showAlert("Validation Error", "Post content cannot be empty.", Alert.AlertType.WARNING));
                    return;
                }

                // Upload image first if one was selected
                String imageUrl = null;
                if (selectedImageFile != null) {
                    imageUrl = uploadImage(selectedImageFile);
                    if (imageUrl == null) {
                        javafx.application.Platform.runLater(() -> showAlert("Error", "Failed to upload image.", Alert.AlertType.ERROR));
                        return;
                    }
                }

                // Create new post object
                Post newPost = new Post();
                newPost.setContent(content);
                newPost.setImageUrl(imageUrl);
                Long id = Long.valueOf(1L);
                newPost.setTagId(id);

                // Call your backend to create the post
                ApiResponse<Post> response = postClient.createPost(newPost);
                if (response.isState()) {
                    javafx.application.Platform.runLater(() -> {
                        showAlert("Success", "Post created successfully!", Alert.AlertType.INFORMATION);
                        handleGoBackToProfile();
                    });
                } else {
                    javafx.application.Platform.runLater(() -> showAlert("Error", "Failed to create post: " + response.getMessage(), Alert.AlertType.ERROR));
                }

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> showAlert("Error", "An error occurred while creating the post: " + e.getMessage(), Alert.AlertType.ERROR));
                e.printStackTrace();
            } finally {
                javafx.application.Platform.runLater(() -> createButton.setDisable(false));
            }
        }, "post-create-thread").start();
    }

    // Helper to upload image and return resulting URL or null
    private String uploadImage(File imageFile) {
        try {
            return postClient.uploadImage(imageFile);
        } catch (Exception e) {
            System.out.println("Error uploading image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    
    // Cancel creation and return to profile
    private void handleCancel() {
        handleGoBackToProfile();
    }

    @FXML
    private void handleGoBack() {
        handleGoBackToProfile();
    }

    private void handleGoBackToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/profile-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) contentTextArea.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println("Error going back to profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}