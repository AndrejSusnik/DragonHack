// ignore_for_file: use_function_type_syntax_for_parameters

import 'dart:io';
import 'package:network_info_plus/network_info_plus.dart';
import 'package:lan_scanner/lan_scanner.dart';

class IPAddress {
  late int value;
  IPAddress(this.value);
  IPAddress.address(IPAddress addr) {
    value = addr.value;
  }
  IPAddress.fromString(String addr) {
    List<String> parts = addr.split('\\.');
    if(parts.length != 4) {
      throw Exception("Invalid IP address");
    }
    value = (int.parse(parts[0], radix:10) << (8*3)) & 0xFF000000 |
            (int.parse(parts[1], radix:10) << (8*2)) & 0x00FF0000 |
            (int.parse(parts[2], radix:10) << (8*1)) & 0x0000FF00 |
            (int.parse(parts[3], radix:10) << (8*0)) & 0x000000FF;
  }
}

