import Vue from "vue";
import Vuex, {ActionTree, GetterTree, MutationTree} from "vuex";
import {
    Branch,
    ExchangeAPI,
    ExchangeDataDTO,
    Place,
    QueryResponseDetail,
    SingleExchangeData,
    Type
} from "@/api/ExchangeAPI";
import router from "./router";

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
        place?: number;
    };

    exchangeDialog: {
        loading: boolean,
        show: boolean,
        data?: SingleExchangeData;
    };
}

const ssr: any = (window as any).ssr || {
    exchanges: undefined,
    branches: undefined,
    currencies: undefined,
    current: undefined
};

export default new Vuex.Store<RootState>({

    state: {
        exchanges: ssr.exchanges || {},
        branches: ssr.branches || {},
        currencies: ssr.currencies || {loading: false, loaded: false},
        current: ssr.current || {
            currency: "USD",
            amount: 1,
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

        setAmount(state, amount: number) {
            state.current = {
                ...state.current,
                amount
            };
        },

        setBankBranches(state, {bank, branches}) {
            state.branches = {
                ...state.branches,
                [bank]: {
                    loaded: true,
                    loading: false,
                    data: branches
                }
            };
        },

        setCurrentPlace(state, placeId?: number) {
            state.current = {
                ...state.current,
                place: placeId ? placeId : undefined
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

        hideExchangeDialog({commit}) {
            commit("hideExchangeDialog");
        },

        setDialogData({commit, state}, query: QueryResponseDetail) {
            commit("setExchangeData", {
                place: query.place,
                branch: query.branch,
                exchange: {
                    purchasePrice: query.purchasePrice,
                    salePrice: query.salePrice,
                    currency: state.current.currency,
                    date: query.queryDate
                }
            });
        },

        showExchangeData: ({commit, dispatch, state}, query: QueryResponseDetail, force: boolean = false) => {

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

        setCurrentCurrency: ({commit, dispatch}, currency: string) => {
            router.replace({
                query: {
                    moneda: currency
                }
            });
            commit("setCurrency", currency);
            dispatch("fetchExchange", currency);
        },

        setAmount: ({commit}, newAmount: number) => {
            commit("setAmount", newAmount);
        },

        setCurrentPlace: ({commit}, placeId: number) => {
            commit("setCurrentPlace", placeId > 0 ? placeId : undefined);
        },

        clearCurrentPlace: ({commit}) => {
            commit("setCurrentPlace");
        },

        init: ({commit, dispatch}) => {
            dispatch("fetchCurrencies");
            dispatch("fetchExchange", "USD");
            commit("currenciesResult", ["USD"]);
        },

        fetchCurrencies: ({commit, dispatch}) => {
            commit("beginLoadCurrencies");
            ExchangeAPI.getCurrencies().then(data => {
                commit("currenciesResult", data);
            });

        },

        fetchCurrencyData: ({commit, dispatch, state}, currency: string = "USD") => {
            if (state.current.currency === currency) {
                commit("setCurrency", currency);
            }
            const exchanges = (state as RootState).exchanges;

            if (currency in exchanges  // if the exchange was queried
                && exchanges[currency].loaded  // if the exchange was loaded
                && new Date(exchanges[currency].data!.lastQueryResult) > addMinutes(new Date(), -15) //
            ) {
                console.debug("Don't reload date because is was fetched recently");
            } else {
                dispatch("fetchExchange", currency);
            }

        },


        fetchExchange: ({commit, state, dispatch}, isoCode: string, force: boolean = false) => {
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
                if (state.current.place) {

                    const countOfCurrentPlace = toRet.filter(tr => tr.place.id === state.current.place).length;
                    if (countOfCurrentPlace === 0) {
                        dispatch("clearCurrentPlace");
                    }
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
                return {loading: false, loaded: false};
            return state.exchanges[state.current.currency];
        },

        filteredCurrencies(state): Loaded<ExchangeDataDTO> {

            if (!state.current.currency || !state.exchanges[state.current.currency])
                return {loading: false, loaded: false};
            const resource = state.exchanges[state.current.currency];

            const filteredPlace = state.current.place;
            if (!filteredPlace || !resource.data)
                return resource;

            return {
                ...resource,
                data: {
                    ...resource.data,
                    data: resource.data.data.filter((l: QueryResponseDetail) => l.place.id === filteredPlace)
                }
            };
        },

        currentPlaces(state): Place[] {
            if (!state.current.currency || !state.exchanges[state.current.currency])
                return [];
            const resource = state.exchanges[state.current.currency];

            if (!resource.data)
                return [];

            const uniq: { [k: number]: Place } = {};
            resource.data.data.map(l => l.place)
                .forEach(l => uniq[l.id] = l);
            return Object.values(uniq)
                .sort((i1, i2) => i1.name.localeCompare(i2.name));

        },

        branchesWithLocation(state): Loaded<ExchangeDataDTO> {
            if (!state.current.currency || !state.exchanges[state.current.currency])
                return {loading: false, loaded: false};
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

function addMinutes(date: Date, minutes: number): Date {
    return new Date(date.getTime() + minutes * 60000);
}
