package com.pins.pcapp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;

public class MiniDB {
    private HashMap<String, Object> dict;

    public MiniDB(FileInputStream jsonFile) {
        JsonElement json = JsonParser.parseReader( new InputStreamReader(jsonFile, StandardCharsets.UTF_8) );
        dict = new HashMap<>();
    }

    public void put(String key, Object value) {
        if (dict.containsKey(key)) {
            dict.replace(key, value);
        } else {
            dict.put(key, value);
        }
    }

    public void remove(String key) {
        if (dict.containsKey(key)) {
            dict.remove(key);
        }
    }

    public Object get(String key) {
        if (dict.containsKey(key)) {
            return dict.get(key);
        }
        return null;
    }
}
