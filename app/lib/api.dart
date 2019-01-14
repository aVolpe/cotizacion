import 'dart:convert';

import 'package:app/model.dart';
import 'package:http/http.dart' as http;

Future<List<String>> fetchCurrencies() async {
  final response =
      await http.get('https://cotizaciones.volpe.com.py/api/exchange/');

  if (response.statusCode == 200) {

    final List<dynamic> toRet = json.decode(response.body);

    return toRet.map((r) => r.toString()).toList();
  } else {
    throw Exception('Failed to load currency list');
  }
}

Future<ExchangeData> fetchCurrency(String isoCode) async {
  final response =
      await http.get('https://cotizaciones.volpe.com.py/api/exchange/$isoCode');

  print("El estado es ${response.statusCode}");

  if (response.statusCode == 200) {
    return ExchangeData.fromJson(json.decode(response.body));
  } else {
    throw Exception('Failed to load exchanges of $isoCode');
  }
}
