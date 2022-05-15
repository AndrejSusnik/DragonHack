package com.pins.pcapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class SwitchUser {

    public PasswordField passwordField;
    public TextField usernameField;
    public String serverUrl = HelloApplication.serverUrl;
    public static Pattern p = Pattern.compile("88.200.\\d*.\\d*");

    public static String getIp() {

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (p.matcher(i.toString().substring(1)).find()) {
                        return i.toString();
                    }
                    System.out.println(i.getHostAddress());
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public void loginButtonPressed(ActionEvent actionEvent) {
        HttpResponse rawResponse = sendUsernamePassword("POST");
        if (rawResponse == null) {
            return;
        }

        String status = rawResponse.getStatusLine().toString();
        if (status.equals("HTTP/1.1 200 OK")) {
            String token = getTokenFromRawResponse(rawResponse);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", 0);
            jsonObject.addProperty("name", "Desktop computer");
            jsonObject.addProperty("type", "computer");
            jsonObject.addProperty("ip", getIp());
            jsonObject.addProperty("id_user", 2);

            HttpClient httpclient = HttpClientBuilder.create().build();
            StringEntity requestEntity = new StringEntity(
                    jsonObject.toString(),
                    ContentType.APPLICATION_JSON);

            HttpPost postMethod = new HttpPost("http://" + serverUrl + ":5000/v1/device");
            postMethod.setEntity(requestEntity);
            try {
                HttpResponse resp = httpclient.execute(postMethod);
            } catch (Exception e) {
            }
        } else {
            // put code for invalid login
        }
        this.switchSceneFromEvent(actionEvent, "hello-view.fxml");

    }

    public void registerButtonPressed(ActionEvent actionEvent) {
        HttpResponse rawResponse = sendUsernamePassword("PUT");
        if (rawResponse == null) {
            return;
        }
    }

    private String getTokenFromRawResponse(HttpResponse rawResponse) {
        try {
            InputStreamReader reader = new InputStreamReader(rawResponse.getEntity().getContent());
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpResponse sendUsernamePassword(String method) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        HttpClient httpclient = HttpClientBuilder.create().build();
        StringEntity requestEntity = new StringEntity(
                jsonObject.toString(),
                ContentType.APPLICATION_JSON);

        try {
            if (method.equals("POST")) {
                HttpPost postMethod = new HttpPost("http://" + serverUrl + ":5000/v1/auth");
                postMethod.setEntity(requestEntity);
                HttpResponse rawResponse = httpclient.execute(postMethod);
                return rawResponse;
            } else if (method.equals("PUT")) {
                HttpPut putMethod = new HttpPut("http://" + serverUrl + ":5000/v1/auth");
                putMethod.setEntity(requestEntity);
                HttpResponse rawResponse = httpclient.execute(putMethod);
                return rawResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
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
}
