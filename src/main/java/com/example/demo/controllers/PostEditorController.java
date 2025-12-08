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

public class PostEditorController {

    @FXML private TextArea contentTextArea;
    @FXML private ImageView postImageView;
    @FXML private Label filenameLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button chooseImageButton;
    @FXML private Button removeImageButton;
    @FXML private Label titleLabel;

    private Post post;
    private File selectedImageFile;
    private final PostClient postClient = new PostClient();
    private String uploadedImageUrl;


    // Set the post to edit and populate UI fields
    public void setPost(Post post) {
        this.post = post;
        initializeData();
    }

  
    // Populate controller fields from the provided Post
    private void initializeData() {
        if (post != null) {
            titleLabel.setText("Edit Post");
            contentTextArea.setText(post.getContent());
            uploadedImageUrl = post.getImageUrl();

            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                loadImageFromUrl(post.getImageUrl());
            }
        } else {
            titleLabel.setText("Create New Post");
        }
    }

    
    // Load an image into the ImageView from a remote or local filename
    private void loadImageFromUrl(String imageUrl) {
        try {
            String fullUrl = imageUrl;
            if (!imageUrl.startsWith("http")) {
                fullUrl = "http://localhost:7007/api/uploads/" + imageUrl;
            }
            Image image = new Image(fullUrl, true);
            postImageView.setImage(image);
            filenameLabel.setText("Current image: " + imageUrl);
            removeImageButton.setDisable(false);
        } catch (Exception e) {
            System.out.println("Error loading existing image: " + e.getMessage());
        }
    }

    @FXML

    // Initialize UI controls
    public void initialize() {
        postImageView.setFitWidth(300);
        postImageView.setFitHeight(300);
        postImageView.setPreserveRatio(true);
        contentTextArea.setWrapText(true);
    }

    @FXML

    // Show file chooser to select an image for the post
    private void handleChooseImage() {
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
                uploadedImageUrl = null;
            } catch (FileNotFoundException e) {
                showAlert("Error", "Could not load the selected image.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    
    // Clear any selected/uploaded image
    private void handleRemoveImage() {
        postImageView.setImage(null);
        selectedImageFile = null;
        uploadedImageUrl = null;
        filenameLabel.setText("No image selected");
        removeImageButton.setDisable(true);
    }

    @FXML

    // Save changes to the post (and upload image if needed)
    private void handleSave() {
        try {
            if (contentTextArea.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Post content cannot be empty.", Alert.AlertType.WARNING);
                return;
            }

            String finalImageUrl = uploadedImageUrl;

            if (selectedImageFile != null) {
                finalImageUrl = uploadImage(selectedImageFile);
                if (finalImageUrl == null) {
                    showAlert("Error", "Failed to upload image.", Alert.AlertType.ERROR);
                    return;
                }
            }

            if (post != null) {
                post.setContent(contentTextArea.getText().trim());
                post.setImageUrl(finalImageUrl);

                ApiResponse<Post> response = postClient.updatePost(post.getId(), post);
                if (response.isState()) {
                    showAlert("Success", "Post updated successfully!", Alert.AlertType.INFORMATION);
                    handleGoBack();
                } else {
                    showAlert("Error", "Failed to update post: " + response.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Info", "Create post functionality not implemented yet.", Alert.AlertType.INFORMATION);
            }

        } catch (Exception e) {
            showAlert("Error", "An error occurred while saving the post: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Upload image file via PostClient and return the resulting URL or null
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
    private void handleCancel() {
        handleGoBack();
    }

    @FXML
    private void handleGoBack() {
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