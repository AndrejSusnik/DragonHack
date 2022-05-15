import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'home_page.dart';
import 'login_page.dart';
import 'register_page.dart';
// import 'package:open_file/open_file.dart';
// import 'background.dart';

final storage = FlutterSecureStorage();

void main() {
  runApp(MaterialApp(
    title: "DragonDrop",
    home: Login(),
    routes: {
      '/home': (context) => Home(),
      '/login': (context) => Login(),
      '/register': (context) => Register(),
    },
  ));
}
