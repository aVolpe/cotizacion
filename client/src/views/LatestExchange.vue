<template>
    <div>
        <v-toolbar color="white" tabs flat>
            <v-tabs v-model="tabModel"
                    color="transparent" slider-color="indigo"
            >
                <v-spacer></v-spacer>
                <v-tab ripple>Mapa</v-tab>
                <v-tab ripple>Lista</v-tab>
                <v-spacer></v-spacer>
            </v-tabs>
        </v-toolbar>

        <v-tabs-items v-model="tabModel">
            <v-tab-item class="text-xs-left">
                <MapOfExchanges> </MapOfExchanges>
            </v-tab-item>
            <v-tab-item class="text-xs-left">
                <ListOfExchanges> </ListOfExchanges>
            </v-tab-item>
        </v-tabs-items>
        <v-dialog v-model="exchangeDialog.show" max-width="500px" :fullscreen="isSmall">
            <ExchangeData v-on:ok="hideExchangeDialog" :data="exchangeDialog.data"></ExchangeData>
        </v-dialog>
    </div>


        
        
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import ExchangeData from "@/components/ExchangeData.vue";
import MapOfExchanges from "@/components/MapOfExchanges.vue";
import ListOfExchanges from "@/components/ListOfExchanges.vue";
import { Meta } from "@/decorators";
import { Action, State } from "vuex-class";

@Component({
  components: {
    ExchangeData,
    MapOfExchanges,
    ListOfExchanges
  }
})
@Meta({
  title: "Cotizaciones de hoy",
  titleTemplate: undefined
})
export default class LatestExchange extends Vue {
  @Action fetchCurrencies!: () => void;
  @Action hideExchangeDialog!: () => void;
  @State exchangeDialog!: { loading: boolean; show: boolean };
  tabModel: number = 0;
  isSmall: boolean;

  constructor() {
    super();
    this.isSmall = window.innerWidth < 600;
  }

  mounted() {
    this.fetchCurrencies();
  }
}
</script>
