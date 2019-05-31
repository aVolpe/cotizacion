import 'dart:io';
import 'package:app/model.dart';
import 'package:intl/intl.dart';
import 'package:url_launcher/url_launcher.dart';

String formatMoney(int number) {
  if (number == null) {
    return '';
  }
  var f = new NumberFormat("#,###", "es_PY");
  return f.format(number);
}

String limitStringToSize(String base, int size) {
  if (base == null) return '';

  if (base.length > size) {
    return '${base.substring(0, size - 3)}...';
  }

  return base;
}

String getFirstLetters(String base) {
  if (base == null) return '';

  return base.split(' ').map((word) => word[0].toLowerCase()).join('');
}

sendMail(String address) async {
  // Android and iOS
  var uri = 'mailto:$address';
  print(uri);
  if (await canLaunch(uri)) {
    await launch(uri);
  } else {
    throw 'Could not launch $uri';
  }
}

callPhone(String number) async {
  // Android
  var finalNumber = number
      .replaceAll("(", "")
      .replaceAll(")", "")
      .replaceAll("-", "")
      .replaceAll(RegExp(r'\s'), "")
      .replaceAll(RegExp(r'/.*'), "")
      .trim();
  var uri = "tel:+$finalNumber";
  if (await canLaunch(uri)) {
    await launch(uri);
  } else {
    // iOS
    if (await canLaunch(uri)) {
      await launch(uri);
    } else {
      throw 'Could not launch $uri';
    }
  }
}

openMap(double lat, double lng) async {
  // Android
  var url = 'geo:$lat,$lng';
  if (Platform.isIOS) {
    // iOS
    url = 'http://maps.apple.com/?ll=$lat,$lng';
  }
  if (await canLaunch(url)) {
    await launch(url);
  } else {
    throw 'Could not launch $url';
  }
}

String format(String date) {
  final f = new DateFormat("dd/MM/yyyy 'a las' HH:mm");
  return f.format(DateTime.parse(date).add(Duration(hours: -4)));
}

getImage(Branch branch, Place place) {
  var image = branch != null && branch.image != null
      ? branch.image
      : 'https://dummyimage.com/700x400/3F51B5/fff.png?text=${getFirstLetters(place.name)}';

  if (place.name.contains("MyD")) {
    image = 'https://dummyimage.com/700x400/3F51B5/fff.png?text=M';
  }
  return image;
}
