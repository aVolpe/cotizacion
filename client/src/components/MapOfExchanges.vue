<template>
<div>
<GmapMap
  :center="{lat:-25.2868696, lng:-57.6257072}"
  :zoom="13"
  v-if="branchesWithLocation.loaded"
  style="width: 500px; height: 300px"
>
 <GmapInfoWindow
    :key="'giw' + qr.id"
    v-for="qr in branchesWithLocation.data.data"
    :position="{lat: qr.branch.latitude, lng: qr.branch.longitude }"
    >
  <b>{{ qr.place.name }}</b>
  <br /><b>{{ qr.branch.name }}</b>
  <br />{{ qr}}
 </GmapInfoWindow>
 <GmapMarker
    :key="'gm' + qr.id"
    v-for="qr in branchesWithLocation.data.data"
    :position="{lat: qr.branch.latitude, lng: qr.branch.longitude }"
    :clickable="true"
    :draggable="true"
  />
</GmapMap>
</div>

</template>
<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import { Loaded } from "@/store";
import { ExchangeDataDTO } from "@/api/ExchangeAPI";
import { Getter } from "vuex-class";

@Component
export default class MapOfExchanges extends Vue {
  @Getter branchesWithLocation!: Loaded<ExchangeDataDTO[]>;
}
</script>
