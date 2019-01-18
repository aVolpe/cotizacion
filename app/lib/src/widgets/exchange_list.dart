import 'package:app/model.dart';
import 'package:app/src/util.dart';
import 'package:flutter/material.dart';

typedef OnClickCallback = void Function(String selected);

class ExchangeList extends StatelessWidget {
  final List<Data> exchanges;
  final OnClickCallback onSelected;

  ExchangeList({this.exchanges, this.onSelected});

  @override
  Widget build(BuildContext context) {
    return SliverFixedExtentList(
        itemExtent: 80,
        delegate: SliverChildBuilderDelegate((BuildContext context, int index) {
          return ExchangeRow(
            onSelected: this.onSelected,
            exchange: this.exchanges[index],
          );
        }, childCount: this.exchanges.length));
  }
}

class ExchangeRow extends StatelessWidget {
  final Data exchange;
  final OnClickCallback onSelected;

  ExchangeRow({this.exchange, this.onSelected});

  @override
  Widget build(BuildContext context) {
    var d = this.exchange;
    var place = d.place;
    var branch = d.branch;
    var isBureau = place.type == 'BUREAU';

    var subTitle = isBureau ? branch.name : 'Ver sucursales';
    subTitle = limitStringToSize(subTitle, 15);
    var image = branch != null && branch.image != null
        ? branch.image
        : 'https://dummyimage.com/700x400/3F51B5/fff.png?text=${getFirstLetters(place.name)}';

    return Container(
      alignment: Alignment.center,
//      color: Colors.lightBlue[100],
      child: Padding(
        padding: EdgeInsets.symmetric(vertical: 0.0, horizontal: 0.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: <Widget>[
            CircleAvatar(
              backgroundImage: NetworkImage(image),
            ),
            Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                Text(
                  d.place.name,
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                Text(subTitle)
              ],
            ),
            Text('${formatMoney(d.salePrice)}'),
            Text('${formatMoney(d.purchasePrice)}')
          ],
        ),
      ),
    );
  }
}
