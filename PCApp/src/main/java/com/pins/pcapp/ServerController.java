package com.pins.pcapp;

import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class ServerController {
    private final int port = 9999;
    private Javalin app;
    public void start() {
       app = Javalin.create().start("0.0.0.0",port);
       app._conf.enableCorsForAllOrigins();
        app.get("/", ctx -> ctx.result("Hello World"));
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "computer");
        obj.addProperty("name", "test");

        app.get("/discovery", ctx -> ctx.result("test"));
        app.post("/file", ctx -> {
            List<UploadedFile> l =  ctx.uploadedFiles();
            for(UploadedFile f: l) {
                try (InputStream inputStream = f.getContent()) {
                    File localFile = new File("./assets/"+f.getFilename());
                    FileUtils.copyInputStreamToFile(inputStream, localFile);
                }catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });
    }

    public void stop() {
        if(app != null) {
            app.stop();
        }
    }
}
