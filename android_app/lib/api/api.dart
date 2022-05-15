import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

final String serverUrl = "http://192.168.137.178:5000/v1";

final storage = FlutterSecureStorage();

class Response {
  int? status;
  dynamic? body;

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

Future<Response> getDevices() async {
  var token = await storage.read(key: "token");
  var user = await storage.read(key: "userId");
  var res = await http.get(Uri.parse("$serverUrl/device_list/$user"),
      headers: {"Authorization": "Bearer $token"});
  return Response(res.statusCode, json.decode(res.body));
}

Future<Response> addDevice(Map? discoveryFrame) async {
  var token = await storage.read(key: "token");
  var res = await http.post(Uri.parse("$serverUrl/device/1"),
      body: json.encode(discoveryFrame),
      headers: {"Authorization": "Bearer $token"});
  return Response(res.statusCode, json.decode(res.body));
}

Future<Response> deleteDevice(String? deviceId) async {
  var token = await storage.read(key: "token");
  var res = await http.delete(Uri.parse("$serverUrl/device/$deviceId"),
      headers: {"Authorization": "Bearer $token"});
  return Response(res.statusCode, json.decode(res.body));
}
