<template>
    <div>
        <MapOfExchanges> </MapOfExchanges>
        <v-dialog v-model="exchangeDialog.show" max-width="500px" :fullscreen="isSmall">
            <ExchangeData v-on:ok="hideExchangeDialog" :data="exchangeDialog.data"></ExchangeData>
        </v-dialog>
    </div>


        
        
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import MapOfExchanges from "@/components/MapOfExchanges.vue";
import ExchangeData from "@/components/ExchangeData.vue";
import { Meta } from "@/decorators";
import { Action, State } from "vuex-class";

@Component({
  components: {
    MapOfExchanges,
    ExchangeData
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

