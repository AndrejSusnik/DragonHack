package com.pins.pcapp;

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
        app.get("/discovery", ctx -> ctx.result("{'type': 'computer'}"));
        app.post("/file", ctx -> {
            List<UploadedFile> l =  ctx.uploadedFiles();
            for(UploadedFile f: l) {
                try (InputStream inputStream = f.getContent()) {
                    File localFile = new File("~/Desktop/"+f.getFilename());
                    FileUtils.copyInputStreamToFile(inputStream, localFile);
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
