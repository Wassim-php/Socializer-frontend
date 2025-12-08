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
    // Initialize home view and set up custom list cell factory
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
    // Create the visual layout for a single post in the feed
        VBox postBox = new VBox(8);
        postBox.setPadding(new Insets(15));
        postBox.setStyle("-fx-background-color: #1E1E1E; -fx-border-color: #333; -fx-border-radius: 10; -fx-background-radius: 10;");
        postBox.setMaxWidth(Double.MAX_VALUE);

        // User
        User user = null;
        try {
            user = userClient.getUserById(post.getUserId()).getData();
        } catch (Exception e) {
            System.out.println("Error fetching user: " + e.getMessage());
        }
        final User finalUser = user;
        Button userButton = new Button(user != null && user.getUserName() != null ? user.getUserName() : "Unknown User");
        userButton.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: white; -fx-background-color: #1E1E1E");

        userButton.setOnAction(e -> openUserProfile(finalUser));

        // Only create ImageView if we have a valid image URL
        String imageUrl = post.getImageUrl();
        ImageView postImageView = null;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                postImageView = new ImageView();
                postImageView.setFitWidth(400);
                postImageView.setFitHeight(300);
                postImageView.setPreserveRatio(true);

                if (!imageUrl.startsWith("http")) {
                    imageUrl = "http://localhost:7007/api/uploads/" + imageUrl;
                }

                Image image = new Image(imageUrl, true);
                postImageView.setImage(image);

                // Error handling for failed image loads
                image.exceptionProperty().addListener((obs, oldEx, newEx) -> {
                    if (newEx != null) {
                        System.out.println("Failed to load image from URL: ");
                    }
                });

            } catch (Exception e) {
                System.out.println("Error setting up image view: " + e.getMessage());
                postImageView = null; // Don't add image view if setup fails
            }
        }

        // Caption
        Label captionLabel = new Label(post.getContent() != null ? post.getContent() : "");
        captionLabel.setWrapText(true);
        captionLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #FFFFFF;");

        // Buttons
        Button likeButton = new Button("❤ " + (post.getLikedUserIds() != null ? post.getLikedUserIds().size() : 0));
        likeButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 12; -fx-text-fill: #BB86FC;");
        Button commentButton = new Button("💬 Comments");
        commentButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 12; -fx-text-fill: #BB86FC;");
        Button deleteButton = new Button("🗑 Delete");
        deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #CF6679; -fx-cursor: hand; -fx-font-size: 12;");

        likeButton.setOnAction(e -> handleLike(post));
        commentButton.setOnAction(e -> openPostDetails(post));
        deleteButton.setOnAction(e -> handleDelete(post));

        HBox actions = new HBox(15, likeButton, commentButton, deleteButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        // Build the post box dynamically
        postBox.getChildren().add(userButton);
        if (postImageView != null) {
            postBox.getChildren().add(postImageView);
        }
        postBox.getChildren().addAll(captionLabel, actions);

        return postBox;
    }



    private void handleLike(Post post) {
    // Send like request for a post and refresh feed
        try {
            postClient.likePost(post.getId());
            refreshPosts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Post post) {
    // Delete a post and refresh feed
        try {
            postClient.delete(post.getId());
            refreshPosts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshPosts() {
    // Reload posts from backend and update list view
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
    // Open the detailed post view for the selected post
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
    // Navigate to the current user's profile view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/profile-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) postList.getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    private void handleLogout() {
    // Logout and return to login view
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

    private void openUserProfile(User user) {
    // Open another user's profile view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/user-profile-view.fxml"));
            Scene scene = new Scene(loader.load());

            UserProfileController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) postList.getScene().getWindow();
            stage.setScene(scene);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
