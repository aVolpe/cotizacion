import 'package:intl/intl.dart';

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

  return base.split(' ')
    .map((word) => word[0].toLowerCase())
    .join('');
}
