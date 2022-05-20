import 'dart:io';
import 'dart:typed_data';
import 'package:file_picker/file_picker.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'api/api.dart';
import 'dart:convert';

final storage = FlutterSecureStorage();

Future<Response> discover(String ip) async {
  print("IP: " + "http://$ip:9999/discovery");
  var res = await http.get(Uri.parse("http://$ip:9999/discovery")).timeout(
      Duration(milliseconds: 5000),
      onTimeout: () => http.Response(json.encode({"Error": "Timeout"}), 400));
  return Response(res.statusCode, {"body": res.body});
}

Future<Response> sendFile(String ip, String path) async {
  print("PATH: " + path);
  var res = await http.MultipartRequest(
    "POST",
    Uri.parse("http://$ip:9999/file"),
  )
    ..files.add(await http.MultipartFile.fromPath("file", path))
    ..send();
  return Response(200, {});
  // return Response(res.statusCode, await json.decode(re));
}

receiveFile(HttpRequest request) async {
  var file = await request.connectionInfo;
  
}
