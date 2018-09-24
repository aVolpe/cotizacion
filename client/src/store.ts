import Vue from "vue";
import Vuex, { ActionTree, GetterTree, MutationTree } from "vuex";
import { Branch, ExchangeAPI, ExchangeDataDTO, QueryResponseDetail, Type, SingleExchangeData } from "@/api/ExchangeAPI";

Vue.use(Vuex);

const getGmap = (branch: Branch) => {

    if (branch && branch.latitude)
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

    branches: {
        [k: string]: Loaded<Branch>;
    };

    exchanges: {
        [k: string]: Loaded<ExchangeDataDTO>
    };

    current: {
        currency: string;
        amount: number;
    };

    exchangeDialog: {
        loading: boolean,
        show: boolean,
        data?: SingleExchangeData;
    };
}

export default new Vuex.Store<RootState>({

    state: {
        exchanges: {},
        branches: {},
        currencies: { loading: false, loaded: false },
        current: {
            currency: "USD",
            amount: 1
        },
        exchangeDialog: { loading: false, show: false }
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

        setAmount(state, amount: number) {
            state.current = {
                ...state.current,
                amount
            };
        },

        setBankBranches(state, { bank, branches }) {
            state.branches = {
                ...state.branches,
                [bank]: {
                    loaded: true,
                    loading: false,
                    data: branches
                }
            };
        },

        exchangeLoading(state, isoCode) {
            state.exchanges = {
                ...state.exchanges,
                [isoCode]: {
                    loading: true,
                    loaded: false
                }
            };
        },
        exchangeResult(state, payload: { isoCode: string, data: ExchangeDataDTO }) {
            state.exchanges = {
                ...state.exchanges,
                [payload.isoCode]: {
                    loading: false,
                    loaded: true,
                    data: payload.data
                }
            };
        },

        setExchangeData(state, payload: SingleExchangeData) {
            state.exchangeDialog = {
                show: true,
                loading: false,
                data: payload
            };
        },

        hideExchangeDialog(state) {
            state.exchangeDialog = {
                loading: false,
                show: false
            };
        }


    } as MutationTree<RootState>,

    actions: {

        hideExchangeDialog({ commit }) {
            commit("hideExchangeDialog");
        },

        setDialogData({ commit }, query: QueryResponseDetail) {
            commit("setExchangeData", {
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

        showExchangeData: ({ commit, dispatch, state }, query: QueryResponseDetail, force: boolean = false) => {

            if (query.place.type !== Type.Bank) {
                dispatch("setDialogData", query);
                return;
            }

            if (!force && state.branches[query.place.code] && state.branches[query.place.code].loaded) {
                dispatch("setDialogData", {
                    ...query,
                    place: {
                        ...query.place,
                        branches: state.branches[query.place.code].data
                    }
                });
            } else {
                ExchangeAPI.getBranches(query.place.code).then(data => {
                    data.forEach(d => d.gmaps = getGmap(d));
                    query.place.branches = data;
                    commit("setBankBranches", {
                        bank: query.place.code,
                        branches: data
                    });
                    dispatch("setDialogData", {
                        ...query,
                        place: {
                            ...query.place,
                            branches: data
                        }
                    });
                });
            }
        },

        setCurrentCurrency: ({ commit, dispatch }, currency: string) => {
            commit("setCurrency", currency);
            dispatch("fetchExchange", currency);
        },

        setAmount: ({ commit }, newAmount: number) => {
            commit("setAmount", newAmount);
        },


        fetchCurrencies: ({ commit, dispatch }) => {
            commit("beginLoadCurrencies");
            ExchangeAPI.getCurrencies().then(data => {
                commit("currenciesResult", data);

                let currency = data[0];
                if (data.includes("USD"))
                    currency = "USD";

                commit("setCurrency", currency);
                dispatch("fetchExchange", currency);
            });

        },

        fetchExchange: ({ commit, state }, isoCode: string, force: boolean = false) => {
            if (!force && state.exchanges[isoCode]) {
                return;
            }
            commit("exchangeLoading", isoCode);
            ExchangeAPI.getTodayExchange(isoCode).then((result: ExchangeDataDTO) => {

                if (result === null) return;

                const toRet: QueryResponseDetail[] = [];

                for (const row of result.data) {
                    if (row.branch) {
                        row.branch.place = row.place;
                        row.branch.gmaps = getGmap(row.branch);
                    }

                    toRet.push(row);

                }
                commit("exchangeResult", {
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

        currentCurrencyData(state): Loaded<ExchangeDataDTO> {
            if (!state.current.currency || !state.exchanges[state.current.currency])
                return { loading: false, loaded: false };
            return state.exchanges[state.current.currency];
        },

        branchesWithLocation(state): Loaded<ExchangeDataDTO> {
            if (!state.current.currency || !state.exchanges[state.current.currency])
                return { loading: false, loaded: false };
            const baseData = state.exchanges[state.current.currency];
            if (!baseData.loaded) {
                return baseData;
            }
            return {
                ...baseData,
                data: {
                    ...baseData.data!,
                    data: baseData.data!.data.filter(qr => qr.branch && qr.branch.gmaps)
                }
            };
        }


    } as GetterTree<RootState, RootState>
});
