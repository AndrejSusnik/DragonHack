package com.pins.pcapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HelloApplication extends Application {

    private String token;
    public MiniDB miniDB;
    ServerController serverController;
    @Override
    public void start(Stage stage) throws IOException {
//        FileInputStream miniDBJsonInputStream = (FileInputStream) getClass().getResourceAsStream("miniDB.json");
//        miniDB = new MiniDB(miniDBJsonInputStream);
//        String serverUrl = "88.200.36.19";
//        miniDB.put("serverUrl", serverUrl);


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
    }

    public static void main(String[] args) {
        launch();
    }

    public void setToken(String token) {
        this.token = token;
    }
}