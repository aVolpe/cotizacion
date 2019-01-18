import 'package:app/model.dart';
import 'package:flutter/material.dart';
import 'package:flutter_picker/flutter_picker.dart';

typedef PickerConfirmCallback = void Function(String selected);
typedef QuantityChangeCallback = void Function(String newQuantity);

class ExchangeSelector extends StatelessWidget {
  final List<String> exchanges;
  final Future<ExchangeData> data;
  final String selected;
  final GlobalKey<ScaffoldState> scaffoldKey;
  final PickerConfirmCallback onSelected;
  final String quantity;
  final QuantityChangeCallback quantityChangeCallback;
  TextEditingController _quantityController;

  ExchangeSelector(
      {this.scaffoldKey,
      this.exchanges,
      this.selected,
      this.onSelected,
      this.data,
      this.quantity,
      this.quantityChangeCallback}) {
    _quantityController = new TextEditingController(text: '${this.quantity}');
    _quantityController.addListener(
        () => this.quantityChangeCallback(_quantityController.text));
  }

  showPicker(BuildContext context) {
    new Picker(
        adapter: PickerDataAdapter<String>(pickerdata: this.exchanges),
        changeToFirst: true,
        selecteds: [exchanges.indexOf(selected)],
        textAlign: TextAlign.right,
        columnPadding: const EdgeInsets.all(8.0),
        title: Text('Elije un amoneda'),
        confirmText: 'Aceptar',
        cancelText: 'Cancelar',
        onConfirm: (Picker picker, List<int> value) {
          this.onSelected(picker.getSelectedValues()[0]);
        }).showModal(context);
//    picker.show(this.scaffoldKey.currentState);
  }

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    var data = Column(
      mainAxisAlignment: MainAxisAlignment.spaceAround,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'Cotizaciones en el Paraguay',
              style: TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 20),
            ),
          ],
        ),
        FutureBuilder<ExchangeData>(
            future: this.data,
            builder: (context, snapshot) {
              if (!snapshot.hasData) {
                return Container();
              }
              var row1 = 'Se muestran las ${snapshot.data.count} cotizaciones';
              var row2 = 'Consultadas el ${snapshot.data.firstQueryResult}';
              return Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  Text(row1,
                      style: TextStyle(
                          color: Colors.white70,
                          fontSize: 15,
                          fontWeight: FontWeight.normal)),
                  Text(row2,
                      style: TextStyle(
                          color: Colors.white70,
                          fontSize: 13,
                          fontWeight: FontWeight.normal))
                ],
              );
            }),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          mainAxisSize: MainAxisSize.max,
          children: [
            Text('Cambio de ',
                style: new TextStyle(
                  fontWeight: FontWeight.normal,
                  color: Colors.white,
                  fontSize: 17.0,
                )),
            Container(
              width: 5 + 9.5 * (this.quantity.toString().length),
              child: TextFormField(
                controller: this._quantityController,
                textAlign: TextAlign.end,
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 17.0,
                  fontWeight: FontWeight.bold,
                  decoration: TextDecoration.underline,
                ),
                decoration: InputDecoration(
                  border: InputBorder.none,
                ),
              ),
            ),
            FlatButton(
                color: Colors.indigo,
                onPressed: () => this.showPicker(context),
                child: Text(this.selected,
                    textAlign: TextAlign.start,
                    style: new TextStyle(
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                      decoration: TextDecoration.underline,
                      fontSize: 17.0,
                    )))
          ],
        ),
      ],
    );

    return Padding(
      padding: EdgeInsets.fromLTRB(5, 20, 5, 0),
      child: data,
    );
  }
}
