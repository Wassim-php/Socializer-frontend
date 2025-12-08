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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.util.List;

public class ProfileController {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label followersCountLabel;
    @FXML
    private Label followingCountLabel;
    @FXML
    private Label postCountLabel;
    @FXML
    private FlowPane gridPane;
    @FXML
    private ListView<User> usersListView;
    @FXML
    private Button showFollowersBtn;
    @FXML
    private Button showFollowingBtn;
    @FXML
    private StackPane avatarDisplay;
    @FXML
    private Label avatarInitialLabel;

    private final PostClient postClient = new PostClient();
    private final UserClient userClient = new UserClient();
    private User currentUser;
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private boolean showingFollowers = true;

    @FXML
    public void initialize() {
    // Initialize profile view and load user data
        System.out.println("DEBUG: ProfileController initialized");
        System.out.println("DEBUG: showFollowersBtn is null: " + (showFollowersBtn == null));
        System.out.println("DEBUG: showFollowingBtn is null: " + (showFollowingBtn == null));
        System.out.println("DEBUG: usersListView is null: " + (usersListView == null));

        setupUsersListView();

        loadUserInfo();

        PauseTransition pause = new PauseTransition(Duration.millis(300));
        pause.setOnFinished(e -> {
            System.out.println("DEBUG: Current user loaded: " + (currentUser != null ? currentUser.getUserName() : "null"));
            handleShowFollowers();
            loadUserPosts();
        });
        pause.play();
    }

    public void refreshProfileData() {
    // Refresh profile information and posts
        loadUserInfo();
        loadUserPosts();
        if (showingFollowers) {
            loadFollowers();
        } else {
            loadFollowing();
        }
    }

    private void loadUserInfo() {
    // Load current user information from backend
        try {
            ApiResponse<User> response = userClient.getCurrentUser();
            if (response.isState() && response.getData() != null) {
                currentUser = response.getData();
                usernameLabel.setText("@" + currentUser.getUserName());

                updateFollowCounts();
                updateAvatarDisplay();

                System.out.println("DEBUG: User info loaded successfully");
                System.out.println("DEBUG: Followers: " + currentUser.getFollowersCount());
                System.out.println("DEBUG: Following: " + currentUser.getFollowingCount());
            } else {
                usernameLabel.setText("Error loading user");
                System.out.println("DEBUG: Failed to load user info");
            }
        } catch (Exception e) {
            System.out.println("Error loading user info: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateAvatarDisplay() {
    // Update the avatar color and initial in UI
        if (currentUser == null || avatarDisplay == null) return;

        String hex = currentUser.getAvatarColorHex();
        if (hex == null || hex.isEmpty()) {
            hex = "#BB86FC";
        }

        Color avatarColor;
        try {
            avatarColor = Color.web(hex);
        } catch (IllegalArgumentException e) {
            avatarColor = Color.web("#BB86FC");
        }

        avatarDisplay.setStyle("-fx-background-color: " + hex + "; -fx-background-radius: 50;");

        if (avatarInitialLabel != null && currentUser.getUserName() != null && !currentUser.getUserName().isEmpty()) {
            avatarInitialLabel.setText(currentUser.getUserName().substring(0, 1).toUpperCase());
            avatarInitialLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 24;");
        }
    }

    private void updateFollowCounts() {
    // Update follower/following count labels
        if (currentUser != null) {
            followersCountLabel.setText(currentUser.getFollowersCount() + " followers");
            followingCountLabel.setText(currentUser.getFollowingCount() + " following");
        }
    }

    private void setupUsersListView() {
    // Configure the users list view appearance and cell factory
        usersListView.setItems(users);

        usersListView.setFixedCellSize(70);

        usersListView.setStyle("-fx-background-color: #1E1E1E; " +
                "-fx-border-color: #333; " +
                "-fx-border-radius: 8; " +
                "-fx-control-inner-background: #1E1E1E;" +
                "-fx-background-insets: 0;" +
                "-fx-padding: 0;");

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

                        StackPane avatar = new StackPane();
                        avatar.setPrefSize(40, 40);

                        String userHex = user.getAvatarColorHex() != null ? user.getAvatarColorHex() : "#BB86FC";
                        avatar.setStyle("-fx-background-color: " + userHex + "; -fx-background-radius: 20;");

                        if (user.getUserName() != null && !user.getUserName().isEmpty()) {
                            Label avatarLabel = new Label(user.getUserName().substring(0, 1).toUpperCase());
                            avatarLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                            avatar.getChildren().add(avatarLabel);
                        }

                        VBox userInfo = new VBox(2);
                        Label usernameLabel = new Label("@" + user.getUserName());
                        usernameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");

                        Label statsLabel = new Label(user.getFollowersCount() + " followers • " + user.getFollowingCount() + " following");
                        statsLabel.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 11px;");

                        userInfo.getChildren().addAll(usernameLabel, statsLabel);

                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        Button actionButton = new Button();

                        if (currentUser != null && user.getId().equals(currentUser.getId())) {
                            actionButton.setText("You");
                            actionButton.setDisable(true);
                            actionButton.setStyle("-fx-background-color: #666; -fx-text-fill: #AAA; -fx-background-radius: 15; -fx-padding: 5 15; -fx-font-size: 12;");
                        } else {
                            actionButton.setText("Follow");
                            actionButton.setStyle("-fx-background-color: #BB86FC; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 5 15; -fx-font-size: 12;");
                            actionButton.setOnAction(e -> handleFollowUser(user, actionButton));
                        }

                        userBox.getChildren().addAll(avatar, userInfo, spacer, actionButton);
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

    @FXML
    private void handleShowFollowers() {
    // Show followers list
        showingFollowers = true;
        updateButtonStyles();
        loadFollowers();
    }

    @FXML
    private void handleShowFollowing() {
    // Show following list
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

    private void loadFollowers() {
    // Load followers from backend
        try {
            if (currentUser == null) {
                System.out.println("DEBUG: Cannot load followers - currentUser is null");
                return;
            }

            System.out.println("DEBUG: Loading followers for user ID: " + currentUser.getId());
            ApiResponse<List<User>> response = userClient.getFollowers(currentUser.getId());
            users.clear();
            if (response.isState() && response.getData() != null) {
                users.addAll(response.getData());
                System.out.println("DEBUG: Loaded " + users.size() + " followers");
                usersListView.refresh();
            } else {
                System.out.println("DEBUG: No followers or API error");
            }
        } catch (Exception e) {
            System.out.println("Error loading followers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFollowing() {
    // Load following users from backend
        try {
            if (currentUser == null) {
                System.out.println("DEBUG: Cannot load following - currentUser is null");
                return;
            }

            System.out.println("DEBUG: Loading following for user ID: " + currentUser.getId());
            ApiResponse<List<User>> response = userClient.getFollowing(currentUser.getId());
            users.clear();
            if (response.isState() && response.getData() != null) {
                users.addAll(response.getData());
                System.out.println("DEBUG: Loaded " + users.size() + " following");
                usersListView.refresh();
            } else {
                System.out.println("DEBUG: No following or API error");
            }
        } catch (Exception e) {
            System.out.println("Error loading following: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleFollowUser(User user, Button button) {
    // Toggle follow/unfollow for a user and update UI
        try {
            if (user == null) return;

            String currentText = button.getText();

            if ("Follow".equals(currentText)) {
                ApiResponse<Void> response = userClient.followUser(user.getId());
                if (response.isState()) {
                    button.setText("Unfollow");
                    button.setStyle("-fx-background-color: #CF6679; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 5 15; -fx-font-size: 12;");
                    System.out.println("Successfully followed user: " + user.getUserName());

                    refreshCurrentUser();
                } else {
                    System.out.println("Failed to follow user: " + response.getMessage());
                }
            } else {
                ApiResponse<Void> response = userClient.unfollowUser(user.getId());
                if (response.isState()) {
                    button.setText("Follow");
                    button.setStyle("-fx-background-color: #BB86FC; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 5 15; -fx-font-size: 12;");
                    System.out.println("Successfully unfollowed user: " + user.getUserName());

                    refreshCurrentUser();
                } else {
                    System.out.println("Failed to unfollow user: " + response.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("Error following/unfollowing user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshCurrentUser() {
    // Refresh current user data and UI elements
        try {
            ApiResponse<User> response = userClient.getCurrentUser();
            if (response.isState() && response.getData() != null) {
                currentUser = response.getData();
                updateFollowCounts();
                updateAvatarDisplay();
                if (showingFollowers) {
                    loadFollowers();
                } else {
                    loadFollowing();
                }
            }
        } catch (Exception e) {
            System.out.println("Error refreshing user data: " + e.getMessage());
        }
    }

    private void loadUserPosts() {
    // Load posts for the profile user and populate the UI grid
        try {
            if (currentUser == null) return;

            ApiResponse<List<Post>> response = postClient.getAllByUserId(currentUser.getId());
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

    @FXML
    private void handleCreatePost() {
    // Navigate to the post creation view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/post-creation.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println("Error opening post creation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoBack() {
    // Return to home view
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

    @FXML
    private void handleEditProfile() {
    // Open edit profile view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/edit-profile.fxml"));
            Scene scene = new Scene(loader.load());

            EditProfileController controller = loader.getController();
            controller.setUser(currentUser);

            // CRITICAL: We pass the ProfileController instance so EditProfileController can refresh it.
            if (controller instanceof EditProfileController) {
                ((EditProfileController) controller).setProfileController(this);
            }

            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println("Error opening edit profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Node createThumbnail(Post post) {
    // Create a small clickable thumbnail for a post
        StackPane wrapper = new StackPane();
        wrapper.setPrefSize(140, 140);
        wrapper.setStyle("-fx-background-color: #1E1E1E; -fx-border-color: #333333; -fx-border-radius: 8; -fx-cursor: hand;");

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

        wrapper.setOnMouseEntered(e -> {
            wrapper.setStyle(wrapper.getStyle() + " -fx-effect: dropshadow(gaussian, #BB86FC, 10, 0.5, 0, 0);");
        });

        wrapper.setOnMouseExited(e -> {
            wrapper.setStyle(wrapper.getStyle().replace(" -fx-effect: dropshadow(gaussian, #BB86FC, 10, 0.5, 0, 0);", ""));
        });

        wrapper.setOnMouseClicked(e -> openPostEditor(post));

        return wrapper;
    }

    private void openPostEditor(Post post) {
    // Open the editor for the selected post
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/post-editor.fxml"));
            Scene scene = new Scene(loader.load());

            PostEditorController controller = loader.getController();
            controller.setPost(post);

            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleOpenAvatar() {
    // Open the 3D avatar view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/avatar-view.fxml"));
            Scene scene = new Scene(loader.load());

            AvatarController controller = loader.getController();

            if (currentUser != null) {
                controller.setUser(currentUser);
            } else {
                System.err.println("Warning: currentUser is null. AvatarController will use default colors.");
            }

            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println("Error opening 3D avatar view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}