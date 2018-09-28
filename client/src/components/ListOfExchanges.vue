<template>
    <v-card>
        <v-card-title class="title">
            <v-layout justify-center align-center>
                <v-flex text-xs-center xs12 md3>
                    Cambio de
                </v-flex>
                <v-flex text-xs-center xs12 md2>
                    <v-text-field v-model="current.amount"
                                  label="Cantidad"
                                  align="rigth"
                                  reverse
                                  class="text-xs-right"
                                  single-line></v-text-field>
                </v-flex>
                <v-flex text-xs-center xs12 md2 v-if="currencies.loaded">
                    <v-select :items="currencies.data"
                              reverse
                              v-model="current.currency"
                              v-on:change="setCurrentCurrency"
                              align="center"
                              label="Moneda"
                              single-line></v-select>
                </v-flex>
            </v-layout>
        </v-card-title>

        <v-card-text class="main-table-wrapper">
            <v-data-table
                    :headers="headers"
                    :loading="filteredCurrencies.loading"
                    v-bind:pagination.sync="pagination"
                    hide-actions
                    no-data-text=""
                    :must-sort="true"
                    item-key="id"
                    :items="filteredCurrencies.data && filteredCurrencies.data.data">
                <template slot="no-data">
                    <v-alert :value="true" color="warning" icon="warning" v-if="filteredCurrencies.loaded">
                        No hay datos de hoy
                    </v-alert>
                </template>
                <template slot="progress">
                    <br/>
                    <v-progress-circular color="blue" :size="70" :width="7" indeterminate></v-progress-circular>
                </template>
                <template slot="headerCell" slot-scope="props">
                <span v-if="props.header.value === 'placeName'">

                    <v-select :items="placesWithEmpty"
                              v-model="current.place"
                              ref="placeSelect"
                              v-on:change="setCurrentPlace"
                              align="center"
                              label="Casa"
                              item-text="name"
                              item-value="id"
                              single-line>
                    </v-select>

                </span>
                    <span v-else>{{ props.header.text }}</span>

                </template>


                <template slot="items" slot-scope="props">
                    <tr @click="props.expanded = !props.expanded">
                        <td class="text-xs-left name-column">
                            <b>{{ props.item.place.name }}</b>
                            <br/>
                            <span v-if="props.item.place.type === 'BANK'">Ver sucursales</span>
                            <span v-if="props.item.place.type !== 'BANK'">{{ props.item.branch.name }}</span>
                        </td>
                        <td class="text-xs-right">{{ props.item.purchasePrice | multiply(current.amount) | fn}}</td>
                        <td class="text-xs-right">{{ props.item.salePrice | multiply(current.amount) |fn}}</td>
                        <td class="text-xs-right" v-if="!isSmall">
                            <a v-on:click="showExchangeData(props.item)">
                                <v-icon dark color="primary" slot="activator">info</v-icon>
                            </a>
                            <a :href="props.item.branch.gmaps"
                               v-if="props.item.place.type === 'BUREAU' && props.item.branch.gmaps" target="_blank">
                                <v-icon>map</v-icon>
                            </a>
                        </td>
                    </tr>
                </template>
                <template slot="expand" slot-scope="props" v-if="isSmall">
                    <v-btn v-on:click="showExchangeData(props.item)" flat>
                        Ver detalles
                        <v-icon>map</v-icon>
                    </v-btn>
                </template>
            </v-data-table>
            <small v-if="filteredCurrencies.loaded">
                <b>{{ filteredCurrencies.data.count }}</b> cotizaciones consultadas
                <b> {{ filteredCurrencies.data.firstQueryResult | mfn }}</b>.
            </small>

        </v-card-text>
    </v-card>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import ExchangeData from "@/components/ExchangeData.vue";
    import {Meta} from "@/decorators";
    import {Action, Getter, State} from "vuex-class";
    import {Loaded} from "@/store";
    import {QueryResponseDetail} from "@/api/ExchangeAPI";
    import {Place} from "../api/ExchangeAPI";

    @Component
    export default class ListOfExchanges extends Vue {
        @State currencies!: Loaded<string[]>;
        @State current!: { currency: string; amount: number };

        @Action showExchangeData!: () => void;
        @Action fetchCurrencies!: () => void;
        @Action setCurrentCurrency!: (isoCode: string) => void;
        @Action setCurrentPlace!: (place: Place) => void;
        @Action clearCurrentPlace!: () => void;

        @Getter filteredCurrencies!: Loaded<ExchangeData[]>;
        @Getter currentPlaces!: Place[];

        placesWithEmpty: Place[] = [];

        headers: Array<{
            text?: string;
            value?: string;
            align?: "left" | "center" | "right";
            sortable?: boolean;
            class?: string[] | string;
            width?: string;
        }>;
        pagination: any;
        isSmall: boolean;

        constructor() {
            super();

            this.isSmall = window.innerWidth < 600;
            this.headers = [
                {
                    text: "",
                    align: "left",
                    sortable: false,
                    value: "placeName",
                    width: "35%",
                    class: "place"
                }, {
                    text: "Compra",
                    value: "purchasePrice",
                    sortable: true,
                    class: "text-xs-right"
                }, {
                    text: "Venta",
                    value: "salePrice",
                    sortable: true,
                    class: "text-xs-right"
                }
            ];
            if (!this.isSmall)
                this.headers.push({text: " ", value: "", sortable: false});

            this.pagination = {
                sortBy: "purchasePrice",
                descending: true,
                rowsPerPage: -1
            };
        }

        @Watch("currentPlaces", {immediate: true})
        setLocalPlace(places: Place[]) {
            this.placesWithEmpty = [{
                name: "Todos",
                id: -1
            } as Place].concat(places);
        }
    }
</script>

<style>
    @media (max-width: 599px) {
        .main-table-wrapper {
            padding: 10px !important;
        }

        .name-column {
            padding: 3px 10px !important;
        }

        .title {
            padding-top: 1px !important;
            padding-bottom: 1px !important;
        }
    }

    .amount-input > input {
        text-align: right;
    }

    .amount-input > label {
        text-align: right;
    }

    .input-group__input > input {
        text-align: right !important;
    }

    .input-group__selections__comma {
        margin-left: auto;
        margin-right: auto;
    }

    a,
    u {
        text-decoration: none;
    }

    th.place {
        padding: 0 0 0 10px !important;
    }
</style>

