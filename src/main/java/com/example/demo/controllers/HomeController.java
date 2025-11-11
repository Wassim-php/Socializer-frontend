package com.example.demo.controllers;

import com.example.demo.api.ApiClient;
import com.example.demo.api.PostClient;
import com.example.demo.api.UserClient;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class HomeController {

    @FXML
    private ListView<Post> postList;

    private final PostClient postClient = new PostClient();
    private final UserClient userClient = new UserClient();
    private final ObservableList<Post> posts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        postList.setItems(posts);

        // Custom cell layout
        postList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                if (empty || post == null) {
                    setGraphic(null);
                } else {
                    VBox postBox = createPostBox(post);
                    setGraphic(postBox);
                }
            }
        });

        refreshPosts();
    }

    private VBox createPostBox(Post post) {
        ImageView postImageView = new ImageView();
        VBox postBox = new VBox(8);
        postBox.setPadding(new Insets(15));
        postBox.setStyle("-fx-background-color: black; -fx-border-color: #ddd; -fx-border-radius: 10; -fx-background-radius: 10;");
        postBox.setMaxWidth(Double.MAX_VALUE);

        // User
        User user = null;
        try {
            user = userClient.getUserById(post.getUserId()).getData();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        Label userLabel = new Label(user != null && user.getUserName() != null ? user.getUserName() : "Unknown User");
        userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        System.out.println("Post image URL: " + post.getImageUrl());
        // Ensure ImageView has size and preserves ratio
        postImageView.setFitWidth(400);   // adjust as needed
        postImageView.setFitHeight(300);  // adjust as needed
        postImageView.setPreserveRatio(true);

        String imageUrl = post.getImageUrl(); // can be full URL or just filename
        Image image = null;

        try {
            if (imageUrl == null || imageUrl.isEmpty()) {
                // No image, use local placeholder
                image = new Image(getClass().getResource("/images/placeholder.png").toExternalForm());
            } else {
                // If it starts with http, use directly, else prepend backend URL
                if (!imageUrl.startsWith("http")) {
                    imageUrl = "http://localhost:7007/api/uploads/" + imageUrl;
                }

                // Load asynchronously in JavaFX
                image = new Image(imageUrl, true);

                // Add listeners for debug
                image.exceptionProperty().addListener((obs, oldEx, newEx) -> {
                    if (newEx != null) {
                        System.out.println("Failed to load image: ");
                        newEx.printStackTrace();
                    }
                });

                image.progressProperty().addListener((obs, oldProg, newProg) -> {
                    System.out.println("Loading progress: " + (newProg.doubleValue() * 100) + "%");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            // fallback placeholder
            image = new Image(getClass().getResource("/images/placeholder.png").toExternalForm());
        }
        postImageView.setImage(image);

        // Caption
        Label captionLabel = new Label(post.getContent() != null ? post.getContent() : "");
        captionLabel.setWrapText(true);
        captionLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #faf7f7;");

        // Buttons
        Button likeButton = new Button("❤ " + post.getLikedUserIds().size());
        likeButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 12;");
        Button commentButton = new Button("💬 Comments");
        commentButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 12;");
        Button deleteButton = new Button("🗑 Delete");
        deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-cursor: hand; -fx-font-size: 12;");

        likeButton.setOnAction(e -> handleLike(post));
        commentButton.setOnAction(e -> openPostDetails(post));
        deleteButton.setOnAction(e -> handleDelete(post));

        HBox actions = new HBox(15, likeButton, commentButton, deleteButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        postBox.getChildren().addAll(userLabel, postImageView, captionLabel, actions);
        return postBox;
    }



    private void handleLike(Post post) {
        try {
            postClient.likePost(post.getId());
            refreshPosts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Post post) {
        try {
            postClient.delete(post.getId());
            refreshPosts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshPosts() {
        try {
            ApiResponse<List<Post>> response = postClient.fetchPosts();
            posts.clear();
            if (response.isState() && response.getData() != null) {
                posts.addAll(response.getData());
            } else {
                Label empty = new Label("No posts available");
                empty.setStyle("-fx-text-fill: gray;");
                postList.setPlaceholder(empty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openPostDetails(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/post-view.fxml"));
            Scene scene = new Scene(loader.load());

            PostController controller = loader.getController();
            controller.setPost(post);

            Stage stage = (Stage) postList.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openProfile() {
        System.out.println("Profile screen not implemented yet");
    }

    @FXML
    private void handleLogout() {
        try {
            // 1️⃣ Logout from backend & clear local session
            ApiClient client = new ApiClient() {}; // anonymous subclass to access method
            client.logout();

            // 2️⃣ Load the login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());

            // 3️⃣ Get current stage
            Stage stage = (Stage) postList.getScene().getWindow(); // or any node in your scene

            // 4️⃣ Set new scene
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
