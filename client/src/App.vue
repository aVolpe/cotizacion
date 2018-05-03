<template>
    <v-app id="inspire">
        <v-navigation-drawer right floating dark v-model="drawer" app>
            <v-list dense>
                <v-list-tile>
                    <v-list-tile-action>
                        <v-icon>home</v-icon>
                    </v-list-tile-action>
                    <v-list-tile-content>
                        <v-list-tile-title>Home</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
                <v-list-tile>
                    <v-list-tile-action>
                        <v-icon>contact_mail</v-icon>
                    </v-list-tile-action>
                    <v-list-tile-content>
                        <v-list-tile-title>Contact</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
            </v-list>
        </v-navigation-drawer>
        <v-toolbar color="primary" dark fixed app>
            <v-spacer></v-spacer>
            <v-toolbar-title>Cotizaciones</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
        </v-toolbar>
        <v-content>
            <v-container fluid>
                <v-layout justify-center align-center>
                    <v-flex text-xs-center xs12 md7 mt-1>
                        <router-view/>
                    </v-flex>
                </v-layout>
            </v-container>
        </v-content>
        <v-footer light app inline>
            <v-spacer></v-spacer>
            <span>
                <a href="https://www.github.com/avolpe/cotizacion" target="_blank">
                    &copy; 2018
                </a>
            </span>
            <v-spacer></v-spacer>
        </v-footer>
    </v-app>
</template>
<script lang="ts">

    import {Component, Vue} from 'vue-property-decorator';
    import 'vuetify/dist/vuetify.min.css' // Ensure you are using css-loader


    @Component({
        components: {},
    })
    export default class App extends Vue {

        drawer: boolean = false;
    }
</script>

