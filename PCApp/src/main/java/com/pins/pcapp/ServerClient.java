package com.pins.pcapp;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerClient {
    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    private static final CloseableHttpClient httpClient = HttpClients.custom().disableAutomaticRetries().build();
    public static String discover(String ip) {
        try {
            System.out.println(ip);
            HttpGet request = new HttpGet("http://" + ip + ":9999" + "/discovery");

            CloseableHttpResponse response = httpClient.execute(request);

            if(response.getStatusLine().getStatusCode() != 200){
                return null;
            }
            else{
                return convertStreamToString(response.getEntity().getContent());
            }

        } catch (Exception e) {
            return null;
        }

    }
}
