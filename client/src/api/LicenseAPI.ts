import {APICaller} from '@/api/APICaller';

export class LicenseAPI {

    public static get() {
        return APICaller.doGet(`licenses`);
    }

}
