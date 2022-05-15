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
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class FileSendingManager {

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

    public VBox fileTransferProgressVBOX;


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
            stage.setTitle("Error");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            javafx.scene.control.TextArea errorText = (javafx.scene.control.TextArea) scene.lookup("#errorTA");
            errorText.setText(errorMsg);

            scene.lookup("#errorPopupBTN").setOnMouseClicked(e-> {
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

    public void downloadDragDropped(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        if (db.hasFiles()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("file-transfer-progress.fxml"));
            ObservableList<Node> children = fileTransferProgressVBOX.getChildren();
            db.getFiles().forEach(file -> {
                try {
                    HBox fileHBox = loader.load();
                    ((Label) fileHBox.lookup("#fileNameLabel")).setText(file.getName());
                    ProgressIndicator pi = (ProgressIndicator) fileHBox.lookup("#fileProgressIndicator");
                    ImageView imageView = (ImageView) fileHBox.lookup("#checkmark");
                    pi.setProgress(0);
                    fileHBox.setMaxWidth(Double.MAX_VALUE);
                    children.add(fileHBox);
                    Runnable t = () -> sendFile(file.getAbsolutePath(), pi, imageView);
                    Thread thread = new Thread(t);
                    thread.start();
                } catch (IOException e) {
                    this.throwError("Unexpected error occurred.");
                }
            });
            dragEvent.setDropCompleted(true);
        } else {
            dragEvent.setDropCompleted(false);
        }
    }

    public void downloadDragOver(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        if (db.hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.MOVE);
        }
    }

    public void sendFile(String filePath, ProgressIndicator pi, ImageView imageView) {
        File f = new File(filePath);
        MyFileBody fb = new MyFileBody(f);
        fb.setListener(new IStreamListener() {

            @Override
            public void counterChanged(int delta) {
                pi.setProgress(pi.getProgress() + delta / (double) fb.getFile().length());
            }
        });
        HttpEntity entity = MultipartEntityBuilder.create()
                .addPart("file", fb)
                .build();

        HttpPost request = new HttpPost("http://88.200.89.247:5000/v1/file");
        request.setEntity(entity);

        HttpClient client = HttpClientBuilder.create().build();
        try {
            HttpResponse response = client.execute(request);
            imageView.setVisible(true);
        } catch (Exception e) {


        }
        imageView.setVisible(true);
    }
}
