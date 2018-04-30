export class ExchangeAPI {

    public static getTodayExchange(isoCode: string) {
        return fetch(`http://localhost:8080/api/exchange/${isoCode}`).then(d => d.json());
    }

    static getCurrencies() {
        return fetch('http://localhost:8080/api/exchange/').then(d => d.json());
    }
}