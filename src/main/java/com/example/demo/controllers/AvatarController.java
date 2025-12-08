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

    
    // Set the user whose avatar will be shown and rebuild the model
    public void setUser(User user) {
        this.user = user;

        worldRoot.getChildren().remove(avatarModel);

        avatarModel = createBasicAvatar();
        worldRoot.getChildren().add(avatarModel);

        if (subScene != null) {
            addInteractionHandlers(subScene);
        }
    }

    @FXML
    // Initialize 3D scene, camera and lighting
    public void initialize() {
        setupCamera();
        setupLighting();
        setupSubScene();
    }

   
    // Configure perspective camera for the 3D scene
    private void setupCamera() {
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-1000);
        camera.setTranslateY(-100);
    }

   
    // Add ambient and point lighting to the 3D scene
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
    // Create a simple 3D avatar composed of basic shapes
        String userColorHex = (user != null && user.getAvatarColorHex() != null)
                ? user.getAvatarColorHex()
                : "#BB86FC";

        PhongMaterial primaryMaterial = new PhongMaterial(Color.web(userColorHex));
        PhongMaterial limbMaterial = new PhongMaterial(Color.web("#CCCCCC"));

        // CORE COMPONENTS
        Sphere head = new Sphere(50);
        head.setMaterial(primaryMaterial);
        head.setTranslateY(-100);

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

        Group avatar = new Group(head, body,
                rightArm, leftArm,
                rightLeg, leftLeg);

        avatar.setTranslateY(-100);

        return avatar;
    }

    
    // Create and attach SubScene containing the 3D world
    private void setupSubScene() {
        subScene = new SubScene(worldRoot, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.web("#121212"));
        subScene.setCamera(camera);

        subScene.widthProperty().bind(rootPane.widthProperty());
        subScene.heightProperty().bind(rootPane.heightProperty());

        rootPane.getChildren().add(0, subScene);
    }

   
    // Add mouse drag handlers to rotate the avatar model
    private void addInteractionHandlers(SubScene subScene) {
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
    }


    
    // Close avatar view and return to profile
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