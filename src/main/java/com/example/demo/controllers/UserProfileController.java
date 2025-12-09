package com.example.demo.controllers;

import com.example.demo.api.PostClient;
import com.example.demo.api.UserClient;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class UserProfileController {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label followersCountLabel;
    @FXML
    private Label followingCountLabel;
    @FXML
    private Label postCountLabel;
    @FXML
    private Button followButton;
    @FXML
    private FlowPane gridPane;
    @FXML
    private ListView<User> usersListView;
    @FXML
    private Button showFollowersBtn;
    @FXML
    private Button showFollowingBtn;
    @FXML
    private VBox avatarContainer;

    private User viewedUser;
    private User currentUser;
    private final PostClient postClient = new PostClient();
    private final UserClient userClient = new UserClient();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private boolean showingFollowers = true;
    private boolean isFollowing = false;


    // Set the user to view and load their details
    public void setUser(User user) {
        this.viewedUser = user;
        loadUserDetails();
    }


    // Load data for the viewed user and initialize UI components
    private void loadUserDetails() {
        try {
            // Load current user first
            ApiResponse<User> currentUserResponse = userClient.getCurrentUser();
            if (currentUserResponse.isState() && currentUserResponse.getData() != null) {
                currentUser = currentUserResponse.getData();
            }

            // Update UI with viewed user's data
            usernameLabel.setText("@" + viewedUser.getUserName());
            updateFollowCounts();
            updateFollowButton();

            // Load user's posts
            loadUserPosts();

            // Setup users list view
            setupUsersListView();

            // Start by showing followers
            handleShowFollowers();

            // Set click handler on the avatar container
            if (avatarContainer != null) {
                avatarContainer.setOnMouseClicked(e -> handleViewAvatar());
            }

        } catch (Exception e) {
            System.out.println("Error loading user details: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Update followers/following labels for viewed user
    private void updateFollowCounts() {
        if (viewedUser != null) {
            followersCountLabel.setText(viewedUser.getFollowersCount() + " followers");
            followingCountLabel.setText(viewedUser.getFollowingCount() + " following");
        }
    }


    // Configure follow/edit button depending on whether viewing own profile
    private void updateFollowButton() {
        if (currentUser == null || viewedUser == null) return;

        // Check if current user is viewing their own profile
        if (currentUser.getId().equals(viewedUser.getId())) {
            followButton.setText("Edit Profile");
            followButton.setStyle("-fx-background-color: #BB86FC; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 16; -fx-font-weight: bold;");
            followButton.setOnAction(e -> handleEditProfile());
            return;
        }

        // Check follow status (you might need to implement this in your backend)
        // For now, we'll just show "Follow" and update on click
        try {
            followButton.setText("Follow");
            followButton.setStyle("-fx-background-color: #BB86FC; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 16; -fx-font-weight: bold;");
            followButton.setOnAction(e -> handleFollow());
        } catch (Exception e) {
            System.out.println("Error checking follow status: " + e.getMessage());
        }
    }



    // Follow or unfollow the viewed user
    @FXML
    private void handleFollow() {
        try {
            if (isFollowing) {
                // Unfollow
                ApiResponse<Void> response = userClient.unfollowUser(viewedUser.getId());
                if (response.isState()) {
                    isFollowing = false;
                    followButton.setText("Follow");
                    followButton.setStyle("-fx-background-color: #BB86FC; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 16; -fx-font-weight: bold;");

                    // Update follow counts
                    viewedUser.setFollowersCount(viewedUser.getFollowersCount() - 1);
                    updateFollowCounts();
                    System.out.println("Successfully unfollowed user");
                }
            } else {
                // Follow
                ApiResponse<Void> response = userClient.followUser(viewedUser.getId());
                if (response.isState()) {
                    isFollowing = true;
                    followButton.setText("Unfollow");
                    followButton.setStyle("-fx-background-color: #CF6679; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 16; -fx-font-weight: bold;");

                    // Update follow counts
                    viewedUser.setFollowersCount(viewedUser.getFollowersCount() + 1);
                    updateFollowCounts();
                    System.out.println("Successfully followed user");
                }
            }
        } catch (Exception e) {
            System.out.println("Error following/unfollowing: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Load posts authored by the viewed user
    private void loadUserPosts() {
        try {
            if (viewedUser == null) return;

            ApiResponse<List<Post>> response = postClient.getAllByUserId(viewedUser.getId());
            gridPane.getChildren().clear();

            if (response.isState() && response.getData() != null) {
                postCountLabel.setText(String.valueOf(response.getData().size()));

                for (Post post : response.getData()) {
                    Node thumb = createThumbnail(post);
                    gridPane.getChildren().add(thumb);
                }
            } else {
                postCountLabel.setText("0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            postCountLabel.setText("0");
        }
    }


    // Setup the list view used to show followers/following
    private void setupUsersListView() {
        usersListView.setItems(users);
        usersListView.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: #1E1E1E;");
                } else {
                    try {
                        HBox userBox = new HBox(10);
                        userBox.setStyle("-fx-padding: 10; -fx-alignment: center-left;");
                        userBox.setPrefWidth(460);

                        // User avatar
                        StackPane avatar = new StackPane();
                        avatar.setPrefSize(40, 40);
                        avatar.setStyle("-fx-background-color: #BB86FC; -fx-background-radius: 20;");

                        if (user.getUserName() != null && !user.getUserName().isEmpty()) {
                            Label avatarLabel = new Label(user.getUserName().substring(0, 1).toUpperCase());
                            avatarLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                            avatar.getChildren().add(avatarLabel);
                        }

                        // User info
                        VBox userInfo = new VBox(2);
                        Label usernameLabel = new Label("@" + user.getUserName());
                        usernameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");

                        Label statsLabel = new Label(user.getFollowersCount() + " followers • " + user.getFollowingCount() + " following");
                        statsLabel.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 11px;");

                        userInfo.getChildren().addAll(usernameLabel, statsLabel);

                        // Spacer
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        userBox.getChildren().addAll(avatar, userInfo, spacer);
                        setGraphic(userBox);
                        setStyle("-fx-background-color: #1E1E1E; -fx-border-color: transparent;");

                    } catch (Exception e) {
                        System.out.println("Error creating user cell: " + e.getMessage());
                        setText("Error loading user");
                        setStyle("-fx-text-fill: red; -fx-background-color: #1E1E1E;");
                    }
                }
            }
        });
    }


    // Switch UI to show followers
    @FXML
    private void handleShowFollowers() {
        showingFollowers = true;
        updateButtonStyles();
        loadFollowers();
    }


    // Switch UI to show following
    @FXML
    private void handleShowFollowing() {
        showingFollowers = false;
        updateButtonStyles();
        loadFollowing();
    }

    private void updateButtonStyles() {
        if (showingFollowers) {
            showFollowersBtn.setStyle("-fx-background-color: #BB86FC; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 16; -fx-font-weight: bold;");
            showFollowingBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #BB86FC; -fx-border-color: #BB86FC; -fx-border-radius: 10; -fx-padding: 8 16; -fx-font-weight: bold;");
        } else {
            showFollowersBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #BB86FC; -fx-border-color: #BB86FC; -fx-border-radius: 10; -fx-padding: 8 16; -fx-font-weight: bold;");
            showFollowingBtn.setStyle("-fx-background-color: #BB86FC; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 16; -fx-font-weight: bold;");
        }
    }


    // Fetch followers for the viewed user
    private void loadFollowers() {
        try {
            if (viewedUser == null) return;

            ApiResponse<List<User>> response = userClient.getFollowers(viewedUser.getId());
            users.clear();
            if (response.isState() && response.getData() != null) {
                users.addAll(response.getData());
            }
        } catch (Exception e) {
            System.out.println("Error loading followers: " + e.getMessage());
        }
    }


    // Fetch users the viewed user is following
    private void loadFollowing() {
        try {
            if (viewedUser == null) return;

            ApiResponse<List<User>> response = userClient.getFollowing(viewedUser.getId());
            users.clear();
            if (response.isState() && response.getData() != null) {
                users.addAll(response.getData());
            }
        } catch (Exception e) {
            System.out.println("Error loading following: " + e.getMessage());
        }
    }



    // Return to home view
    @FXML
    private void handleGoBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/home-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println("Error going back to home: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Placeholder for edit profile action when viewing someone else's profile
    @FXML
    private void handleEditProfile() {
        System.out.println("Edit profile clicked - but you're viewing someone else's profile");
        // This should only be enabled if viewedUser == currentUser
    }

    // Open avatar 3D view for the viewed user
    @FXML
    private void handleViewAvatar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/avatar-view.fxml"));
            Scene scene = new Scene(loader.load());

            AvatarController avatarController = loader.getController();

            // Pass the currently viewed user (could be self or someone else)
            avatarController.setUser(viewedUser);

            Stage stage = (Stage) avatarContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to load avatar view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to create a clickable post thumbnail
    private Node createThumbnail(Post post) {
        StackPane wrapper = new StackPane();
        wrapper.setPrefSize(140, 140);
        wrapper.setStyle("-fx-background-color: #1E1E1E; -fx-border-color: #333333; -fx-border-radius: 8; -fx-cursor: hand;"); // Added -fx-cursor: hand;

        ImageView img = new ImageView();
        img.setFitWidth(140);
        img.setFitHeight(140);
        img.setPreserveRatio(false);

        String url = post.getImageUrl();
        if (url != null && !url.isEmpty()) {
            if (!url.startsWith("http")) {
                url = "http://localhost:7007/api/uploads/" + url;
            }
            img.setImage(new Image(url, true));
        } else {
            img.setImage(null);
            wrapper.setStyle("-fx-background-color: #BB86FC; -fx-border-color: #3700B3; -fx-border-radius: 8; -fx-cursor: hand;");
        }

        wrapper.getChildren().add(img);

        // --- NEW CLICK HANDLER ---
        wrapper.setOnMouseClicked(event -> handleViewPost(post, wrapper.getScene()));

        return wrapper;
    }

    // NEW: Method to handle post click and navigate to PostView
    private void handleViewPost(Post post, Scene currentScene) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/post-view.fxml"));
            Scene scene = new Scene(loader.load());

            Object controller = loader.getController();
            if (controller instanceof PostController) {
                ((PostController) controller).setPost(post);
            } else {
                System.err.println("Controller loaded is not PostViewController or setPost method is missing.");
                return;
            }

            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load post view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}