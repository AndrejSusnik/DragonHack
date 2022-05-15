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

import java.io.File;
import java.io.OutputStream;
import java.util.regex.Pattern;

import javafx.scene.layout.HBox;

import java.io.IOException;

import javafx.stage.Stage;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class HelloController {
    private interface IStreamListener {

        void counterChanged(int delta);

    }
    private class MyFileBody extends FileBody {

        private IStreamListener listener;

        public MyFileBody(File file) {
            super(file);
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            CountingOutputStream output = new CountingOutputStream(out) {
                @Override
                protected void beforeWrite(int n) {
                    if (listener != null && n != 0)
                        listener.counterChanged(n);
                    super.beforeWrite(n);
                }
            };
            super.writeTo(output);

        }

        public void setListener(IStreamListener listener) {
            this.listener = listener;
        }

        public IStreamListener getListener() {
            return listener;
        }

    }
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
        File f = new File("/home/asusnik/Downloads/Scand_20220513_115902.pdf");
        MyFileBody fb = new MyFileBody(f);
        fb.setListener(new IStreamListener(){

            @Override
            public void counterChanged(int delta) {
                // do something
                System.out.println(delta);
            }});
        HttpEntity entity = MultipartEntityBuilder.create()
                .addPart("file", fb)
                .build();

        HttpPost request = new HttpPost("http://88.200.37.140:5000/v1/file");
        request.setEntity(entity);

        HttpClient client = HttpClientBuilder.create().build();
        try{
            HttpResponse response = client.execute(request);}
        catch (Exception e ){

        }
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
//            this.throwError("Unexpected error occurred.");
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
//            this.throwError("Unexpected error occurred.");
        }
    }
}