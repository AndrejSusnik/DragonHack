package com.pins.pcapp;

import io.javalin.Javalin;

public class ServerController {
    public void start() {
        Javalin app = Javalin.create().start(7070);
        app.get("/", ctx -> ctx.result("Hello World"));
    }

}
