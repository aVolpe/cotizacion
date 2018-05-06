<template>
    <div>
        <v-card>
            <v-card-title class="title">
                <v-layout justify-center align-center>
                    <v-flex text-xs-center xs12 md3 mt-1>
                        Cotizaciones de
                    </v-flex>
                    <v-flex text-xs-center xs12 md3 mt-1>
                        <v-select :items="currencies"
                                  v-model="currentCurrency"
                                  v-on:change="changeCurrency"
                                  align="center"
                                  label="Moneda"
                                  single-line></v-select>
                    </v-flex>
                </v-layout>
            </v-card-title>

            <v-card-text class="main-table-wrapper">
                {{ isSmall }}
                <v-data-table
                        :headers="headers"
                        :loading="loading"
                        v-bind:pagination.sync="pagination"
                        hide-actions
                        no-data-text="Cargando ..."
                        :must-sort="true"
                        item-key="branchId"
                        :items="data && data.data"
                        class="elevation-1 ">
                    <template slot="no-data">
                        <v-alert :value="true" color="warning" icon="warning" v-if="!loading">
                            No hay datos de hoy
                        </v-alert>
                    </template>
                    <v-progress-linear slot="progress" color="blue" indeterminate></v-progress-linear>


                    <template slot="items" slot-scope="props">
                        <tr @click="props.expanded = !props.expanded">
                            <td class="text-xs-left name-column">
                                <b>{{ props.item.placeName }}</b>
                                <br/>
                                {{ props.item.branchName }}
                            </td>
                            <td class="text-xs-right">{{ props.item.purchasePrice | fn}}</td>
                            <td class="text-xs-right">{{ props.item.salePrice | fn}}</td>
                            <td class="text-xs-right" v-if="!isSmall">
                                <v-tooltip bottom>
                                    <v-icon dark color="primary" slot="activator">info</v-icon>
                                    <span>Consultado el {{ props.item.queryDate | fd("DD/MM/YYYY [a las] HH:mm") }}</span>
                                </v-tooltip>
                                <a :href="props.item.gmapsLink" v-if="props.item.gmapsLink" target="_blank">
                                    <v-icon>map</v-icon>
                                </a>
                            </td>
                        </tr>
                    </template>
                    <template slot="expand" slot-scope="props">
                        <v-card flat>
                            <v-card-text>
                                <span>Consultado el {{ props.item.queryDate | fd("DD/MM/YYYY [a las] HH:mm") }}</span>
                                <br />
                                <a :href="props.item.gmapsLink" v-if="props.item.gmapsLink" target="_blank">
                                    Ver ubicación <v-icon>map</v-icon>
                                </a>
                            </v-card-text>
                        </v-card>
                    </template>
                </v-data-table>
                <small v-if="data">
                    <b>{{ data.count }}</b> cotizaciones consultadas
                    <b> {{ data.firstQueryResult | mfn }}</b>.
                </small>

            </v-card-text>
        </v-card>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from 'vue-property-decorator';
    import {ExchangeAPI} from "../api/ExchangeAPI"; // @ is an alias to /src

    @Component({
        components: {},
    })
    export default class LatestExchange extends Vue {

        currencies: Array<string>;
        currentCurrency: string;
        data: {
            firstQueryResult: number,
            lastQueryResult: number,
            count: number,
            data: Array<any>
        };
        headers: Array<any>;
        loading = false;
        pagination: any;
        isSmall: boolean;

        constructor() {
            super();

            this.currencies = ['USD', 'EUR'];
            this.currentCurrency = this.currencies[0];
            this.data = this.buildEmptyData();

            this.isSmall = window.innerWidth < 600;
            this.headers = [
                {text: '', align: 'left', sortable: true, value: 'placeName'},
                {text: 'Compra', value: 'purchasePrice', sortable: true, class: 'text-xs-right'},
                {text: 'Venta', value: 'salePrice', sortable: true, class: 'text-xs-right'}
            ];
            if (!this.isSmall) {
                this.headers.push({text: ' ', value: '', sortable: false});
            }
            this.pagination = {'sortBy': 'purchasePrice', 'descending': false, 'rowsPerPage': -1};
        }

        buildEmptyData() {
            return {
                data: [],
                count: 0,
                firstQueryResult: 0,
                lastQueryResult: 0
            };
        }

        changeCurrency(newCur: string) {
            this.currentCurrency = newCur;
            this.load();
        }

        private load() {
            this.loading = true;
            this.data = this.buildEmptyData();
            ExchangeAPI.getTodayExchange(this.currentCurrency).then(result => {

                this.data = result;
                if (this.data === null) return;

                for (let row of this.data.data) {
                    if (row.branchLatitude)
                        row['gmapsLink'] = `https://www.google.com/maps/search/?api=1&query=${row.branchLatitude},${row.branchLongitude}`;
                    else
                        row['gmapsLink'] = null;
                }
                this.loading = false;
            });
        }

        mounted() {
            this.loading = true;
            ExchangeAPI.getCurrencies().then((currencies: Array<string>) => {
                this.currencies = currencies;
                if (currencies.includes('USD')) this.currentCurrency = 'USD';
                else this.currentCurrency = this.currencies[0];
                this.load();
            });
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

        .input-group__selections__comma {
            margin-left: auto;
            margin-right: auto;
        }
    }

</style>
