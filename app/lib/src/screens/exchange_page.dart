import 'package:app/model.dart';
import 'package:app/src/util.dart';
import 'package:flutter/material.dart';

_row(Widget key, Widget value) => Padding(
    padding: EdgeInsets.symmetric(vertical: 3, horizontal: 30),
    child: Row(
      mainAxisAlignment: MainAxisAlignment.start,
      children: <Widget>[key, Text("  "), value],
    ));

_key(String text) => Text(
      text,
      textAlign: TextAlign.right,
      style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
    );

_value(String text) => Text(
      text,
      textAlign: TextAlign.left,
      style: TextStyle(fontSize: 15),
    );

_valueW(Widget body) => body;

class ExchangePage extends StatelessWidget {
  final Data exchange;
  final String isoCode;

  ExchangePage({this.exchange, this.isoCode});

  @override
  Widget build(BuildContext context) {
    List<Widget> body = [];

    var im = NetworkImage(getImage(this.exchange.branch, this.exchange.place));

    body.add(Container(
      width: MediaQuery.of(context).size.width,
      height: 100,
      decoration: BoxDecoration(
        image: DecorationImage(
          fit: BoxFit.fitWidth,
          image: im,
        ),
      ),
    ));

    body.add(Padding(padding: EdgeInsets.all(20)));
    body.add(Text(
      this.exchange.place.name,
      style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
    ));
    body.add(Text(
      this.exchange.branch.name,
      style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20),
    ));
    body.add(Padding(padding: EdgeInsets.all(20)));

    body.add(_row(_key("Compra de ${this.isoCode}:"),
        _value("${formatMoney(this.exchange.purchasePrice)}")));
    body.add(_row(_key("Venta de ${this.isoCode}:"),
        _value("${formatMoney(this.exchange.salePrice)}")));

    if (this.exchange.branch.email != null) {
      body.add(_row(
          _key("Email:"),
          _valueW(GestureDetector(
              child: Text(
                this.exchange.branch.email,
                textAlign: TextAlign.left,
                style: TextStyle(fontSize: 15),
              ),
              onTap: () {
                sendMail(this.exchange.branch.email);
              }))));
    }

    if (this.exchange.branch.phoneNumber != null)
      body.add(_row(
          _key("Teléfonos:"),
          _valueW(GestureDetector(
              child: Text(
                this
                    .exchange
                    .branch
                    .phoneNumber
                    .replaceAll("/", "\n")
                    .replaceAll(RegExp(r'\n\d$'), "")
                    .trim()
                    .replaceAll(" ", ""),
                textAlign: TextAlign.left,
                style: TextStyle(fontSize: 15),
              ),
              onTap: () {
                callPhone(this.exchange.branch.phoneNumber);
              }))));

    if (this.exchange.branch.schedule != null) {
      var schedule = this.exchange.branch.schedule;
      schedule = schedule.split(",").join("\n").replaceAll("\n ", "\n");
      body.add(_row(_key("Horario:"), _value(schedule)));
    }

    List<Widget> buttons = [];
    if (this.exchange.branch.latitude != null &&
        this.exchange.branch.longitude != null) {
      buttons.add(FlatButton(
          child: Text(
            "Ver en el mapa",
            textAlign: TextAlign.left,
            style: TextStyle(
                color: Colors.black87,
                fontSize: 18,
                fontWeight: FontWeight.bold),
          ),
          onPressed: () {
            openMap(
                this.exchange.branch.latitude, this.exchange.branch.longitude);
          }));
    }

    buttons.add(FlatButton(
      child: Text("Volver",
          style: TextStyle(
              color: Colors.indigo, fontSize: 18, fontWeight: FontWeight.bold)),
      onPressed: () {
        Navigator.pop(context);
      },
    ));

    body.add(Padding(
        padding: EdgeInsets.symmetric(vertical: 40.0, horizontal: 10.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: buttons,
        )));

    return Scaffold(
//        appBar: AppBar(
//          title: Text(this.exchange.place.name),
//        ),
        body: Padding(
            padding: EdgeInsets.symmetric(vertical: 0.0, horizontal: 0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: body,
            )),
        bottomNavigationBar: BottomAppBar(
            child: Padding(
              padding: EdgeInsets.fromLTRB(4, 4, 4, 0),
              child: Column(mainAxisSize: MainAxisSize.min, children: [
                Text(
                  """
            Estos datos fueron obtenidos de la página web de ${this.exchange.place.name}. Si tienes mas datos de esta casa de cambios, envianos un mail.
            """
                      .trim(),
                  style: TextStyle(fontSize: 10),
                  textAlign: TextAlign.center,
                ),
                Text(
                  "",
                  textAlign: TextAlign.center,
                ),
                Text(
                  "Estos datos fueron consultados el ${format(this.exchange.queryDate)}",
                  textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 10),
                )
              ]),
            ),
            color: Color(0xFFD3D3D3)));
  }
}
