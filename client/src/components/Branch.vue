<template>
    <v-card v-if="branch">
        <span v-if="branch.place.type !== 'BANK'">
            <v-card-media v-if="branch.image" :src="branch.image" height="200px"></v-card-media>
            <v-card-media v-if="!branch.image"
                          :src="'https://dummyimage.com/700x400/3F51B5/fff.png?text=' + branch.place.name"
                          height="200px"></v-card-media>
        </span>
        <v-card-title primary-title>
            <div>
                <h3 class="headline mb-0">
                    <span v-if="branch.place.type === 'BANK'">{{ branch.place.name }}</span>
                    <span v-if="branch.place.type !== 'BANK'">{{ branch.name }} - {{ branch.place.name }}</span>

                </h3>
            </div>
        </v-card-title>

        <v-card-text>
            <v-container fluid>

                <v-layout row>
                    <v-flex xs4>
                        <v-subheader>Compra de {{ branch.exchange.currency }}</v-subheader>
                    </v-flex>
                    <v-flex xs8>
                        <v-subheader><b>{{ branch.exchange.purchasePrice | fn}}</b></v-subheader>
                    </v-flex>
                </v-layout>
                <v-layout row>
                    <v-flex xs4>
                        <v-subheader>Venta de {{ branch.exchange.currency }}</v-subheader>
                    </v-flex>
                    <v-flex xs8>
                        <v-subheader><b>{{ branch.exchange.salePrice | fn}}</b></v-subheader>
                    </v-flex>
                </v-layout>
                <v-layout row v-if="branch.email">
                    <v-flex xs4>
                        <v-subheader>Email</v-subheader>
                    </v-flex>
                    <v-flex xs8>
                        <v-subheader><a :href="'mailto:' + branch.email">{{ branch.email}}</a></v-subheader>
                    </v-flex>
                </v-layout>
                <v-layout row v-if="branch.phoneNumber && branch.place.type === 'BUREAU'">
                    <v-flex xs4>
                        <v-subheader>Telefono</v-subheader>
                    </v-flex>
                    <v-flex xs8>
                        <v-subheader>{{ branch.phoneNumber }}</v-subheader>
                    </v-flex>
                </v-layout>
                <v-layout row v-if="branch.schedule && branch.place.type === 'BUREAU'">
                    <v-flex xs4>
                        <v-subheader>Horario de atención</v-subheader>
                    </v-flex>
                    <v-flex xs8>
                        <v-subheader>{{ branch.schedule }}</v-subheader>
                    </v-flex>
                </v-layout>
                <v-layout row v-if="branch.place.branches && branch.place.branches.length">
                    <v-data-table
                            :headers="headers"
                            v-bind:pagination.sync="pagination"
                            hide-actions
                            no-data-text=""
                            :must-sort="true"
                            item-key="id"
                            :items="branch.place.branches"
                            class="elevation-1 branches">
                        <template slot="items" slot-scope="props">
                            <tr>
                                <td class="text-xs-left name-column">
                                    <a :href="props.item.branch.gmaps"
                                       v-if="props.item.branch.gmaps"
                                       target="_blank">
                                        <v-icon>map</v-icon>
                                        <b>{{ props.item.branch.name }}</b>
                                    </a>
                                    <span v-if="!props.item.branch.gmaps">
                                        <b>{{ props.item.branch.name }}</b>
                                    </span>

                                </td>
                                <td class="text-xs-right">{{ props.item.branch.schedule }}</td>
                                <td class="text-xs-right">{{ props.item.branch.phoneNumber }}</td>
                            </tr>
                        </template>
                    </v-data-table>
                </v-layout>
                <v-layout row v-if="branch.place.branches && branch.place.branches.length">
                    <div class="text-xs-center pt-2" style="width: 100%">
                        <v-pagination v-model="pagination.page"
                                      :length="Math.ceil(branch.place.branches.length / pagination.rowsPerPage)"
                                      style="marign:auto"></v-pagination>
                    </div>
                </v-layout>

            </v-container>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn :href="branch.gmaps" v-if="branch.place.type === 'BUREAU' && branch.gmaps" target="_blank" flat
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
                        Consultado el {{ branch.exchange.date | fd("DD/MM/YYYY [a las] HH:mm") }}
                    </small>
                    <v-spacer></v-spacer>
                </v-layout>
            </v-container>
        </v-card-actions>
    </v-card>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from 'vue-property-decorator';

    @Component
    export default class Branch extends Vue {
        @Prop() private branch: any;
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
    div.branches tbody td {
        padding: 0 4px !important;
    }
</style>
