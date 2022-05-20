import 'package:android_app/connection.dart';
import 'package:flutter/material.dart';
import 'dart:io';
import 'package:file_picker/file_picker.dart';
import 'dart:convert';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:network_info_plus/network_info_plus.dart';
import 'api/api.dart';
// import 'package:open_file/open_file.dart';
// import 'background.dart';

final storage = FlutterSecureStorage();

class Device {
  String? name;
  String? type;
  String? id;
  String? idUser;
  String? ip;

  @override
  String toString() {
    // TODO: implement toString
    return "$name $type $id $idUser $ip";
  }
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
  String wifiIP = "";

  @override
  void initState() {
    super.initState();
    myIp(onSuccess: () async {
      startServer();
      _addDevice();
    });
  }

  Future<void> _addDevice({VoidCallback? onSuccess}) async {
    Map<String, String> DISCOVERY_FRAME = {
      "id": await storage.read(key: "deviceId"),
      "device_type": "phone",
      "ip": wifiIP
    };
    var response = await addDevice(DISCOVERY_FRAME);
    onSuccess?.call();
  }

  Future<void> myIp({VoidCallback? onSuccess}) async {
    for (var interface in await NetworkInterface.list()) {
      if (interface.name == "wlan0") {
        wifiIP = interface.addresses.first.address;
      }
    }
    onSuccess?.call();
  }

  startServer({VoidCallback? onSuccess}) async {
    var server = await HttpServer.bind("0.0.0.0", 9999, shared: true);
    NetworkInfo networkInfo = NetworkInfo();
    print("Server running on IP : " +
        wifiIP +
        " On Port : " +
        server.port.toString());
    await for (var request in server) {
      if (request.method == 'GET') {
        if (request.uri.path == '/discovery') {
          Map<String, String> DISCOVERY_FRAME = {
            "id": await storage.read(key: "deviceId"),
            "device_type": "phone",
            "ip": wifiIP
          };
          request.response
            ..headers.contentType =
                new ContentType("text", "html", charset: "utf-8")
            ..write(json.encode(DISCOVERY_FRAME))
            ..close();
        } else if (request.uri.path == "/file") {
          // receiveFile(request);
        } else {
          request.response
            ..headers.contentType =
                new ContentType("text", "html", charset: "utf-8")
            ..write("Not implemented :)!")
            ..close();
        }
      }
      onSuccess?.call();
    }
  }

  void _pickFile() async {
    // opens storage to pick files and the picked file or files
    // are assigned into result and if no file is chosen result is null.
    // you can also toggle "allowMultiple" true or false depending on your need
    final result = await FilePicker.platform
        .pickFiles(allowMultiple: false, withData: true);
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
                      Container(
                        width: MediaQuery.of(context).size.width + 200,
                        margin: EdgeInsets.only(bottom: 20),
                        decoration: BoxDecoration(
                          color: Colors.blue,
                        ),
                        child: RaisedButton(
                          color: Colors.blue,
                          onPressed: () {
                            _pickFile();
                          },
                          child: Text(
                            !fileSelected
                                ? "Pick a File"
                                : selectedFile.files[0].name + " selected",
                            style: TextStyle(
                              color: Colors.white,
                            ),
                          ),
                        ),
                      ),
                    ])))));
  }

  Widget chooseDevicePopup(int index) {
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
            print(devices[index].toString());
            sendFile(devices[index].ip!, selectedFile.files.single.path!);
            Navigator.of(context).pop();
          },
          child: Text("Send"),
        ),
      ],
    );
  }

  Future<AsyncSnapshot> _getDevices() async {
    print("gettingDevices");
    Response response = await getDevices();
    print(response.body);
    if (response.successful()) {
      devices = [];

      for (var device in response.body?["devices"]) {
        print("ANDREJ?: " + device["ip"]);
        var discovery = await discover(device["ip"]);
        print("DISCOVERY: " + discovery.toString());
        if (discovery.successful()) {
          print(device);
          devices.add(Device()
            ..name = device["name"]
            ..type = device["type"]
            ..id = device["id"]
            ..ip = device["ip"]
            ..idUser = device["id_user"]);
        } else {
          deleteDevice(device["id"]);
        }
      }
      print(devices.toString());
    }
    return AsyncSnapshot.withData(ConnectionState.done,
        {"devices": devices, "message": response.body["message"]});
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
        minHeight: MediaQuery.of(context).size.height / 2,
        minWidth: MediaQuery.of(context).size.width / 2,
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
                            return chooseDevicePopup(index);
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
