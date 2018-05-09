<template>
    <v-card v-if="branch">
        <v-card-media v-if="branch.image" :src="branch.image" height="200px"></v-card-media>
        <v-card-media v-if="!branch.image"
                      :src="'https://dummyimage.com/700x400/3F51B5/fff.png?text=' + branch.place.name"
                      height="200px"></v-card-media>
        <v-card-title primary-title>
            <div>
                <h3 class="headline mb-0">
                    {{ branch.name }} - {{ branch.place.name }}
                </h3>
                <div>Compra de {{ branch.exchange.currency }}: {{ branch.exchange.purchasePrice | fn}}</div>
                <div>Venta de {{ branch.exchange.currency }}: {{ branch.exchange.salePrice | fn}}</div>
            </div>
        </v-card-title>

        <v-card-text>
            <v-container fluid>
                <v-layout row v-if="branch.email">
                    <v-flex xs4>
                        <v-subheader>Email</v-subheader>
                    </v-flex>
                    <v-flex xs8>
                        <v-subheader><a :href="'mailto:' + branch.email">{{ branch.email}}</a></v-subheader>
                    </v-flex>
                </v-layout>
                <v-layout row v-if="branch.phoneNumber">
                    <v-flex xs4>
                        <v-subheader>Telefono</v-subheader>
                    </v-flex>
                    <v-flex xs8>
                        <v-subheader>{{ branch.phoneNumber }}</v-subheader>
                    </v-flex>
                </v-layout>
                <v-layout row v-if="branch.schedule">
                    <v-flex xs4>
                        <v-subheader>Horario de atención</v-subheader>
                    </v-flex>
                    <v-flex xs8>
                        <v-subheader>{{ branch.schedule }}</v-subheader>
                    </v-flex>
                </v-layout>

            </v-container>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn :href="branch.gmaps" v-if="branch.gmaps" target="_blank" flat color="orange">
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

        constructor() {
            super();
            this.isSmall = window.innerWidth < 600;
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
