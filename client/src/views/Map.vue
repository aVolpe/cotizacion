<template>
  <div>
    <MapOfExchanges />
    <v-dialog v-model="store.exchangeDialog.show" max-width="500px" :fullscreen="isSmall">
      <ExchangeData @ok="store.hideExchangeDialog()" :data="store.exchangeDialog.data" />
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useHead } from '@unhead/vue'
import { useExchangeStore } from '@/stores/exchange'
import MapOfExchanges from '@/components/MapOfExchanges.vue'
import ExchangeData from '@/components/ExchangeData.vue'

const store = useExchangeStore()
const isSmall = ref(window.innerWidth < 600)

useHead({
  title: 'Cotizaciones de hoy'
})

onMounted(() => {
  store.init()
})
</script>

