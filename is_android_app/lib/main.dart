import 'package:flutter/material.dart';
import 'dart:io';
import 'package:file_picker/file_picker.dart';
import 'dart:convert';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'api/api.dart';
import 'home_page.dart';
// import 'package:open_file/open_file.dart';
// import 'background.dart';

final storage = FlutterSecureStorage();

void main() {
  runApp(MaterialApp(
    title: "DragonDrop",
    home: Home(),
  ));
}

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  @override
  void initState() {
    super.initState();
    startServer();
  }

  startServer() async {
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
  }

  void _pickFile() async {
    // opens storage to pick files and the picked file or files
    // are assigned into result and if no file is chosen result is null.
    // you can also toggle "allowMultiple" true or false depending on your need
    final result = await FilePicker.platform.pickFiles(allowMultiple: false);

    // if no file is picked
    if (result == null) return;

    // we will log the name, size and path of the
    // first picked file (if multiple are selected)
    print(result.files.first.name);
    print(result.files.first.size);
    print(result.files.first.path);
    print(result.files.first.bytes);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text("DragonDrop"),
        ),
        backgroundColor: Colors.green[100],
        body: Container(
          decoration: BoxDecoration(image: DecorationImage()),
        ));
  }

  Widget chooseDevicePopup() {
    return AlertDialog(
      title: Text("Choose Device"),
      content: Text("Choose a device to send the file to"),
      actions: <Widget>[
        FlatButton(
          onPressed: () {
            Navigator.of(context).pop();
          },
          child: Text("Cancel"),
        ),
        FlatButton(
          onPressed: () {
            Navigator.of(context).pop();
          },
          child: Text("Send"),
        ),
      ],
    );
  }

  sendFile() async {}

  Future<bool> _register() async {
    Response response = await register("admin", "admin");
    if (response.successful()) {
      print("Successfully registered");
      return true;
    } else {
      print("Failed to register");
      return false;
    }
  }

  void _login() async {
    Response response = await login("admin", "admin");
    if (response.successful()) {
      storage.write(key: "token", value: response.body?["token"]);
      Navigator.push(context, MaterialPageRoute(builder: (_) => HomePage()));
    }
    String token = await storage.read(key: "token");
    print("GOT TOKEN: " + token);
  }
}
