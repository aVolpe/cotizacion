import {APICaller} from "@/api/APICaller";

export class LicenseAPI {

    public static get(): Promise<any> {
        return APICaller.doGet(`licenses`);
    }

}
