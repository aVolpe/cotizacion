<template>
    <div>
        <v-card>
            <v-card-title>
                <v-layout justify-center align-center>
                    <v-flex text-xs-center xs12 md3 mt-1>
                        Explorador de API:
                    </v-flex>
                </v-layout>
            </v-card-title>

            <v-card-text >
                <iframe :src="swaggerUrl" class="swagger-iframe">
                </iframe>

            </v-card-text>
        </v-card>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from 'vue-property-decorator';


    @Component({})
    export default class Swagger extends Vue {

        swaggerUrl: string | undefined;


        constructor() {
            super();
            this.swaggerUrl = `${process.env.VUE_APP_SWAGGER}`;
        }

    }
</script>

<style>
    .swagger-iframe {
        width: 100%;
        height: 90vh;
        
    }
</style>
