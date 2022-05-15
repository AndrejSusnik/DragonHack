import 'package:android_app/login_page.dart';
import 'package:flutter/material.dart';
import 'api/api.dart';

class Register extends StatefulWidget {
  @override
  _RegisterState createState() => _RegisterState();
}

class _RegisterState extends State<Register> {
  List<TextEditingController> teds = [
    new TextEditingController(),
    new TextEditingController(),
    new TextEditingController()
  ];
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: Text("Registry Page"),
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
              //entering username
              child: TextField(
                decoration: InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'User Name',
                    hintText: 'Enter user name'),
                controller: teds[0],
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  left: 15.0, right: 15.0, top: 15, bottom: 0),
              //padding: EdgeInsets.symmetric(horizontal: 15),
              //entering password
              child: TextField(
                obscureText: true,
                decoration: InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'Password',
                    hintText: 'Enter good password'),
                controller: teds[1],
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  left: 15.0, right: 15.0, top: 15, bottom: 0),
              //padding: EdgeInsets.symmetric(horizontal: 15),
              //entering password
              child: TextField(
                obscureText: true,
                decoration: InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'Repeat password',
                    hintText: 'Repeat good password'),
                controller: teds[2],
              ),
            ),
            Container(
              margin: EdgeInsets.only(top: 20),
              height: 50,
              width: 250,
              decoration: BoxDecoration(
                  color: Color.fromARGB(255, 2, 134, 57),
                  borderRadius: BorderRadius.circular(20)),
              child: FlatButton(
                onPressed: () async {
                  var user = teds[0].text;
                  var pass = teds[1].text;
                  var pass2 = teds[2].text;
                  if (pass == pass2) {
                    bool res = await _register(user, pass);
                    print(res);
                    if (res) {
                      Navigator.push(context,
                          MaterialPageRoute(builder: (_) => Login()));
                    } else {
                      Navigator.push(context,
                          MaterialPageRoute(builder: (_) => Register()));
                    }
                    ;
                  } else {
                    print("Passwords don't match");
                    // reload register page
                  }
                },
                child: Text(
                  'Register',
                  style: TextStyle(color: Colors.white, fontSize: 25),
                ),
              ),
            ),
            SizedBox(
              height: 130,
            ),
          ],
        ),
      ),
    );
  }

  Future<bool> _register(username, password) async {
    Response response = await register(username, password);
    if (response.successful()) {
      print("Successfully registered");
      return true;
    } else {
      print("Failed to register");
      return false;
    }
  }
}
