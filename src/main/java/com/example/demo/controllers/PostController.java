package com.example.demo.controllers;

import com.example.demo.api.CommentClient;
import com.example.demo.api.PostClient;
import com.example.demo.api.UserClient;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class PostController {

    @FXML
    private Label userLabel;

    @FXML
    private ImageView postImageView;

    @FXML
    private Label captionLabel;

    @FXML
    private ListView<Comment> commentList;

    @FXML
    private TextField commentField;

    @FXML
    private Button addCommentButton;

    private final CommentClient commentClient = new CommentClient();
    private Post post;
    private final ObservableList<Comment> comments = FXCollections.observableArrayList();
    private final UserClient userClient = new UserClient();
    private final PostClient postClient = new PostClient();

    /**
     * This method is called by HomeController when a post is clicked.
     */
    public void setPost(Post post) {
        this.post = post;
        loadPostDetails();
    }

    private void loadPostDetails() {
    // Load post metadata, image and comments into the view
        if (post == null) return;

        User user = null;
        try {
            user = userClient.getUserById(post.getUserId()).getData();
        } catch (Exception e) {
            System.out.println("Error fetching user: " + e.getMessage());
        }

        userLabel.setText(user != null ? user.getUserName() : "Unknown User");
        captionLabel.setText(post.getContent() != null ? post.getContent() : "");

        System.out.println("Post image URL: " + post.getImageUrl());

        // Configure ImageView
        postImageView.setFitWidth(400);
        postImageView.setFitHeight(300);
        postImageView.setPreserveRatio(true);

        String imageUrl = post.getImageUrl();

        // Only load image if we have a valid URL
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                if (!imageUrl.startsWith("http")) {
                    imageUrl = "http://localhost:7007/api/uploads/" + imageUrl;
                }

                // Load asynchronously in JavaFX
                Image image = new Image(imageUrl, true);
                postImageView.setImage(image);

                // Add listeners for debug
                image.exceptionProperty().addListener((obs, oldEx, newEx) -> {
                    if (newEx != null) {
                        System.out.println("Failed to load post image from URL: " );
                        // Clear the image view if loading fails
                        postImageView.setImage(null);
                    }
                });

                image.progressProperty().addListener((obs, oldProg, newProg) -> {
                    System.out.println("Loading progress for " + ": " + (newProg.doubleValue() * 100) + "%");
                });

            } catch (Exception e) {
                System.out.println("Error setting up image view: " + e.getMessage());
                postImageView.setImage(null); // Clear if setup fails
            }
        } else {
            // No image URL - clear the image view
            postImageView.setImage(null);
        }

        loadComments();
        addCommentButton.setOnAction(e -> addComment());
    }

    private void loadComments() {
    // Load comments for the current post and render them in the list
        try {
            ApiResponse<List<Comment>> response = commentClient.getCommentsByPostId(post.getId());
            System.out.println("Comments: " + response.getData());
            comments.clear();
            if (response.isState() && response.getData() != null) {
                comments.addAll(response.getData());
            }
            commentList.setItems(comments);

            commentList.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Comment comment, boolean empty) {
                    super.updateItem(comment, empty);
                    if (empty || comment == null) {
                        setGraphic(null);
                    } else {
                        try {
                            Label usernameLabel;

                            if (comment.getUserId() != null) {
                                // Fetch username safely
                                ApiResponse<User> response = userClient.getUserById(comment.getUserId());
                                User user = response != null ? response.getData() : null;

                                usernameLabel = new Label(user != null ? user.getUserName() : "Unknown User");
                            } else {
                                usernameLabel = new Label("Unknown User");
                            }

                            usernameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

                            // Content (normal text)
                            Label content = new Label(comment.getContent());
                            content.setWrapText(true);
                            content.setStyle("-fx-text-fill: #555;");

                            // Delete button
                            Button deleteButton = new Button("🗑");
                            deleteButton.setOnAction(e -> {
                                try {
                                    commentClient.delete(comment.getId());
                                    loadComments();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            });
                            deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-cursor: hand; -fx-font-size: 12;");

                            // Top section (username + delete button)
                            HBox topRow = new HBox(10, usernameLabel, deleteButton);
                            topRow.setAlignment(Pos.CENTER_LEFT);

                            // Comment box (vertical layout)
                            VBox commentBox = new VBox(5, topRow, content);
                            commentBox.setPadding(new Insets(5, 10, 5, 10));
                            commentBox.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8;");

                            setGraphic(commentBox);
                        } catch (Exception ex) {
                            System.out.println("Error loading comment user: " + ex.getMessage());
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addComment() {
    // Add a new comment to the post
        String content = commentField.getText().trim();
        if (content.isEmpty()) return;

        try {
            Comment newComment = new Comment();
            newComment.setContent(content);

            postClient.addComment(post.getId(), newComment);
            commentField.clear();
            loadComments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
    // Return to the home view
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/demo/fxml/home-view.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            javafx.stage.Stage stage = (javafx.stage.Stage) userLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}