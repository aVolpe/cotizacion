<template>
<div>
      <v-card>
        <GmapMap
          :center="{lat:-25.2868696, lng:-57.5757072}"
          :zoom="10"
          ref="mapRef"
          :options="{gestureHandling: 'greedy'}"
          v-if="branchesWithLocation.loaded"
          :style="'width: 100%; height: ' + height + 'px'"
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
        <div slot="visible">
          <div style="top: 0px; right: 0px;  position: absolute; z-index: 100">
              <v-icon style="padding: 7px; margin: 10px; margin-top: 56px; background-color: white;" v-on:click="searchLocation">
                location_searching
              </v-icon>
          </div>
        </div>
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
  height: number = window.innerWidth < 600
    ? window.innerHeight - 46 - 56 - 20
    : 800;

  infoWindowOptions = {
    pixelOffset: {
      width: 0,
      height: -35
    }
  };

  selected: string = "";

  searchLocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        position => {
          const pos = {
            lat: position.coords.latitude,
            lng: position.coords.longitude
          };

          const map = (this.$refs.mapRef as any).$mapObject;
          map.panTo(pos);
          map.setZoom(14);
        },
        (err) => {
          alert(`Error (${err.code}): ${err.message}`);
        }
      );
    } else {
      // Browser doesn't support Geolocation
      alert("Imposible usar ubicación");
    }
  }
}
</script>
