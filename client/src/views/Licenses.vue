<template>
    <div class="no-back">
        <div>
            <div class="elevation-4">
                <v-toolbar dark color="indigo" tabs>
                    <v-spacer></v-spacer>
                    <v-toolbar-title>Licencias utilizadas</v-toolbar-title>
                    <v-spacer></v-spacer>

                    <v-tabs
                            slot="extension"
                            dark color="indigo" slider-color="white" v-model="tabModel">
                        <v-spacer></v-spacer>
                        <v-tab ripple> Back-end</v-tab>
                        <v-tab ripple>Front-end</v-tab>
                        <v-tab ripple>Otras</v-tab>
                        <v-spacer></v-spacer>
                    </v-tabs>
                </v-toolbar>

                <v-tabs-items v-model="tabModel">
                    <v-tab-item class="text-xs-left">
                        <v-card flat>
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
                        </v-card>
                    </v-tab-item>
                    <v-tab-item class="text-xs-left">

                        <v-card flat>
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
                                <a href="https://github.com/aVolpe/cotizacion/blob/master/client/package.json"
                                   target="_blank">
                                    package.json
                                    <v-icon>launch</v-icon>
                                </a>
                            </h5>
                        </v-card>
                    </v-tab-item>
                    <v-tab-item class="text-xs-left">
                        <v-card flat>

                            <v-container fluid grid-list-md>
                                <v-data-iterator content-tag="v-layout"
                                                 row
                                                 wrap
                                                 hide-actions
                                                 :items="other">
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
                                                    <v-list-tile-content>Autor:</v-list-tile-content>
                                                    <v-list-tile-content class="align-end">{{ props.item.author }}
                                                    </v-list-tile-content>
                                                </v-list-tile>

                                                <v-list-tile>
                                                    <v-list-tile-content>Licencia:</v-list-tile-content>
                                                    <v-list-tile-content class="align-end">{{ props.item.license}}
                                                    </v-list-tile-content>
                                                </v-list-tile>


                                            </v-list>
                                        </v-card>
                                    </v-flex>
                                </v-data-iterator>

                            </v-container>
                        </v-card>
                    </v-tab-item>
                </v-tabs-items>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {LicenseAPI} from "../api/LicenseAPI";
    import {Meta} from "../decorators";

    // tslint:disable-next-line
    const data = require("../licenses.json");


    @Component
    @Meta({
        title: "Licencias"
    })
    export default class Licenses extends Vue {
        baseUrl: string | undefined;

        backend: any[];
        frontend: any[];
        other: any[];
        tabModel: number;


        constructor() {
            super();
            this.tabModel = 0;
            this.baseUrl = process.env.BASE_URL;
            this.backend = [];
            this.frontend = data as any;
            this.other = [{
                name: "lukaszadam Free Illustrations",
                author: "Lukasz Dzikowski",
                url: "https://lukaszadam.com/illustrations",
                license: "MIT License"
            }, {
                name: "github-corners",
                author: "Tim Holman",
                url: "https://github.com/tholman/github-corners/",
                license: "MIT License"
            }];
        }

        mounted() {
            LicenseAPI.get().then(response => {
                this.backend = response.dependencies;
            });
        }

    }
</script>

<style scoped>

    .no-back > img {
        width: 50vw;
        max-width: 400px;
    }

</style>
