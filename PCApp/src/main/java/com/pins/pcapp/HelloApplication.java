package com.pins.pcapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private String token;
    ServerController serverController;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 550, 300);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        Image image = new Image(getClass().getResource("/icons/icon.png").toExternalForm());
        stage.getIcons().add(image);

        stage.show();
        stage.setResizable(false);
        serverController = new ServerController();
        serverController.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        serverController.stop();
    }

    public static void main(String[] args) {
        launch();
    }

    public void setToken(String token) {
        this.token = token;
    }
}