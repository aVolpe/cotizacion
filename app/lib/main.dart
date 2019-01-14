import 'package:app/api.dart';
import 'package:app/model.dart';
import 'package:flutter/material.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Cotizaciones PY',
      theme: ThemeData(
        primarySwatch: Colors.deepPurple,
      ),
      home: MyHomePage(
          title: 'Cotizaciones de monedas en Paraguay',
          currencies: fetchCurrencies()),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title, this.currencies}) : super(key: key);

  final String title;
  final Future<List<String>> currencies;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  Future<List<String>> _exchanges;
  Future<String> _currentExchange;
  Future<ExchangeData> _exchangeData;

  _MyHomePageState() {
    this._exchanges = fetchCurrencies();
    this._currentExchange =
        this._exchanges.then((data) => this.getDefaultExchange(data));
    this._exchangeData =
        this._currentExchange.then((exc) => fetchCurrency(exc));
  }

  void loadData() {
    setState(() {
      this._exchanges = fetchCurrencies();
      this._currentExchange =
          this._exchanges.then((data) => this.getDefaultExchange(data));
      this._exchangeData =
          this._currentExchange.then((exc) => fetchCurrency(exc));
    });
  }

  String getDefaultExchange(List<String> currencies) {
    if (currencies.indexOf('USD') >= 0) {
      print('returing USD');
      return 'USD';
    }
    print('returing ${currencies[0]}');
    return currencies[0];
  }

  void _setCurrent(String newCurrency) {
    setState(() {
      this._exchangeData = fetchCurrency(newCurrency);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            FutureBuilder<String>(
              future: this._currentExchange,
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  return Text('Moneda: ${snapshot.data}');
                } else if (snapshot.hasError) {
                  return Text("${snapshot.error}");
                }
                return CircularProgressIndicator();
              },
            ),
            FutureBuilder<ExchangeData>(
              future: this._exchangeData,
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  return Text(snapshot.data.lastQueryResult);
                } else if (snapshot.hasError) {
                  return Text("${snapshot.error}");
                }
                return CircularProgressIndicator();
              },
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: loadData,
        tooltip: 'Recargar',
        child: Icon(Icons.refresh),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}

class Exchanges extends StatelessWidget {
  final ExchangeData exchanges;

  Exchanges({this.exchanges});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Cotizaciones PY',
      theme: ThemeData(
        primarySwatch: Colors.deepPurple,
      ),
      home: MyHomePage(
          title: 'Cotizaciones de monedas en Paraguay',
          currencies: fetchCurrencies()),
    );
  }
}
