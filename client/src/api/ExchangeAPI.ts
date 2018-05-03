import {APICaller} from '@/api/APICaller';

export class ExchangeAPI {

    public static getTodayExchange(isoCode: string) {
        return APICaller.doGet(`exchange/${isoCode}`);
    }

    static getCurrencies() {
        return APICaller.doGet(`exchange/`);
    }
}