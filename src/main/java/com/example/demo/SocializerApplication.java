package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SocializerApplication extends Application {
    @Override
    /**
     * Start the JavaFX application and show the login scene.
     */
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/com/example/demo/css/style.css").toExternalForm());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

}
