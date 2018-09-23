import {APICaller} from '@/api/APICaller';

export interface ExchangeData {
    firstQueryResult: string;
    lastQueryResult: string;
    count: number;
    data: QueryResponseDetail[];
}

export interface QueryResponseDetail {
    id: number;
    place: Place;
    branch: Branch;
    queryId: number;
    queryDate: string;
    queryDetailId: number;
    salePrice: number;
    purchasePrice: number;
}

export interface Branch {
    id: number;
    latitude: number | null;
    longitude: number | null;
    phoneNumber: string;
    email: null | string;
    image: null | string;
    name: string;
    schedule: string;
    remoteCode: string;
    place?: Place;

    gmaps?: string | null;
    exchange?: {
        purchasePrice: number,
        salePrice: number,
        currency: string,
        date: string
    };
}

export interface Place {
    id: number;
    code: string;
    name: string;
    type: Type;
    branches?: Branch[];
}

export enum Type {
    Bank = 'BANK',
    Bureau = 'BUREAU'

}

export interface ExchangeData {
    place: Place;
    branch: Branch;
    exchange: {
        purchasePrice: number;
        salePrice: number;
        currency: string;
        date: Date;
    };
}


export class ExchangeAPI {

    public static getTodayExchange(isoCode: string): Promise<ExchangeData> {
        return APICaller.doGet(`exchange/${isoCode}`);
    }

    public static getCurrencies(): Promise<string[]> {
        return APICaller.doGet(`exchange/`);
    }

    public static getBranches(code: string): Promise<Branch[]> {
        return APICaller.doGet(`places/${code}/branches`);
    }
}
