<template>
    <v-app id="inspire">
        <v-navigation-drawer right floating v-model="drawer" app>
            <v-list dense>
                <v-list-tile :to="route.to" v-for="route in routes">
                    <v-list-tile-action>
                        <v-icon>{{ route.icon }}</v-icon>
                    </v-list-tile-action>
                    <v-list-tile-content>
                        <v-list-tile-title>{{ route.name }}</v-list-tile-title>
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
            <v-container fluid class="main-container">
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
            <a href="https://www.github.com/avolpe/cotizacion"
               target="_blank">
                <svg width="80" height="80" viewBox="0 0 250 250"
                     id="github-logo"
                     aria-hidden="true">
                    <path d="M0,0 L115,115 L130,115 L142,142 L250,250 L250,0 Z"></path>
                    <path d="M128.3,109.0 C113.8,99.7 119.0,89.6 119.0,89.6 C122.0,82.7 120.5,78.6 120.5,78.6 C119.2,72.0 123.4,76.3 123.4,76.3 C127.3,80.9 125.5,87.3 125.5,87.3 C122.9,97.6 130.6,101.9 134.4,103.2"
                          fill="currentColor" style="transform-origin: 130px 106px;" class="octo-arm"></path>
                    <path d="M115.0,115.0 C114.9,115.1 118.7,116.5 119.8,115.4 L133.7,101.6 C136.9,99.2 139.9,98.4 142.2,98.6 C133.8,88.0 127.5,74.4 143.8,58.0 C148.5,53.4 154.0,51.2 159.7,51.0 C160.3,49.4 163.2,43.6 171.4,40.1 C171.4,40.1 176.1,42.5 178.8,56.2 C183.1,58.6 187.2,61.8 190.9,65.4 C194.5,69.0 197.7,73.2 200.1,77.6 C213.8,80.2 216.3,84.9 216.3,84.9 C212.7,93.1 206.9,96.0 205.4,96.6 C205.1,102.4 203.0,107.8 198.3,112.5 C181.9,128.9 168.3,122.5 157.7,114.1 C157.9,116.9 156.7,120.9 152.7,124.9 L141.0,136.5 C139.8,137.7 141.6,141.9 141.8,141.8 Z"
                          fill="currentColor" class="octo-body"></path>
                </svg>
            </a>
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
        public routes: Array<{
            name: string,
            icon: string,
            to: string
        }>;

        constructor() {
            super();
            // console.log(this.$router);

            this.routes = [
                {
                    name: 'Inicio',
                    to: '/',
                    icon: 'home'
                }, {
                    name: 'Librerías OpenSource',
                    to: '/licenses',
                    icon: 'gavel'
                }, {
                    name: 'Explorar API',
                    to: '/swagger',
                    icon: 'scanner'
                }
            ]
        }
    }
</script>


<style>

    #github-logo {
        fill: #283593;
        color: #fff;
        position: absolute;
        bottom: 0%;
        border: 0;
        right: 0;
        transform: scale(1, -1);
    }

    #github-logo:hover .octo-arm {
        animation: octocat-wave 560ms ease-in-out;
    }

    @keyframes octocat-wave {
        0%, 100% {
            transform: rotate(0);
        }
        20%, 60% {
            transform: rotate(-25deg);
        }
        40%, 80% {
            transform: rotate(10deg);
        }
    }

    @media (max-width: 500px) {
        #github-logo:hover .octo-arm {
            animation: none
        }

        #github-logo .octo-arm {
            animation: octocat-wave 560ms ease-in-out
        }
    }

    @media (max-width: 500px) {
        .main-container {
            padding: 10px;
        }
    }

</style>
