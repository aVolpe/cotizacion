<template>
  <div>
    <MapOfExchanges />
    <v-dialog v-model="store.exchangeDialog.show" max-width="500px" :fullscreen="isSmall">
      <ExchangeData @ok="store.hideExchangeDialog()" :data="store.exchangeDialog.data" />
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useHead } from '@unhead/vue'
import { useExchangeStore } from '@/stores/exchange'
import MapOfExchanges from '@/components/MapOfExchanges.vue'
import ExchangeData from '@/components/ExchangeData.vue'

const route = useRoute()
const store = useExchangeStore()
const isSmall = ref(window.innerWidth < 600)

useHead({
  title: computed(() => `Mapa de cotizaciones de ${store.current.currency}`)
})

onMounted(() => {
  const str = route.query.moneda
  const currency = (Array.isArray(str) ? str[0] : str) || 'USD'
  store.fetchCurrencyData(currency)
  if (!store.currencies.loaded) store.fetchCurrencies()
})
</script>

