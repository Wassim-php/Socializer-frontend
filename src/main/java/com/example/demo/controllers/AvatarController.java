package com.example.demo.controllers;

import com.example.demo.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class AvatarController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button closeButton;

    private final Group worldRoot = new Group();
    private Group avatarModel = new Group();
    private SubScene subScene;
    private final PerspectiveCamera camera = new PerspectiveCamera(true);

    private double mouseX;
    private double mouseY;
    private User user;

    private Text followerCountText = new Text("0 Followers"); // Component to hold the 3D text


    public void setUser(User user) {
        this.user = user;

        worldRoot.getChildren().remove(avatarModel);

        avatarModel = createBasicAvatar();
        worldRoot.getChildren().add(avatarModel);

        // Update the follower count display
        updateFollowerDisplay();

        if (subScene != null) {
            addInteractionHandlers(subScene);
        }
    }

    @FXML
    public void initialize() {
        setupCamera();
        setupLighting();
        setupSubScene();
    }


    private void setupCamera() {
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-1000);
        camera.setTranslateY(-100);
    }


    private void setupLighting() {
        AmbientLight ambient = new AmbientLight(Color.web("#3700B3", 0.3));
        worldRoot.getChildren().add(ambient);

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(-250);
        pointLight.setTranslateY(-50);
        pointLight.setTranslateZ(-300);
        worldRoot.getChildren().add(pointLight);
    }

    private Group createBasicAvatar() {
        String userColorHex = (user != null && user.getAvatarColorHex() != null)
                ? user.getAvatarColorHex()
                : "#BB86FC";

        PhongMaterial primaryMaterial = new PhongMaterial(Color.web(userColorHex));
        PhongMaterial limbMaterial = new PhongMaterial(Color.web("#CCCCCC"));

        // CORE COMPONENTS
        Sphere headSphere = new Sphere(50);
        headSphere.setMaterial(primaryMaterial);

        // Wrap the head sphere in a Group to allow for centered rotation (Gaze Tracking)
        Group headGroup = new Group(headSphere);
        headGroup.setTranslateY(-100);

        double bodyWidth = 100;
        double bodyHeight = 200;
        double bodyDepth = 50;
        Box body = new Box(bodyWidth, bodyHeight, bodyDepth);
        body.setMaterial(primaryMaterial);
        body.setTranslateY(50);

        // LIMBS CREATION
        double armLength = 100;
        double armThickness = 20;

        Box rightArm = new Box(armLength, armThickness, armThickness);
        rightArm.setMaterial(limbMaterial);
        rightArm.setTranslateX(bodyWidth / 2 + armLength / 2);
        rightArm.setTranslateY(10);

        Box leftArm = new Box(armLength, armThickness, armThickness);
        leftArm.setMaterial(limbMaterial);
        leftArm.setTranslateX(-(bodyWidth / 2 + armLength / 2));
        leftArm.setTranslateY(10);

        double legWidth = 30;
        double legHeight = 150;

        Box rightLeg = new Box(legWidth, legHeight, legWidth);
        rightLeg.setMaterial(limbMaterial);
        rightLeg.setTranslateX(bodyWidth / 4);
        rightLeg.setTranslateY(50 + bodyHeight / 2 + legHeight / 2);

        Box leftLeg = new Box(legWidth, legHeight, legWidth);
        leftLeg.setMaterial(limbMaterial);
        leftLeg.setTranslateX(-(bodyWidth / 4));
        leftLeg.setTranslateY(50 + bodyHeight / 2 + legHeight / 2);

        // Use headGroup in the main avatar Group
        Group avatar = new Group(headGroup, body,
                rightArm, leftArm,
                rightLeg, leftLeg,
                followerCountText); // ADD TEXT TO THE AVATAR GROUP

        avatar.setTranslateY(-100);

        return avatar;
    }


    private void setupSubScene() {
        subScene = new SubScene(worldRoot, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.web("#121212"));
        subScene.setCamera(camera);

        subScene.widthProperty().bind(rootPane.widthProperty());
        subScene.heightProperty().bind(rootPane.heightProperty());

        rootPane.getChildren().add(0, subScene);
    }

    private void updateFollowerDisplay() {
        if (user != null) {
            String count = (user.getFollowersCount() > 0) ? String.valueOf(user.getFollowersCount()) : "0";

            followerCountText.setText(count + " Followers");
            followerCountText.setFont(Font.font("Arial", FontWeight.BOLD, 40));
            followerCountText.setFill(Color.web("#03DAC6")); // Bright cyan color for visualization

            // Position the text slightly above the head sphere (radius 50)
            followerCountText.setTranslateY(-200);

            // Ensures the text is always perpendicular to the camera to be readable
            followerCountText.getTransforms().add(new Rotate(180, Rotate.Y_AXIS));
        }
    }

    private void addInteractionHandlers(SubScene subScene) {

        // 1. Setup Avatar Drag Rotation
        avatarModel.getTransforms().clear();

        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        avatarModel.getTransforms().addAll(rotateX, rotateY);

        final double ROTATION_SPEED = 0.5;

        subScene.setOnMousePressed(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        subScene.setOnMouseDragged(event -> {
            double dx = event.getSceneX() - mouseX;
            double dy = event.getSceneY() - mouseY;

            rotateY.setAngle(rotateY.getAngle() - (dx * ROTATION_SPEED));

            double newAngleX = rotateX.getAngle() + (dy * ROTATION_SPEED);
            rotateX.setAngle(Math.min(Math.max(newAngleX, -90), 90));

            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        // ----------------------------------------------------
        // 2. Setup Gaze Tracking (Look-At Feature)

        // Head Group is the first element in avatarModel's children (before the body)
        Group headGroup = (Group) avatarModel.getChildren().get(0);

        // Rotations are centered at the neck pivot point (Y=100 relative to headGroup's translation)
        Rotate gazeRotateX = new Rotate(0, 0, 100, 0, Rotate.X_AXIS);
        Rotate gazeRotateY = new Rotate(0, 0, 100, 0, Rotate.Y_AXIS);

        headGroup.getTransforms().addAll(gazeRotateX, gazeRotateY);

        subScene.setOnMouseMoved(event -> {
            // Get mouse position relative to the center of the SubScene
            double centerX = subScene.getWidth() / 2;
            double centerY = subScene.getHeight() / 2;

            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();

            // Calculate offsets
            double dx = mouseX - centerX;
            double dy = mouseY - centerY;

            // Map movement to small angles, clamping the value
            final double MAX_GAZE_ANGLE = 15.0; // Max degrees the head can turn
            final double SCALE_FACTOR = 0.1; // Sensitivity

            // X rotation (tilting up/down)
            double rotationX = Math.min(Math.max(-dy * SCALE_FACTOR, -MAX_GAZE_ANGLE), MAX_GAZE_ANGLE);

            // Y rotation (turning left/right)
            double rotationY = Math.min(Math.max(dx * SCALE_FACTOR, -MAX_GAZE_ANGLE), MAX_GAZE_ANGLE);

            // Apply the rotation
            gazeRotateY.setAngle(rotationY);
            gazeRotateX.setAngle(rotationX);
        });
        // ----------------------------------------------------
    }



    @FXML
    private void handleClose() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/profile-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to return to profile view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}