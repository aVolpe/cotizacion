class ExchangeData {
  String firstQueryResult;
  String lastQueryResult;
  int count;
  List<Data> data;

  ExchangeData(
      {this.firstQueryResult, this.lastQueryResult, this.count, this.data});

  ExchangeData.fromJson(Map<String, dynamic> json) {
    firstQueryResult = json['firstQueryResult'];
    lastQueryResult = json['lastQueryResult'];
    count = json['count'];
    if (json['data'] != null) {
      data = new List<Data>();
      json['data'].forEach((v) {
        data.add(new Data.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['firstQueryResult'] = this.firstQueryResult;
    data['lastQueryResult'] = this.lastQueryResult;
    data['count'] = this.count;
    if (this.data != null) {
      data['data'] = this.data.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class Data {
  int id;
  Place place;
  Branch branch;
  int queryId;
  String queryDate;
  int queryDetailId;
  int salePrice;
  int purchasePrice;

  Data(
      {this.id,
      this.place,
      this.branch,
      this.queryId,
      this.queryDate,
      this.queryDetailId,
      this.salePrice,
      this.purchasePrice});

  Data.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    place = json['place'] != null ? new Place.fromJson(json['place']) : null;
    branch =
        json['branch'] != null ? new Branch.fromJson(json['branch']) : null;
    queryId = json['queryId'];
    queryDate = json['queryDate'];
    queryDetailId = json['queryDetailId'];
    salePrice = json['salePrice'];
    purchasePrice = json['purchasePrice'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    if (this.place != null) {
      data['place'] = this.place.toJson();
    }
    if (this.branch != null) {
      data['branch'] = this.branch.toJson();
    }
    data['queryId'] = this.queryId;
    data['queryDate'] = this.queryDate;
    data['queryDetailId'] = this.queryDetailId;
    data['salePrice'] = this.salePrice;
    data['purchasePrice'] = this.purchasePrice;
    return data;
  }
}

class Place {
  int id;
  String code;
  String name;
  String type;

  Place({this.id, this.code, this.name, this.type});

  Place.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    code = json['code'];
    name = json['name'];
    type = json['type'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['code'] = this.code;
    data['name'] = this.name;
    data['type'] = this.type;
    return data;
  }
}

class Branch {
  int id;
  double latitude;
  double longitude;
  String phoneNumber;
  String email;
  String image;
  String name;
  String schedule;
  String remoteCode;

  Branch(
      {this.id,
      this.latitude,
      this.longitude,
      this.phoneNumber,
      this.email,
      this.image,
      this.name,
      this.schedule,
      this.remoteCode});

  Branch.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    phoneNumber = json['phoneNumber'];
    email = json['email'];
    image = json['image'];
    name = json['name'];
    schedule = json['schedule'];
    remoteCode = json['remoteCode'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['phoneNumber'] = this.phoneNumber;
    data['email'] = this.email;
    data['image'] = this.image;
    data['name'] = this.name;
    data['schedule'] = this.schedule;
    data['remoteCode'] = this.remoteCode;
    return data;
  }
}
