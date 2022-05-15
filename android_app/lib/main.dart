import 'package:android_app/connection.dart';
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

class Device {
  String? name;
  String? type;
  int? id;
  int? idUser;
  String? ip;
}

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  var devices = <Device>[];
  var selectedDevice = Device();
  var fileSelected = false;
  late FilePickerResult selectedFile;

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
    final result = await FilePicker.platform.pickFiles(allowMultiple: false, withData: true);
    // if no file is picked
    if (result == null) {
      setState(() {
        fileSelected = false;
      });
      return;
    }
    setState(() {
      selectedFile = result;
      fileSelected = true;
    });

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
        backgroundColor: Colors.white,
        body: Container(
            child: Center(
                child: Container(
                    width: 200,
                    decoration: BoxDecoration(
                        image: DecorationImage(
                            opacity: 0.5,
                            image: AssetImage("assets/images/icon.png"),
                            fit: BoxFit.scaleDown)),
                    child: Column(children: [
                      Expanded(
                          child: FutureBuilder(
                              initialData: "Finding your devices...",
                              future: _getDevices(),
                              builder: (BuildContext context,
                                  AsyncSnapshot snapshot) {
                                if (snapshot.hasData &&
                                    snapshot.data ==
                                        "Finding your devices...") {
                                  return Center(
                                      child: Column(
                                          mainAxisAlignment:
                                              MainAxisAlignment.center,
                                          children: [
                                        Container(
                                          width: 50,
                                          height: 50,
                                          child: CircularProgressIndicator(),
                                        ),
                                        Text(
                                          snapshot.data,
                                          textAlign: TextAlign.center,
                                          style: TextStyle(
                                              fontSize: 20,
                                              fontWeight: FontWeight.bold),
                                        )
                                      ]));
                                } else {
                                  return buildDeviceGrid();
                                }
                              })),
                      RaisedButton(
                        child: Text("Login"),
                        onPressed: () => _login(),
                      ),
                      RaisedButton(
                        onPressed: () {
                          _pickFile();
                        },
                        child: Text(!fileSelected
                            ? "Pick a File"
                            : selectedFile.files[0].name + " selected"),
                      ),
                    ])))));
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
            
            // sendFile("s", selectedFile.files[0].path!);
            sendFile("s", selectedFile.files.single.path!);
            Navigator.of(context).pop();
          },
          child: Text("Send"),
        ),
      ],
    );
  }

  void _register() async {
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
      storage.write(
          key: "token", value: response.body?["token"].toString().trim());
    }
    String token = await storage.read(key: "token");
    print("GOT TOKEN: " + token);
  }

  Future _getDevices() async {
    Response response = await getDevices();
    if (response.successful()) {
      devices = [];

      for (var device in response.body?["devices"]) {
        var discovery = await discover(device["ip"]);
        if (!discovery.successful()) {
          devices.add(Device()
            ..name = device["name"]
            ..type = device["type"]);
        } else {
          deleteDevice(device["id"]);
        }
      }
    }
    return true;
  }

  AssetImage getDeviceImage(String deviceType) {
    switch (deviceType) {
      case "computer":
        return AssetImage("assets/images/pc_icon.png");
      case "phone":
        return AssetImage("assets/images/smartphone_icon.png");
      default:
        return AssetImage("assets/images/pc_icon.png");
    }
  }

  Widget buildDeviceGrid() {
    return OverflowBox(
        maxHeight: MediaQuery.of(context).size.height - 150,
        maxWidth: MediaQuery.of(context).size.width - 20,
        child: Container(
          margin: EdgeInsets.fromLTRB(0, 20, 0, 20),
          child: GridView.count(
              crossAxisCount: 2,
              children: List.generate(devices.length, (index) {
                Device device = devices[index];
                AssetImage deviceImage = getDeviceImage(device.type!);
                return GestureDetector(
                    child: Container(
                        decoration: BoxDecoration(
                            boxShadow: [
                              BoxShadow(
                                color: Colors.grey,
                                blurRadius: 10.0,
                              )
                            ],
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(10)),
                        margin: EdgeInsets.all(10),
                        child: Column(children: [
                          Expanded(
                            child: Container(
                                margin: EdgeInsets.only(top: 10),
                                width: 100,
                                height: 100,
                                decoration: BoxDecoration(
                                  image: DecorationImage(
                                      image: deviceImage, fit: BoxFit.contain),
                                )),
                          ),
                          Container(
                            margin: EdgeInsets.only(bottom: 10),
                            child: Text(
                              devices[index].name!,
                              style: TextStyle(
                                  fontSize: 20, fontWeight: FontWeight.w600),
                            ),
                          ),
                        ])),
                    onTap: () {
                      showDialog(
                          context: context,
                          builder: (BuildContext context) {
                            return chooseDevicePopup();
                          });
                    });
              })),
        ));
    // children: ListView.builder(
    //     itemBuilder: (BuildContext context, int index) {
    //       return Container(

    //       )
    //     })));
  }
}
