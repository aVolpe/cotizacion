<template>
  <div>
    <v-card>
      <div class="map-toolbar">
        <v-select
          :items="store.currencies.data || [store.current.currency]"
          :model-value="store.current.currency"
          @update:model-value="store.setCurrentCurrency"
          density="compact"
          variant="outlined"
          hide-details
          class="map-currency"
        />
        <v-btn-toggle v-model="priceMode" mandatory density="compact" variant="outlined" divided color="primary">
          <v-btn value="purchase">Compra</v-btn>
          <v-btn value="sale">Venta</v-btn>
        </v-btn-toggle>
        <div class="map-legend" v-if="range" title="En verde, el precio más conveniente">
          <span>{{ formatNumber(multiply(range.min, store.current.amount)) }}</span>
          <span class="map-legend-bar" :style="{ background: legendGradient }"></span>
          <span>{{ formatNumber(multiply(range.max, store.current.amount)) }}</span>
        </div>
      </div>
      <v-progress-linear indeterminate color="primary" v-if="store.branchesWithLocation.loading" />

      <GMapMap
        :center="{ lat: -25.2868696, lng: -57.5757072 }"
        :zoom="10"
        ref="mapRef"
        class="map-container"
        :options="{ gestureHandling: 'greedy' }"
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
          v-for="m in markers"
          :key="'gm' + m.qr.id"
          @click="selected = m.qr.id"
          :position="{ lat: m.qr.branch.latitude, lng: m.qr.branch.longitude }"
          :icon="m.icon"
          :z-index="m.zIndex"
          :clickable="true"
          :draggable="false"
        />

        <GMapMarker
          v-if="userPosition"
          :position="userPosition"
          :icon="userPosition.icon"
          :z-index="2000"
          :clickable="false"
        />

        <template v-slot:visible>
          <div class="map-locate">
            <v-btn
              icon="mdi-crosshairs-gps"
              size="small"
              color="white"
              :loading="locating"
              title="Centrar en mi ubicación"
              @click="searchLocation"
            />
          </div>
        </template>
      </GMapMap>
    </v-card>

    <v-snackbar v-model="showLocationError" :timeout="6000" color="error">
      {{ locationError }}
    </v-snackbar>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useExchangeStore } from '@/stores/exchange'
import type { QueryResponseDetail } from '@/api/ExchangeAPI'

type PriceMode = 'purchase' | 'sale'

const store = useExchangeStore()
const mapRef = ref()
const selected = ref<number | null>(null)
const priceMode = ref<PriceMode>('purchase')

const height = window.innerWidth < 600
  ? window.innerHeight - 46 - 56 - 56 - 20
  : 750

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

const priceOf = (qr: QueryResponseDetail) => priceMode.value === 'purchase' ? qr.purchasePrice : qr.salePrice

const range = computed(() => {
  const prices = branches.value.map(priceOf)
  if (!prices.length) return null
  return { min: Math.min(...prices), max: Math.max(...prices) }
})

// 1 is the best price for the user: the highest when the place buys, the lowest when it sells
const scoreOf = (price: number) => {
  const r = range.value
  if (!r || r.max === r.min) return 1
  const t = (price - r.min) / (r.max - r.min)
  return priceMode.value === 'purchase' ? t : 1 - t
}

// From red (worst) to green (best)
const colorOf = (score: number) => `hsl(${Math.round(score * 120)}, 70%, 42%)`

const pinIcon = (color: string) => 'data:image/svg+xml;charset=UTF-8,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="26" height="38" viewBox="0 0 26 38">' +
  `<path d="M13 1C6.4 1 1 6.3 1 12.9 1 21.8 13 37 13 37s12-15.2 12-24.1C25 6.3 19.6 1 13 1z" fill="${color}" stroke="#fff" stroke-width="2"/>` +
  '<circle cx="13" cy="13" r="4.5" fill="#fff"/></svg>'
)

const markers = computed(() => branches.value.map(qr => {
  const score = scoreOf(priceOf(qr))
  // The best prices are drawn on top when the pins overlap
  return { qr, icon: pinIcon(colorOf(score)), zIndex: Math.round(score * 1000) }
}))

const legendGradient = computed(() => {
  const r = range.value!
  return `linear-gradient(to right, ${colorOf(scoreOf(r.min))}, ${colorOf(0.5)}, ${colorOf(scoreOf(r.max))})`
})

const multiply = (value: number, multiplier: number = 1) => {
  if (!multiplier || multiplier < 1 || isNaN(multiplier)) return value
  return value * multiplier
}

const formatNumber = (value: any) => {
  if (typeof value !== 'number') return value
  return value.toLocaleString('es-PY', { minimumFractionDigits: 0, maximumFractionDigits: 2 })
}

const userPosition = ref<{ lat: number, lng: number, icon: object } | null>(null)
const locating = ref(false)
const locationError = ref('')
const showLocationError = ref(false)

const locationErrors: { [code: number]: string } = {
  1: 'No diste permiso para usar tu ubicación. Podés habilitarlo desde la configuración del navegador.',
  2: 'No se pudo obtener tu ubicación.',
  3: 'Se tardó demasiado en obtener tu ubicación, probá de nuevo.'
}

const failLocation = (message: string) => {
  locating.value = false
  locationError.value = message
  showLocationError.value = true
}

const searchLocation = () => {
  if (!navigator.geolocation) {
    failLocation('Tu navegador no permite obtener la ubicación.')
    return
  }
  locating.value = true
  // The browser asks for the permission the first time
  navigator.geolocation.getCurrentPosition(
    async position => {
      const map = await mapRef.value?.$mapPromise
      const maps = (window as any).google.maps
      const pos = { lat: position.coords.latitude, lng: position.coords.longitude }
      userPosition.value = {
        ...pos,
        icon: {
          url: 'data:image/svg+xml;charset=UTF-8,' + encodeURIComponent(
            '<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22">' +
            '<circle cx="11" cy="11" r="8" fill="#1a73e8" stroke="#fff" stroke-width="3"/></svg>'
          ),
          anchor: new maps.Point(11, 11)
        }
      }
      map?.panTo(pos)
      map?.setZoom(14)
      locating.value = false
    },
    err => failLocation(locationErrors[err.code] || `No se pudo obtener tu ubicación (${err.message}).`),
    { enableHighAccuracy: true, timeout: 15000, maximumAge: 60000 }
  )
}
</script>

<style>
.map-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 8px;
}

.map-currency {
  max-width: 110px;
}

.map-legend {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  margin-left: auto;
}

.map-legend-bar {
  display: inline-block;
  width: 80px;
  height: 10px;
  border-radius: 5px;
}

.map-container {
  position: relative;
}

.map-locate {
  position: absolute;
  top: 60px;
  right: 10px;
  z-index: 100;
}
</style>
