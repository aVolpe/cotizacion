import 'package:app/model.dart';
import 'package:app/src/util.dart';
import 'package:flutter/material.dart';

typedef OnClickCallback = void Function(Data selected);

class ExchangeList extends StatelessWidget {
  final List<Data> exchanges;
  final OnClickCallback onSelected;
  final int multiplier;

  ExchangeList({this.exchanges, this.onSelected, this.multiplier});

  @override
  Widget build(BuildContext context) {
    return SliverFixedExtentList(
        itemExtent: 80,
        delegate: SliverChildBuilderDelegate((BuildContext context, int index) {
          return ExchangeRow(
            onSelected: this.onSelected,
            exchange: this.exchanges[index],
            multiplier: this.multiplier,
          );
        }, childCount: this.exchanges.length));
  }
}

class ExchangeRow extends StatelessWidget {
  @required
  final Data exchange;

  @required
  final OnClickCallback onSelected;

  @required
  final int multiplier;

  ExchangeRow({this.exchange, this.onSelected, this.multiplier});

  @override
  Widget build(BuildContext context) {
    var d = this.exchange;
    var place = d.place;
    var branch = d.branch;
    var isBureau = place.type == 'BUREAU';

    var subTitle = isBureau ? branch.name : 'Ver sucursales';

    var body = Container(
      alignment: Alignment.center,
      child: Padding(
        padding: EdgeInsets.symmetric(vertical: 0.0, horizontal: 10.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: <Widget>[
            Flexible(
              flex: 1,
              child: CircleAvatar(
                backgroundImage: NetworkImage(getImage(branch, place)),
              ),
            ),
            Expanded(
              flex: 6,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  Text(
                    d.place.name,
                    textAlign: TextAlign.justify,
                    overflow: TextOverflow.ellipsis,
                    maxLines: 2,
                    style: TextStyle(fontWeight: FontWeight.bold),
                  ),
                  Text(
                    subTitle,
                    textAlign: TextAlign.justify,
                    overflow: TextOverflow.fade,
                    maxLines: 2,
                  ),
                ],
              ),
            ),
            Flexible(
              flex: 2,
              child: Text('${formatMoney(d.salePrice * this.multiplier)}'),
            ),
            Flexible(
              flex: 2,
              child: Text('${formatMoney(d.purchasePrice * this.multiplier)}'),
            )
          ],
        ),
      ),
    );

    return GestureDetector(
        child: body,
        onTap: () {
          this.onSelected(this.exchange);
        });
  }
}
