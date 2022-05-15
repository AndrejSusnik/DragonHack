import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

final String serverUrl = "http://88.200.891.37:5000/v1";

final storage = FlutterSecureStorage();

class Response {
  int? status;
  Map? body;
  Response(int status, Map body) {
    this.status = status;
    this.body = body;
  }

  @override
  String toString() {
    return 'Response{status: $status, body: $body}';
  }

  bool successful() {
    return status == 200;
  }
}

Future<Response> register(String username, String password) async {
  var res = await http.put(Uri.parse("$serverUrl/auth"),
      body: jsonEncode({"username": username, "password": password}),
      headers: {"Content-Type": "application/json"});
  return Response(res.statusCode, json.decode(res.body));
}

Future<Response> login(String username, String password) async {
  var res = await http.post(Uri.parse("$serverUrl/auth"),
      body: jsonEncode({"username": username, "password": password}),
      headers: {"Content-Type": "application/json"});
  return Response(res.statusCode, json.decode(res.body));
}
