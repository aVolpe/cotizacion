import Vue from 'vue';
import Vuex, {ActionTree, GetterTree, MutationTree} from 'vuex';
import {Branch, ExchangeAPI, ExchangeData, QueryResponseDetail, Type} from '@/api/ExchangeAPI';

Vue.use(Vuex);

const getGmap = (branch: Branch) => {

    if (branch.latitude)
        return `https://www.google.com/maps/search/?api=1&query=${branch.latitude},${branch.longitude}`;
    else
        return null;
};

export interface Loaded<T> {
    loading: boolean;
    loaded: boolean;
    data?: T;
}

export interface RootState {
    currencies: Loaded<string[]>;
    exchanges: {
        [k: string]: Loaded<QueryResponseDetail[]>
    };

    current: {
        currency: string;
        amount: number;
    };

    exchangeDialog: {
        loading: boolean,
        show: boolean,
        data?: ExchangeData;
    };
}

export default new Vuex.Store<RootState>({

    state: {
        exchanges: {},
        currencies: {loading: false, loaded: false},
        current: {
            currency: 'USD',
            amount: 1
        },
        exchangeDialog: {loading: false, show: false}
    },

    mutations: {
        beginLoadCurrencies(state) {
            state.currencies.loading = true;
        },

        currenciesResult(state, data: string[]) {
            state.currencies = {
                loading: false,
                loaded: true,
                data
            };
        },

        setCurrency(state, isoCode: string) {
            state.current = {
                ...state.current,
                currency: isoCode
            };
        },

        exchangeResult(state, payload: { isoCode: string, data: QueryResponseDetail[] }) {
            state.exchanges[payload.isoCode] = {
                loading: false,
                loaded: true,
                data: payload.data
            };
        },

        setExchangeData(state, payload: ExchangeData) {
            state.exchangeDialog = {
                show: true,
                loading: false,
                data: payload
            };
        },

        closeExchangeDialog(state) {
            state.exchangeDialog = {
                loading: false,
                show: false
            };
        }


    } as MutationTree<RootState>,

    actions: {

        closeExchangeDialog({commit}) {
            commit('closeExchangeDialog');
        },

        setDialogData({commit}, query: QueryResponseDetail) {
            commit('setExchangeData', {
                place: query.place,
                branch: query.branch,
                exchange: {
                    purchasePrice: query.purchasePrice,
                    salePrice: query.salePrice,
                    currency: this.currentCurrency,
                    date: query.queryDate
                }
            });
        },

        showExchangeData: ({commit, dispatch}, query: QueryResponseDetail) => {

            if (query.place.type === Type.Bank)
                ExchangeAPI.getBranches(query.place.code).then(data => {
                    data.forEach(d => d.gmaps = getGmap(d));
                    query.place.branches = data;
                    dispatch('setDialogData', {
                        ...query,
                        place: {
                            ...query.place,
                            branches: data
                        }
                    });
                });
            else
                dispatch('setDialogData', query);
        },


        fetchCurrencies: ({commit, dispatch}) => {
            commit('beginLoadCurrencies');
            ExchangeAPI.getCurrencies().then(data => {
                commit('currenciesResult', data);

                let currency = data[0];
                if (data.includes('USD'))
                    currency = 'USD';

                commit('setCurrency', currency);
                dispatch('fetchExchange', currency);
            });

        },

        fetchExchange: ({commit}, isoCode: string) => {
            commit('exchangeLoading', isoCode);
            ExchangeAPI.getTodayExchange(isoCode).then(result => {

                if (result === null) return;

                const toRet: QueryResponseDetail[] = [];

                for (const row of result.data) {
                    if (row.branch)
                        row.branch.place = row.place;
                    row.branch.gmaps = getGmap(row.branch);

                    toRet.push(row);

                }
                commit('exchangeResult', {
                    isoCode,
                    data: {
                        ...result,
                        lastQueryResult: result.lastQueryResult,
                        firstQueryResult: result.firstQueryResult,
                        count: result.count,
                        data: toRet
                    }
                });
            });
        }
    } as ActionTree<RootState, RootState>,

    getters: {

        current(state): Loaded<QueryResponseDetail[]> {
            if (!state.current.currency)
                return {loading: false, loaded: false};
            return state.exchanges[state.current.currency];
        }


    } as GetterTree<RootState, RootState>
});
