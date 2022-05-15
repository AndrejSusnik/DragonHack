import 'dart:html';

import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'home_page.dart';
import 'main.dart';
import 'api/api.dart';

final storage = FlutterSecureStorage();


class Login extends StatefulWidget {
  @override
  _LoginState createState() => _LoginState();
}

class _LoginState extends State<Login> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: Text("Login Page"),
      ),
      body: SingleChildScrollView(
        child: Column(
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(top: 60.0),
              child: Center(
                child: Container(
                  width: 200,
                  height: 150,
                  /*decoration: BoxDecoration(
                        color: Colors.red,
                        borderRadius: BorderRadius.circular(50.0)),*/
                  child: Image.asset('assets/images/icon.png'),
                ),
              ),
            ),
            Padding(
              //padding: const EdgeInsets.only(left:15.0,right: 15.0,top:0,bottom: 0),
              padding: EdgeInsets.symmetric(horizontal: 15),
              child: TextField(
                decoration: InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'User Name',
                    hintText: 'Enter valid user name'),
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  left: 15.0, right: 15.0, top: 15, bottom: 0),
              //padding: EdgeInsets.symmetric(horizontal: 15),
              child: TextField(
                obscureText: true,
                decoration: InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'Password',
                    hintText: 'Enter good password'),
              ),
            ),
            FlatButton(
              onPressed: () {
                forgotPassword();
              },
              child: Text(
                'Forgot Password',
                style: TextStyle(color: Colors.blue, fontSize: 15),
              ),
            ),
            Container(
              height: 50,
              width: 250,
              decoration: BoxDecoration(
                  color: Colors.blue, borderRadius: BorderRadius.circular(20)),
              child: FlatButton(
                onPressed: () {
                  _login();
                },
                child: Text(
                  'Login',
                  style: TextStyle(color: Colors.white, fontSize: 25),
                ),
              ),
            ),
            SizedBox(
              height: 130,
            ),
            Container(
              height: 10,
              width: 50,
              decoration: BoxDecoration(
                  color: Colors.blue, borderRadius: BorderRadius.circular(20)),
              child: FlatButton(
                onPressed: () {
                  _register();
                },
                child: Text(
                  Text('New User? Create Account'),
                  style: TextStyle(color: Color.fromARGB(255, 255, 255, 255), fontSize: 25),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }


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
  Future<String> getUsername() async {
    String username = await 
    return username;
  }
  Future<String> getPassword() async {
    String password = await storage.read(key: "password");
    return password;
  }

  void _login() async {
    uName = await getUsername();
    passwd = await getPassword();
    Response response = await login(uName, passwd);
    if (response.successful()) {
      storage.write(key: "token", value: response.body?["token"]);
      Navigator.push(context, MaterialPageRoute(builder: (_) => HomePage()));
    }
    String token = await storage.read(key: "token");
    print("GOT TOKEN: " + token);
  }
}