<template>
  <div v-if="store.currencies.loaded">
    <ListOfExchanges />
    <v-dialog v-model="store.exchangeDialog.show" max-width="500px" :fullscreen="isSmall">
      <ExchangeData @ok="store.hideExchangeDialog()" :data="store.exchangeDialog.data" />
    </v-dialog>
  </div>
  <div v-else>
    <img :src="`${baseUrl}imgs/loading.svg`"
         style="margin: auto; width: 200px; height: 200px; max-width: 20vw; max-height: 20vw">
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useHead } from '@unhead/vue'
import { useExchangeStore } from '@/stores/exchange'
import ExchangeData from '@/components/ExchangeData.vue'
import ListOfExchanges from '@/components/ListOfExchanges.vue'

const route = useRoute()
const store = useExchangeStore()
const isSmall = ref(window.innerWidth < 600)
const baseUrl = import.meta.env.BASE_URL

useHead({
  title: computed(() => `Cotización de ${store.current.currency}`)
})

onMounted(() => {
  let finalCurrency: string = 'USD'
  const str = route.query.moneda
  if (Array.isArray(str)) finalCurrency = str[0] || 'USD'
  if (typeof str === 'string') finalCurrency = str
  store.fetchCurrencyData(finalCurrency)
  store.fetchCurrencies()
})
</script>
