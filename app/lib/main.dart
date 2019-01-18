import 'package:app/api.dart';
import 'package:app/model.dart';
import 'package:app/src/widgets/exchange_list.dart';
import 'package:app/src/widgets/exchange_selector.dart';
import 'package:flutter/material.dart';
import "package:intl/intl.dart";

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    Intl.defaultLocale = 'es_PY';

    return MaterialApp(
      title: 'Cotizaciones PY',
      theme: ThemeData(
        primarySwatch: Colors.indigo,
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
  String _currentExchange;
  Future<ExchangeData> _exchangeData;
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();
  ScrollController _scrollController;
  double kExpandedHeight = 300.0;

  _MyHomePageState() {
    this._exchanges = fetchCurrencies();
    _exchanges.then((data) => this._setCurrent(this.getDefaultExchange(data)));

    _scrollController = ScrollController()..addListener(() => setState(() {}));
  }

  Future<void> loadVoid() {
    return new Future<void>(() => setState(() {
          this._exchanges = fetchCurrencies();
          _exchanges.then((data) => _setCurrent(getDefaultExchange(data)));
        }));
  }

  void loadData() {
    setState(() {
      this._exchanges = fetchCurrencies();
      _exchanges
          .then((data) => this._setCurrent(this.getDefaultExchange(data)));
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
      this._currentExchange = newCurrency;
      this._exchangeData = fetchCurrency(newCurrency);
      print('setting new currency: $newCurrency');
    });
  }

  double get _titleOpacity {
    if (!_scrollController.hasClients) return 1;
    double height = kExpandedHeight - kToolbarHeight;
    if (_scrollController.offset > height) return 1;
    if (_scrollController.offset < 0) return 0;

    return ((_scrollController.offset / height)).abs();
  }

  @override
  Widget build(BuildContext context) {
    var pickerRow = FutureBuilder<List<String>>(
      future: this._exchanges,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          return ExchangeSelector(
              scaffoldKey: this._scaffoldKey,
              exchanges: snapshot.data,
              selected: _currentExchange,
              onSelected: _setCurrent);
        } else if (snapshot.hasError) {
          return Text("${snapshot.error}");
        }
        return Container(
          alignment: Alignment.center,
          height: 300,
          child: CircularProgressIndicator(
            valueColor: new AlwaysStoppedAnimation<Color>(Colors.white),
          ),
        );
      },
    );
    var exchanges = FutureBuilder<ExchangeData>(
      future: this._exchangeData,
      builder: (context, snapshot) {
        switch (snapshot.connectionState) {
          case ConnectionState.none:
          case ConnectionState.active:
          case ConnectionState.waiting:
            return SliverList(
              delegate: SliverChildListDelegate(
                [
                  Container(
                    alignment: Alignment.center,
                    height: 300,
                    child: CircularProgressIndicator(),
                  ),
                ],
              ),
            );
          case ConnectionState.done:
            if (snapshot.hasError) {
              return new SliverAppBar(
                title: Text('Error: ${snapshot.error}'),
                snap: true,
                floating: true,
              );
            }
            return ExchangeList(
                exchanges: snapshot.data.data, onSelected: (e) => print(e));
        }
      },
    );
    return Scaffold(
      key: this._scaffoldKey,
      body: CustomScrollView(
        controller: _scrollController,
        slivers: <Widget>[
          SliverAppBar(
            primary: true,
            bottom: PreferredSize(
                preferredSize: Size(110, 50),
                child: Opacity(
                  opacity: _titleOpacity,
                  child: Padding(
                      padding: EdgeInsets.all(8.0),
                      child: Text(
                        this._currentExchange == null
                            ? ''
                            : 'Moneda ${this._currentExchange}',
                        style: TextStyle(
                            color: Colors.white,
                            fontSize: 20,
                            fontWeight: FontWeight.bold),
                      )),
                )),
            floating: true,
//            snap: true,
            pinned: true,
            automaticallyImplyLeading: false,
            backgroundColor: Colors.indigo,
            expandedHeight: kExpandedHeight,
            flexibleSpace: FlexibleSpaceBar(
              background: pickerRow,
            ),
          ),
          exchanges,
        ],
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
