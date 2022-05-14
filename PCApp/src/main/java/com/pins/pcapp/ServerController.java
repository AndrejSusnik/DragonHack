package com.pins.pcapp;

import io.javalin.Javalin;

public class ServerController {
    private record DeviceInfo (String deviceName, String deviceType, String passphrase, String macAddress){};
    private final int port = 9999;
    public void start() {
//        Javalin app = Javalin.create().start(9999);
//        app.get("/", ctx -> ctx.result("Hello World"));
//        app.get("/discovery", ctx -> ctx.result("{}"));
    }

}
