<template>
<div class="elevation-4">
      <v-card style="margin: 2px">
        <GmapMap
          :center="{lat:-25.2868696, lng:-57.6257072}"
          :zoom="13"
          v-if="branchesWithLocation.loaded"
          style="width: 100%; height: 800px"
        >
        <GmapInfoWindow
            :key="'giw' + qr.id"
            :options="infoWindowOptions"
            v-if="selected === qr.id"
            v-for="qr in branchesWithLocation.data.data"
            :position="{lat: qr.branch.latitude, lng: qr.branch.longitude }"
            >
          <br />
          <b>{{ qr.place.name }}</b>
          <br /><b>{{ qr.branch.name }}</b>

          <br />
          <span>Compra: </span>
          <span><b>{{ qr.purchasePrice | multiply(current.amount) | fn}}</b></span>
          <br />
          <span>Venta: </span>
          <span><b>{{ qr.salePrice | multiply(current.amount) | fn}}</b></span>

          <br />
          <v-btn v-on:click="showExchangeData(qr)" flat>
              Ver más detalles
              <v-icon>
                launch
              </v-icon>
          </v-btn>
        </GmapInfoWindow>
        <GmapMarker
            :key="'gm' + qr.id"
            v-for="qr in branchesWithLocation.data.data"
            v-on:click="selected = qr.id"
            :position="{lat: qr.branch.latitude, lng: qr.branch.longitude }"
            :clickable="true"
            :draggable="false"
          />
        </GmapMap>
      </v-card>


</div>

</template>
<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import { Loaded } from "@/store";
import { ExchangeDataDTO } from "@/api/ExchangeAPI";
import { Getter, State, Action } from "vuex-class";

@Component
export default class MapOfExchanges extends Vue {
  @Getter branchesWithLocation!: Loaded<ExchangeDataDTO[]>;
  @State current!: { currency: string; amount: number };
  @Action showExchangeData!: () => void;

  infoWindowOptions = {
    pixelOffset: {
      width: 0,
      height: -35
    }
  };

  selected: string = "";
}
</script>
