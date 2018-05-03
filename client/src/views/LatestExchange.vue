<template>
    <div>
        <v-card>
            <v-card-title>
                <v-layout justify-center align-center>
                    <v-flex text-xs-center xs12 md3 mt-1>
                        Cotizaciones de
                    </v-flex>
                    <v-flex text-xs-center xs12 md3 mt-1>
                        <v-select :items="currencies"
                                  v-model="currentCurrency"
                                  v-on:change="changeCurrency"
                                  class="bx-5"
                                  label="Moneda"
                                  single-line></v-select>
                    </v-flex>
                </v-layout>
            </v-card-title>

            <v-card-text>
                <v-data-table
                        :headers="headers"
                        :loading="loading"
                        v-bind:pagination.sync="pagination"
                        hide-actions
                        :must-sort="true"
                        :items="data"
                        class="elevation-1">
                    <template slot="no-data">
                        <v-alert :value="true" color="error" icon="warning">
                            No hay datos de hoy
                        </v-alert>
                    </template>
                    <v-progress-linear slot="progress" color="blue" indeterminate></v-progress-linear>

                    <template slot="items" slot-scope="props">
                        <td class="text-xs-left">
                            <b>{{ props.item.placeName }}</b>
                            <br/>
                            {{ props.item.branchName }}
                        </td>
                        <td class="text-lg-right">{{ props.item.purchasePrice | fn}}</td>
                        <td class="text-lg-right">{{ props.item.salePrice | fn}}</td>
                        <td class="text-lg-right">
                            <v-tooltip bottom>
                                <v-icon dark color="primary" slot="activator">info</v-icon>
                                <span>Consultado el {{ props.item.queryDate | fd("YYYY/MM/DD [a las] HH:mm") }}</span>
                            </v-tooltip>
                            <a :href="props.item.gmapsLink" v-if="props.item.gmapsLink" target="_blank">
                                <v-icon>map</v-icon>
                            </a>
                        </td>
                    </template>
                </v-data-table>

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
        data: any;
        headers: Array<any>;
        loading = false;
        pagination: any;

        constructor() {
            super();

            this.currencies = ['USD', 'EUR'];
            this.currentCurrency = this.currencies[0];
            this.data = [];
            this.headers = [
                {text: 'Casa de cambios', align: 'left', sortable: true, value: 'placeName'},
                {text: 'Compra', value: 'purchasePrice', sortable: true},
                {text: 'Venta', value: 'salePrice', sortable: true},
                {text: ''}
            ];
            this.pagination = {'sortBy': 'purchasePrice', 'descending': false, 'rowsPerPage': -1};
        }

        changeCurrency(newCur: string) {
            this.currentCurrency = newCur;
            this.load();
        }

        private load() {
            this.loading = true;
            ExchangeAPI.getTodayExchange(this.currentCurrency).then(data => {
                for (let row of data) {
                    if (row.branchLatitude)
                        row['gmapsLink'] = `https://www.google.com/maps/search/?api=1&query=${row.branchLatitude},${row.branchLongitude}`;
                    else
                        row['gmapsLink'] = null;
                }
                this.data = data;
                this.loading = false;
                console.log(this.data);
            });
        }

        mounted() {
            this.loading = true;
            this.data = [];
            ExchangeAPI.getCurrencies().then((currencies: Array<string>) => {
                this.currencies = currencies;
                if (currencies.includes('USD')) this.currentCurrency = 'USD';
                else this.currentCurrency = this.currencies[0];
                this.load();
            });
        }
    }
</script>
