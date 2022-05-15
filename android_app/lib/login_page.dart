import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'home_page.dart';
import 'main.dart';
import 'api/api.dart';
import 'register_page.dart';

final storage = FlutterSecureStorage();

class Login extends StatefulWidget {
  @override
  _LoginState createState() => _LoginState();
}

class _LoginState extends State<Login> {
  List<TextEditingController> teds = [
    new TextEditingController(),
    new TextEditingController()
  ];
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
              //entering username
              child: TextField(
                decoration: InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'User Name',
                    hintText: 'Enter valid user name'),
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
                    border: OutlineInputBorder(), labelText: 'Password'),
                controller: teds[1],
              ),
            ),
            Container(
              height: 50,
              width: 250,
              margin: EdgeInsets.only(top: 40, bottom: 100),
              decoration: BoxDecoration(
                  color: Colors.blue, borderRadius: BorderRadius.circular(20)),
              child: FlatButton(
                onPressed: () {
                  var user = teds[0].text;
                  var pass = teds[1].text;
                  _login(user, pass);
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
              child: GestureDetector(
                  child: Text(
                    'New User? Create Account',
                    style: TextStyle(
                        // color: Color.fromARGB(255, 255, 255, 255),
                        fontSize: 16),
                  ),
                  onTap: () => Navigator.push(context,
                      MaterialPageRoute(builder: (context) => Register()))),
            ),
          ],
        ),
      ),
    );
  }

  void _login(String username, String password) async {
    Response response = await login(username, password);
    if (response.successful()) {
      storage.write(key: "token", value: response.body?["token"]);
      storage.write(key: "userId", value: response.body?["id"]);
      Navigator.push(context, MaterialPageRoute(builder: (_) => Home()));
    }
    String? token = await storage.read(key: "token");
    print("GOT TOKEN: " + token);
  }
}
