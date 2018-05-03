<template>
    <div class="no-back">
        <div>
            <h1>Licencias utilizadas</h1>
            <div>
                <v-tabs color="indigo" dark slider-color="white">
                    <v-spacer/>
                    <v-tab ripple>
                        Back-end
                    </v-tab>
                    <v-tab ripple>
                        Front-end
                    </v-tab>
                    <v-spacer/>
                    <v-tab-item class="text-xs-left">

                        <v-container fluid grid-list-md>
                            <v-data-iterator content-tag="v-layout"
                                             row
                                             wrap
                                             hide-actions
                                             :items="backend">
                                <v-flex slot="item" slot-scope="props"
                                        xs12 sm6 md5 lg4>
                                    <v-card>
                                        <v-card-title>
                                            <h4>{{ props.item.name }}</h4>
                                            <v-spacer></v-spacer>
                                            <a :href="props.item.url " target="_blank">
                                                <v-icon>launch</v-icon>
                                            </a>
                                        </v-card-title>
                                        <v-divider></v-divider>
                                        <v-list dense>
                                            <v-list-tile>
                                                <v-list-tile-content>Licencia:</v-list-tile-content>
                                                <v-list-tile-content class="align-end">{{ props.item.license }}
                                                </v-list-tile-content>
                                            </v-list-tile>
                                            <v-list-tile>
                                                <v-list-tile-content>Group:</v-list-tile-content>
                                                <v-list-tile-content class="align-end">{{ props.item.groupId }}
                                                </v-list-tile-content>
                                            </v-list-tile>
                                            <v-list-tile>
                                                <v-list-tile-content>Id:</v-list-tile-content>
                                                <v-list-tile-content class="align-end">{{ props.item.artifactId }}
                                                </v-list-tile-content>
                                            </v-list-tile>
                                            <v-list-tile>
                                                <v-list-tile-content>Version:</v-list-tile-content>
                                                <v-list-tile-content class="align-end">{{ props.item.version }}
                                                </v-list-tile-content>
                                            </v-list-tile>
                                        </v-list>
                                    </v-card>
                                </v-flex>
                            </v-data-iterator>
                        </v-container>
                        <h5 class="text-xs-center">
                            Estas dependencias se encuentran en el
                            <a href="https://github.com/aVolpe/cotizacion/blob/master/pom.xml" target="_blank">
                                pom.xml
                                <v-icon>launch</v-icon>
                            </a>
                        </h5>
                    </v-tab-item>
                    <v-tab-item class="text-xs-left">

                        <v-container fluid grid-list-md>
                            <v-data-iterator content-tag="v-layout"
                                             row
                                             wrap
                                             hide-actions
                                             :items="frontend">
                                <v-flex slot="item" slot-scope="props"
                                        xs12 sm6 md5 lg4>
                                    <v-card>
                                        <v-card-title>
                                            <h4>{{ props.item.name }}</h4>
                                            <v-spacer></v-spacer>
                                            <a :href="props.item.link " target="_blank">
                                                <v-icon>launch</v-icon>
                                            </a>
                                        </v-card-title>
                                        <v-divider></v-divider>
                                        <v-list dense>
                                            <v-list-tile>
                                                <v-list-tile-content>Licencia:</v-list-tile-content>
                                                <v-list-tile-content class="align-end">{{ props.item.licenseType }}
                                                </v-list-tile-content>
                                            </v-list-tile>


                                            <v-list-tile>
                                                <v-list-tile-content>Version:</v-list-tile-content>
                                                <v-list-tile-content class="align-end">{{ props.item.comment }}
                                                </v-list-tile-content>
                                            </v-list-tile>
                                        </v-list>
                                    </v-card>
                                </v-flex>
                            </v-data-iterator>
                        </v-container>
                        <h5 class="text-xs-center">
                            Estas dependencias se encuentran en el
                            <a href="https://github.com/aVolpe/cotizacion/blob/master/client/package.json" target="_blank">
                                package.json
                                <v-icon>launch</v-icon>
                            </a>
                        </h5>
                    </v-tab-item>
                </v-tabs>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from 'vue-property-decorator';
    import {LicenseAPI} from '../api/LicenseAPI';
    import * as data from '../licenses.json';


    @Component({})
    export default class Licenses extends Vue {
        baseUrl: string | undefined;

        backend: any[];
        frontend: any[];


        constructor() {
            super();
            this.baseUrl = process.env.BASE_URL;
            this.backend = [];
            this.frontend = <any> data;
        }

        mounted() {
            console.log(data);
            LicenseAPI.get().then(data => {
                this.backend = data.dependencies;
            })
        }

    }
</script>

<style scoped>

    .no-back > img {
        width: 50vw;
        max-width: 400px;
    }

</style>
