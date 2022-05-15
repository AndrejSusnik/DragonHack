package com.pins.pcapp;

import io.javalin.Javalin;

public class ServerController {
    private final int port = 9999;
    public void start() {
        Javalin app = Javalin.create().start("0.0.0.0",port);
        app.get("/", ctx -> ctx.result("Hello World"));
        app.get("/discovery", ctx -> ctx.result("{}"));

    }
}
