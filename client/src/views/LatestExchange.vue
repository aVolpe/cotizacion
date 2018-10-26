<template>
    <div>
        <ListOfExchanges> </ListOfExchanges>
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
import { Meta, MetaMethod } from "@/decorators";
import { Action, State } from "vuex-class";

@Component({
  components: {
    ExchangeData,
    MapOfExchanges,
    ListOfExchanges
  }
})
export default class LatestExchange extends Vue {
  @Action fetchCurrencies!: (isoCode: string) => void;
  @Action hideExchangeDialog!: () => void;
  @State exchangeDialog!: { loading: boolean; show: boolean };
  @State current!: any;
  tabModel: number = 0;
  isSmall: boolean;

  constructor() {
    super();
    this.isSmall = window.innerWidth < 600;
  }

    @MetaMethod
    meta() {
        return {
            title: `Cotización de ${this.current.currency}`,
            titleTemplate: undefined
        };
    }

  mounted() {
    this.fetchCurrencies(this.$route.query.moneda);
  }

}
</script>
