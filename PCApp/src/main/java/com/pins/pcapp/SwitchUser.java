package com.pins.pcapp;

import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

public class SwitchUser {

    public PasswordField passwordField;
    public TextField usernameField;

    public void loginButtonPressed(ActionEvent actionEvent) {
        HttpResponse rawResponse = sendUsernamePassword("POST");
        if (rawResponse == null) {
            return;
        }
        String token = getTokenFromRawResponse(rawResponse);

    }

    public void registerButtonPressed(ActionEvent actionEvent) {
        HttpResponse rawResponse = sendUsernamePassword("PUT");
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
                HttpPost postMethod = new HttpPost("http://88.200.89.137:5000/v1/auth");
                postMethod.setEntity(requestEntity);
                HttpResponse rawResponse = httpclient.execute(postMethod);
                return rawResponse;
            } else if (method.equals("PUT")) {
                HttpPut putMethod = new HttpPut("http://88.200.89.137:5000/v1/auth");
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
}
