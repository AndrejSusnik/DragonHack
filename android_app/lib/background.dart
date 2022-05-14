import 'dart:io';
import 'dart:convert';
import 'package:flutter/services.dart';

import 'package:flutter/material.dart';
import 'package:flutter_background/flutter_background.dart';
import 'package:receive_intent/receive_intent.dart';

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  // static const platform = MethodChannel('files.dragondrop.io/files');
  late AndroidIntent _initialIntent;
  String statusText = "Start Server";
  String textContent = "SERET TEXT";
  int count = 0;

  @override
  void initState() {
    super.initState();
    _init();
  }

  Future<void> _init() async {
    final receivedIntent = await ReceiveAndroidIntent.getInitialIntent();

    if (!mounted) return;

    setState(() {
      count++;
      _initialIntent = receivedIntent!;
      debugPrint("INITIAL INTENT: $_initialIntent");
    });
  }

  startServer() async {
    // platform.invokeMethod("getSharedText").then((value) => setState(() => textContent = value)).onError((error, stackTrace) =>
    //   debugPrint("FAILED EVERYTHING"));
    setState(() {
      statusText = "Starting server on Port : 8080";
    });

    final androidConfig = FlutterBackgroundAndroidConfig(
      notificationTitle: "flutter_background example app",
      notificationText:
          "Background notification for keeping the example app running in the background",
      notificationImportance: AndroidNotificationImportance.Default,
      notificationIcon: AndroidResource(
          name: 'background_icon',
          defType: 'drawable'), // Default is ic_launcher from folder mipmap
    );
    bool success =
        await FlutterBackground.initialize(androidConfig: androidConfig);

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
      if (request.uri.path == '/discovery' && request.method == 'GET') {
        Map<String, String> DISCOVERY_FRAME = {
          "device_name": Platform.isAndroid ? "Android Phone" : "iPhone",
          "device_type": "phone",
          "passphrase": "admin"
        };
        request.response
          ..headers.contentType =
              new ContentType("text", "html", charset: "utf-8")
          ..write(json.encode(DISCOVERY_FRAME))
          ..close();
      } else {
        request.response
          ..headers.contentType =
              new ContentType("text", "html", charset: "utf-8")
          ..write("Not implemented :)!")
          ..close();
      }
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
          ),
          Text(textContent + "LMAO"),
          Text("COUNT: $count"),
        ],
      ),
    ));
  }
}
