import 'package:flutter/material.dart';
import 'dart:io';
import 'dragon_drop.dart';
import 'main.dart';

class _HomeState extends State<Home> {
  String statusText = "Start Server";
  startServer() async {
    setState(() {
      statusText = "Starting server on Port : 8080";
    });
    // var chain =
    //     Platform.script.resolve('certificates/server_chain.pem').toFilePath();
    // var key =
    //     Platform.script.resolve('certificates/server_key.pem').toFilePath();
    // var context = SecurityContext()
    //   ..useCertificateChain(chain)
    //   ..usePrivateKey(key, password: 'dartdart');
    var server = await HttpServer.bind("0.0.0.0", 9999);
    print("Server running on IP : " +
        server.address.toString() +
        " On Port : " +
        server.port.toString());
    await for (var request in server) {
      request.response
        ..headers.contentType =
            new ContentType("text", "plain", charset: "utf-8")
        ..write('Hello, world')
        ..close();
    }
    setState(() {
      statusText = "Server running on IP : " +
          server.address.toString() +
          " On Port : " +
          server.port.toString();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: <Widget>[
          RaisedButton(
            onPressed: () {
              startServer();
            },
            child: Text(statusText),
          )
        ],
      ),
    ));
  }
}
