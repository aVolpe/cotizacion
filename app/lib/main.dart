import 'package:app/api.dart';
import 'package:app/src/screens/exchange_list_page.dart';
import 'package:flutter/material.dart';
import "package:intl/intl.dart";

void main() => runApp(ExchangeApp());

class ExchangeApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {

    return MaterialApp(
      title: 'Cotizaciones PY',
      theme: ThemeData(
        primarySwatch: Colors.indigo,
      ),
      locale: Locale('es', 'PY'),
      home: ExchangeListPage(currencies: fetchCurrencies()),
    );
  }
}
