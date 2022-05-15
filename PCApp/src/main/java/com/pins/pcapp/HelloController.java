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
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import java.util.regex.Pattern;

import java.io.IOException;

import javafx.stage.Stage;

public class HelloController {
    public FlowPane devicesFlowPane;
    private static Pattern p = Pattern.compile("\\d*.\\d*.\\d*.\\d]");

    @FXML
    public void initialize() {
        addDeviceIcon("Device 1", "phone");
        addDeviceIcon("Device 2", "pc");
        addDeviceIcon("Device 3", "phone");
        addDeviceIcon("Device 4", "phone");
        addDeviceIcon("Device 5", "pc");
    }

    @FXML
    public void refreshButtonPressed() {
        System.out.println("Refresh button pressed");
    }

    private void addDeviceIcon(String deviceName, String deviceType) {
        FXMLLoader loaderDevices = new FXMLLoader(getClass().getResource("device-option.fxml"));
        ObservableList<Node> children = devicesFlowPane.getChildren();
        try {
            BorderPane rootLoaderDevices = loaderDevices.load();
            ((Label) rootLoaderDevices.lookup("#deviceNameLabel")).setText(deviceName);
            if (deviceType.equals("pc")) {
                ((ImageView) rootLoaderDevices.lookup("#deviceIcon")).setImage(new Image(getClass().getResource("/icons/pc_icon.png").toString()));
            } else if (deviceType.equals("phone")) {
                ((ImageView) rootLoaderDevices.lookup("#deviceIcon")).setImage(new Image(getClass().getResource("/icons/smartphone_icon.png").toString()));
            }
            children.add(rootLoaderDevices);
            rootLoaderDevices.setOnMouseClicked(event -> {
                System.out.println("Clicked on " + deviceName);
                // implement device selection handler here
                this.switchSceneFromEvent(event, "file-sending-manager.fxml");
            });
        } catch (IOException e) {
            e.printStackTrace();
//            this.throwError("Unexpected error occurred.");
        }
    }

    private void throwError(String errorMsg) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("error-popup.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Error");
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
            e.printStackTrace();
        }
    }

    public void switchUserButtonPressed(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("switch-user.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}