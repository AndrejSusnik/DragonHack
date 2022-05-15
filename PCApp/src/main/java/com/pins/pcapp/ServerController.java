package com.pins.pcapp;

import io.javalin.Javalin;

public class ServerController {
    private final int port = 9999;
    Javalin app;
    public void start() {
//        app = Javalin.create().start(port);
//        app.get("/discovery", ctx -> ctx.result("{}"));
//        app.post("/files", ctx -> {
//
//        });
    }

    public void stop() {
        if(app != null) {
            app.stop();
        }
    }

}
