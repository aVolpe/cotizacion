<template>
    <div>
        <v-card>
            <v-card-title>
                <v-layout justify-center align-center>
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
                        <td>{{ props.item.placeName }}</td>
                        <td>{{ props.item.branchName }}</td>
                        <td class="text-lg-right">{{ props.item.purchasePrice }}</td>
                        <td class="text-lg-right">{{ props.item.salePrice }}</td>
                        <td class="text-lg-right" v-if="props.item.gmapsLink">
                            <a :href="props.item.gmapsLink">
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
                {text: 'Sucursal', align: 'left', sortable: true, value: 'branchName'},
                {text: 'Compra', value: 'purchasePrice', sortable: true},
                {text: 'Venta', value: 'salePrice', sortable: true},
                {text: 'Ver en el mapa'}
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
                        row['gmapsLink'] = `https://www.google.com/maps/@${row.branchLatitude},${row.branchLongitude}`
                    else
                        row['gmapsLink'] = null;
                }
                this.data = data;
                this.loading = false;
            });
        }

        mounted() {
            this.loading = true;
            this.data = [];
            ExchangeAPI.getCurrencies().then((currencies: Array<string>) => {
                console.log('UPS');
                return;
                // this.currencies = currencies;
                // if (currencies.includes('USD')) this.currentCurrency = 'USD';
                // else this.currentCurrency = this.currencies[0];
                // this.load();
            });
        }
    }
</script>
