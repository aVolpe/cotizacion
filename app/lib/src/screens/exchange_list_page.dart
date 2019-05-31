import 'package:app/api.dart';
import 'package:app/model.dart';
import 'package:app/src/screens/exchange_page.dart';
import 'package:app/src/widgets/exchange_list.dart';
import 'package:app/src/widgets/exchange_selector.dart';
import 'package:flutter/material.dart';

class ExchangeListPage extends StatefulWidget {
  ExchangeListPage({Key key, this.currencies}) : super(key: key);

  final Future<List<String>> currencies;

  @override
  _ExchangeListPage createState() => _ExchangeListPage();
}

class _ExchangeListPage extends State<ExchangeListPage> {
  Future<List<String>> _exchanges;
  String _currentExchange;
  Future<ExchangeData> _exchangeData;
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();
  ScrollController _scrollController;
  double kExpandedHeight = 300.0;
  String quantity = '1';

  _ExchangeListPage() {
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
      return 'USD';
    }
    return currencies[0];
  }

  void _setCurrent(String newCurrency) {
    setState(() {
      this._currentExchange = newCurrency;
      this._exchangeData = fetchCurrency(newCurrency);
    });
  }

  void _setQuantity(String newQuantity) {
    setState(() {
      this.quantity = newQuantity;
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
              onSelected: _setCurrent,
              data: this._exchangeData,
              quantity: this.quantity,
              quantityChangeCallback: (quan) => _setQuantity(quan));
        } else if (snapshot.hasError) {
          return Text("${snapshot.error}");
        }
        return Container(
          alignment: Alignment.center,
          height: 100,
          child: CircularProgressIndicator(
            valueColor: new AlwaysStoppedAnimation<Color>(Colors.white),
          ),
        );
      },
    );
    var quantity = int.tryParse(this.quantity) ?? 1;
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
              exchanges: snapshot.data.data,
              onSelected: (e) => Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => ExchangePage(
                              exchange: e,
                              isoCode: this._currentExchange,
                            )),
                  ),
              multiplier: quantity,
            );
        }
      },
    );

    var scrollTitle = this._currentExchange == null
        ? ''
        : 'Cambio de $quantity ${this._currentExchange}';

    var scrollTitleComponent = Padding(
      padding: EdgeInsets.symmetric(vertical: 0.0, horizontal: 10.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: <Widget>[
          Flexible(
            flex: 1,
            child: Text('              '),
          ),
          Expanded(
            flex: 6,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                Text(
                  scrollTitle,
                  textAlign: TextAlign.justify,
                  overflow: TextOverflow.ellipsis,
                  maxLines: 2,
                  style: TextStyle(
                      fontWeight: FontWeight.bold, color: Colors.white),
                )
              ],
            ),
          ),
          Flexible(
            flex: 2,
            child: Text(
              'Venta',
              textAlign: TextAlign.right,
              style:
                  TextStyle(fontWeight: FontWeight.bold, color: Colors.white),
            ),
          ),
          Flexible(
            flex: 2,
            child: Text(
              'Compra',
              textAlign: TextAlign.right,
              style:
                  TextStyle(fontWeight: FontWeight.bold, color: Colors.white),
            ),
          )
        ],
      ),
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
                      child: scrollTitleComponent,
                    ))),
            floating: true,
//            snap: true,
            pinned: true,
            automaticallyImplyLeading: false,
            backgroundColor: Colors.indigo,
            expandedHeight: kExpandedHeight,
            flexibleSpace: FlexibleSpaceBar(
              background: pickerRow,
              collapseMode: CollapseMode.parallax,
            ),
          ),
          exchanges,
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: loadData,
        tooltip: 'Recargar',
        child: Icon(Icons.refresh),
      ),
    );
  }
}
