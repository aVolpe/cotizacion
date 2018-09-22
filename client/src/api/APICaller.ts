export class APICaller {

    public static goTo(path: string) {
        window.location.href = `${process.env.BASE_URL}#/${path}`;
    }

    public static handleError(response: Response) {
        if (response.status >= 200 && response.status < 300) {
            return response;
        }


        const error: any = new Error(response.statusText);
        error.response = response;
        throw error;
    }

    public static handleFirstError(err: any) {
        if (err && err.message === 'Failed to fetch') {
            this.goTo('no-back');
        }
        throw err;
    }

    public static doGet(url: string): Promise<any> {

        return fetch(`${process.env.VUE_APP_API}/${url}`)
            .then(this.handleError)
            .then(okResponse => okResponse.json())
            .catch(err => this.handleFirstError(err))
    }
}