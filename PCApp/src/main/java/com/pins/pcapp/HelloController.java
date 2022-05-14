package com.pins.pcapp;

import javafx.collections.ObservableList;
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
import javafx.scene.layout.HBox;

import java.io.IOException;
import javafx.stage.Stage;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
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
        Runnable task = () -> {
            try{Enumeration e = NetworkInterface.getNetworkInterfaces();
                while(e.hasMoreElements())
                {
                    NetworkInterface n = (NetworkInterface) e.nextElement();
                    java.util.List<java.net.InterfaceAddress> addresses = n.getInterfaceAddresses();
                    for(java.net.InterfaceAddress a : addresses) {
                        if(p.matcher(a.toString()).find()){
                            final Network net = new Network(a.getAddress(), a.getNetworkPrefixLength());
                            System.out.println(net);
                            for(String el : net.getAllAddresses())
                            {
                               String response = ServerClient.discover(el);
                               if(response != null) {
                                   System.out.println(response);
                               }
                            }
                        }
                    }
                }
            }
            catch (java.net.SocketException e){}

        };
        Thread thread = new Thread(task);
        thread.start();
        System.out.println("Refresh button pressed");
    }

    private void addDeviceIcon( String deviceName, String deviceType ) {
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
                FXMLLoader loaderFileManager = new FXMLLoader(getClass().getResource("file-sending-manager.fxml"));
                this.switchSceneFromEvent(event, "file-sending-manager.fxml");
            });
        } catch (IOException e) {
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