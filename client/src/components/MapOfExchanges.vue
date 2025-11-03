<template>
  <div>
    <v-card>
      <GMapMap
        :center="{ lat: -25.2868696, lng: -57.5757072 }"
        :zoom="10"
        ref="mapRef"
        :options="{ gestureHandling: 'greedy' }"
        v-if="store.branchesWithLocation.loaded"
        :style="'width: 100%; height: ' + height + 'px'"
      >
        <template v-for="qr in branches" :key="'giw' + qr.id">
          <GMapInfoWindow
            v-if="selected === qr.id"
            :options="infoWindowOptions"
            :position="{ lat: qr.branch.latitude, lng: qr.branch.longitude }"
          >
            <br />
            <b>{{ qr.place.name }}</b>
            <br /><b>{{ qr.branch.name }}</b>

            <br />
            <span>Compra: </span>
            <span><b>{{ formatNumber(multiply(qr.purchasePrice, store.current.amount)) }}</b></span>
            <br />
            <span>Venta: </span>
            <span><b>{{ formatNumber(multiply(qr.salePrice, store.current.amount)) }}</b></span>

            <br />
            <v-btn @click="store.showExchangeData(qr)" variant="text">
              Ver más detalles
              <v-icon>mdi-launch</v-icon>
            </v-btn>
          </GMapInfoWindow>
        </template>
        
        <GMapMarker
          v-for="qr in branches"
          :key="'gm' + qr.id"
          @click="selected = qr.id"
          :position="{ lat: qr.branch.latitude, lng: qr.branch.longitude }"
          :clickable="true"
          :draggable="false"
        />
        
        <template v-slot:visible>
          <div style="top: 0px; right: 0px; position: absolute; z-index: 100">
            <v-icon 
              style="padding: 7px; margin: 10px; margin-top: 56px; background-color: white;" 
              @click="searchLocation"
            >
              mdi-crosshairs-gps
            </v-icon>
          </div>
        </template>
      </GMapMap>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useExchangeStore } from '@/stores/exchange'
import type { QueryResponseDetail } from '@/api/ExchangeAPI'

const store = useExchangeStore()
const mapRef = ref()
const selected = ref('')

const height = window.innerWidth < 600
  ? window.innerHeight - 46 - 56 - 20
  : 800

const infoWindowOptions = {
  pixelOffset: {
    width: 0,
    height: -35
  }
}

const branches = computed((): QueryResponseDetail[] => {
  const data = store.branchesWithLocation.data?.data || []
  return data.filter(item => item && item.branch && item.branch.latitude && item.branch.longitude)
})

const multiply = (value: number, multiplier: number = 1) => {
  if (!multiplier || multiplier < 1 || isNaN(multiplier)) return value
  return value * multiplier
}

const formatNumber = (value: any) => {
  if (typeof value !== 'number') return value
  return value.toLocaleString('es-PY')
}

const searchLocation = () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      position => {
        const pos = {
          lat: position.coords.latitude,
          lng: position.coords.longitude
        }

        const map = mapRef.value?.$mapObject
        if (map) {
          map.panTo(pos)
          map.setZoom(14)
        }
      },
      (err) => {
        alert(`Error (${err.code}): ${err.message}`)
      }
    )
  } else {
    alert('Imposible usar ubicación')
  }
}
</script>
