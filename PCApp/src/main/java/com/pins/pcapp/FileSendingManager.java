package com.pins.pcapp;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class FileSendingManager {

    public ImageView downloadIconImageView;


    public void fileDragDropped(DragEvent dragEvent) {
//        Dragboard db = dragEvent.getDragboard();
//        boolean success = false;
//        if (db.hasFiles()) {
//            success = true;
//            String filePath = null;
//            for (File file : db.getFiles()) {
//                filePath = file.getAbsolutePath();
//                System.out.println(filePath);
//            }
//        }
//        dragEvent.setDropCompleted(success);
//        dragEvent.consume();
    }

    public void backPressed(ActionEvent actionEvent) {
        try {
            this.switchSceneFromEvent(actionEvent, "hello-view.fxml");
        } catch (Exception e) {
            this.throwError("Unexpected error occurred.");
        }
    }

    private void throwError(String errorMsg) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("error-popup.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Napaka");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            javafx.scene.control.TextArea errorText = (javafx.scene.control.TextArea) scene.lookup("#errorTA");
            errorText.setText(errorMsg);

            scene.lookup("#errorPopupBTN").setOnMouseClicked(e -> {
                stage.close();
            });
        } catch (IOException e) {
            this.throwError("Unexpected error occurred.");
        }
    }

    private void switchSceneFromEvent(Event event, String sceneName) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(sceneName));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            this.throwError("Unexpected error occurred.");
        }
    }
}
