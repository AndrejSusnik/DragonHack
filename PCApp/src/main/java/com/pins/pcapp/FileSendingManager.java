package com.pins.pcapp;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class FileSendingManager {

    public ImageView downloadIconImageView;
    public VBox fileTransferProgressVBOX;

    @FXML
    private void initialize() {
        downloadIconImageView.setOnDragOver(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            if (db.hasFiles()) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
        });

        downloadIconImageView.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            if (db.hasFiles()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("file-transfer-progress.fxml"));
                ObservableList<Node> children = fileTransferProgressVBOX.getChildren();
                db.getFiles().forEach(file -> {
                    try {
                        HBox fileHBox = loader.load();
                        ((Label) fileHBox.lookup("#fileNameLabel")).setText(file.getName());
                        ProgressIndicator pi = (ProgressIndicator) fileHBox.lookup("#fileProgressIndicator");
                        pi.setProgress(0);
                        fileHBox.setMaxWidth(Double.MAX_VALUE);
                        children.add(fileHBox);
                    } catch (IOException e) {
                        this.throwError("Unexpected error occurred.");
                    }
                });
                dragEvent.setDropCompleted(true);
            } else {
                dragEvent.setDropCompleted(false);
            }
        });
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
