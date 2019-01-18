import 'package:flutter/material.dart';
import 'package:flutter_picker/flutter_picker.dart';

typedef PickerConfirmCallback = void Function(String selected);

class ExchangeSelector extends StatelessWidget {
  final List<String> exchanges;
  final String selected;
  final GlobalKey<ScaffoldState> scaffoldKey;
  final PickerConfirmCallback onSelected;

  ExchangeSelector(
      {this.scaffoldKey, this.exchanges, this.selected, this.onSelected});

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
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          mainAxisSize: MainAxisSize.max,
          children: [
            Text('Moneda: ',
                style: new TextStyle(
                  fontWeight: FontWeight.bold,
                  fontSize: 20.0,
                  fontFamily: 'Roboto',
                )),
            RaisedButton(
                color: Colors.deepPurple,
                textColor: Colors.white,
                onPressed: () => this.showPicker(context),
                child: Text(this.selected,
                    style: new TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 20.0,
                      fontFamily: 'Roboto',
                    )))
          ],
        )
      ],
    );
  }
}
