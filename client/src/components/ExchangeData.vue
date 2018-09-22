<template>
    <v-card v-if="data">
        <span v-if="data.place.type !== 'BANK'">
            <v-card-media v-if="data.branch.image" :src="data.branch.image" height="200px"></v-card-media>
            <v-card-media v-if="!data.branch.image"
                          :src="'https://dummyimage.com/700x400/3F51B5/fff.png?text=' + data.place.name"
                          height="200px"></v-card-media>
        </span>
        <span v-if="data.place.type === 'BANK'">
            <v-card-media :src="'https://dummyimage.com/700x400/3F51B5/fff.png?text=' + data.place.name"
                          height="200px"></v-card-media>
        </span>
        <v-card-title primary-title v-if="data.place.type !== 'BANK'">
            <div>
                <h3 class="headline mb-0 text-md-center">
                    {{ data.branch.name }} - {{ data.place.name }}
                </h3>
            </div>
        </v-card-title>

        <v-card-text>
            <v-container fluid>

                <v-layout row>
                    <v-flex xs4>
                        <v-subheader>Compra de {{ data.exchange.currency }}</v-subheader>
                    </v-flex>
                    <v-flex xs6>
                        <v-subheader class="text-xs-right"><b>{{ data.exchange.purchasePrice | fn}}</b></v-subheader>
                    </v-flex>
                </v-layout>
                <v-layout row>
                    <v-flex xs4>
                        <v-subheader>Venta de {{ data.exchange.currency }}</v-subheader>
                    </v-flex>
                    <v-flex xs6 class="text-xs-right">
                        <v-subheader class="text-xs-right"><b>{{ data.exchange.salePrice | fn}}</b></v-subheader>
                    </v-flex>
                </v-layout>
                <v-layout row v-if="data.branch && data.branch.email">
                    <v-flex xs4>
                        <v-subheader>Email</v-subheader>
                    </v-flex>
                    <v-flex xs8>
                        <v-subheader><a :href="'mailto:' + data.branch.email">{{ data.branch.email}}</a></v-subheader>
                    </v-flex>
                </v-layout>
                <v-layout row v-if="data.branch && data.branch.phoneNumber">
                    <v-flex xs4>
                        <v-subheader>Telefono</v-subheader>
                    </v-flex>
                    <v-flex xs8>
                        <v-subheader>{{ data.branch.phoneNumber }}</v-subheader>
                    </v-flex>
                </v-layout>
                <v-layout row v-if="data.branch && data.branch.schedule">
                    <v-flex xs4>
                        <v-subheader>Horario de atención</v-subheader>
                    </v-flex>
                    <v-flex xs8>
                        <v-subheader>{{ data.branch.schedule }}</v-subheader>
                    </v-flex>
                </v-layout>
                <v-layout row v-if="data.place.branches && data.place.branches.length">
                    <v-data-table
                            :headers="headers"
                            v-bind:pagination.sync="pagination"
                            hide-actions
                            no-data-text=""
                            :must-sort="true"
                            item-key="id"
                            :items="data.place.branches"
                            class="branches">
                        <template slot="items" slot-scope="props">
                            <tr>
                                <td class="text-xs-left name-column">
                                    <a :href="props.item.gmaps"
                                       v-if="props.item.gmaps"
                                       target="_blank">
                                        <v-icon>map</v-icon>
                                        <b>{{ props.item.name }}</b>
                                    </a>
                                    <span v-if="!props.item.gmaps">
                                        <b>{{ props.item.name }}</b>
                                    </span>

                                </td>
                                <td class="text-xs-right" v-html="getSchedule(props.item)"></td>
                                <td class="text-xs-right" v-html="getPhone(props.item)"></td>
                            </tr>
                        </template>
                    </v-data-table>
                </v-layout>
                <v-layout row v-if="data.place.branches && data.place.branches.length">
                    <div class="text-xs-center pt-2" style="width: 100%">
                        <v-pagination v-model="pagination.page"
                                      :length="Math.ceil(data.place.branches.length / pagination.rowsPerPage)"
                                      style="marign:auto"></v-pagination>
                    </div>
                </v-layout>

            </v-container>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn :href="data.branch.gmaps" v-if="data.place.type === 'BUREAU' && data.branch.gmaps" target="_blank"
                   flat
                   color="orange">
                Ir al mapa
            </v-btn>
            <v-btn flat color="blue" v-on:click="$emit('ok')">Aceptar</v-btn>
        </v-card-actions>
        <v-card-actions class="footpart">
            <v-container fluid>
                <v-layout row>
                    <small class="footnote">
                        Estos datos fueron obtenidos de la página web de cada casa de cambios.
                        Si tienes mas datos de esta casa de cambios, <a href="mailto:arturovolpe@gmail.com"> envianos un
                        mail</a>.
                    </small>
                </v-layout>
                <br/>
                <v-layout row>
                    <v-spacer></v-spacer>
                    <small class="footnote">
                        Consultado el {{ data.exchange.date | fd("DD/MM/YYYY [a las] HH:mm") }}
                    </small>
                    <v-spacer></v-spacer>
                </v-layout>
            </v-container>
        </v-card-actions>
    </v-card>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from 'vue-property-decorator';
    import {Branch} from "../api/ExchangeAPI";

    @Component
    export default class ExchangeData extends Vue {
        @Prop() private data: any;
        isSmall: boolean;
        headers: any[];
        pagination: { sortBy: string; descending: boolean; rowsPerPage: number };

        constructor() {
            super();
            this.isSmall = window.innerWidth < 600;

            this.headers = [
                {text: 'Nombre', value: 'branch.name', align: 'left', sortable: true,},
                {text: 'Horario', value: 'branch.schedule', sortable: false, class: 'text-xs-right'},
                {text: 'Teléfono', value: 'branch.phone', sortable: false, class: 'text-xs-right'},
            ];

            this.pagination = {sortBy: 'name', descending: false, rowsPerPage: 3};
        }

        getSchedule(branch: Branch) {
            if (!branch || !branch.schedule) return '';

            return branch.schedule
                .replace(/Lunes/, "L")
                .replace(/Martes/, "Ma")
                .replace(/Miercoles/, "Mi")
                .replace(/Jueves/, "J")
                .replace(/Viernes/, "V")
                .replace(/Sabado/, "S")
                .replace(/Domingo/, "D")


                .replace(/LUNES_VIERNES/, "L a V")
                .replace(/SBADO/, "S")
                .replace(/DOMINGO/, "D")
                .replace(/hs /, "")

                .replace(/0 a /, "0-")
                .replace(/\./, "<br />")
        }

        getPhone(branch: Branch) {
            if (!branch || !branch.phoneNumber) return '';

            return branch.phoneNumber
                .replace(/.*Cel.:/, "")
                .replace(/\(RA\)/, "")
                .replace(/\/.*/, "")
                .replace(/y .*/, "")
                .trim();

            ;

        }

    }
</script>

<style scoped>
    @media (max-width: 599px) {
        .container.fluid {
            padding: 0 !important;
        }
    }

    .footnote {
        text-align: center
    }

    .footpart {
        background-color: lightgray;
    }

</style>

<style>
    div.branches {
        width: 100%;
    }

    div.branches tbody td {
        padding: 0 4px !important;
    }
</style>
