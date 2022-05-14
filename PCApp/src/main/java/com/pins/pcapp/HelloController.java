package com.pins.pcapp;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class HelloController {

    public FlowPane devicesFlowPane;

    @FXML
    private void initialize() {
        addDeviceIcon("Device 1", "phone");
        addDeviceIcon("Device 2", "pc");
        addDeviceIcon("Device 3", "pc");
        addDeviceIcon("Device 4", "pc");
        addDeviceIcon("Device 5", "pc");
    }

    @FXML
    private void refreshButtonPressed() {
        System.out.println("Refresh button pressed");
    }

    private void addDeviceIcon( String deviceName, String deviceType ) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("deviceOption.fxml"));
        ObservableList<Node> children = devicesFlowPane.getChildren();
        try {
            BorderPane root = loader.load();
            ((Label) root.lookup("#deviceNameLabel")).setText(deviceName);
            if (deviceType.equals("pc")) {
                ((ImageView) root.lookup("#deviceIcon")).setImage(new Image(getClass().getResource("/icons/pc_icon.png").toString()));
            } else if (deviceType.equals("phone")) {
                ((ImageView) root.lookup("#deviceIcon")).setImage(new Image(getClass().getResource("/icons/smartphone_icon.png").toString()));
            }
            children.add(root);
            root.setOnMouseClicked(event -> {
                System.out.println("Clicked on " + deviceName);
                // implement device selection handler here
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}