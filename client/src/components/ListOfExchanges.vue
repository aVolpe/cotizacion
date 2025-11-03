<template>
  <v-card>
    <v-card-title class="title text-center">
      <v-row justify="center" align="center" no-gutters>
        <v-col cols="auto" class="text-center d-none d-md-flex align-center mr-3">
          Cambio de
        </v-col>
        <v-col cols="12" md="9" class="text-center" v-if="store.currencies.loaded">
          <div class="combined-input">
            <v-text-field 
              v-model="store.current.amount"
              class="amount-field"
              :prefix="isMD ? 'Cambio de' : ''"
              density="compact"
              bg-color="white"
              :variant="isMD ? 'filled' : 'underlined'"
            />
            <v-select 
              :items="store.currencies.data"
              v-model="store.current.currency"
              @update:model-value="store.setCurrentCurrency"
              density="compact"
              bg-color="white"
              :variant="isMD ? 'filled' : 'underlined'"
              class="currency-field"
            />
          </div>
        </v-col>
      </v-row>
    </v-card-title>

    <v-card-text class="main-table-wrapper pt-6">
      <v-data-table
        :headers="headers"
        :loading="store.filteredCurrencies.loading"
        :items="store.filteredCurrencies.data?.data || []"
        :sort-by="[{ key: 'purchasePrice', order: 'desc' }]"
        item-value="id"
        density="default"
        :items-per-page="-1"
        hide-default-footer
      >
        <template v-slot:loading>
          <img :src="`${baseUrl}imgs/loading.svg`"
               style="margin: auto; width: 200px; height: 200px; max-width: 20vw; max-height: 20vw">
        </template>
        
        <template v-slot:no-data>
          <v-alert v-if="store.filteredCurrencies.loaded" color="warning" icon="mdi-alert">
            No hay datos de hoy
          </v-alert>
        </template>

        <template v-slot:[`header.placeName`]>
          <v-select 
            :items="placesWithEmpty"
            v-model="store.current.place"
            @update:model-value="store.setCurrentPlace"
            label="Casa"
            item-title="name"
            item-value="id"
            density="compact"
            hide-details
            bg-color="white"
            variant="filled"
          />
        </template>

        <template v-slot:item="{ item }">
          <tr>
            <td class="text-left name-column">
              <b>{{ item.place.name }}</b>
              <br/>
              <span v-if="item.place.type === 'BANK'">Ver sucursales</span>
              <span v-else>{{ item.branch.name }}</span>
            </td>
            <td class="text-right">{{ formatNumber(multiply(item.purchasePrice, store.current.amount)) }}</td>
            <td class="text-right">{{ formatNumber(multiply(item.salePrice, store.current.amount)) }}</td>
            <td class="text-right" v-if="!isSmall">
              <a @click="store.showExchangeData(item)">
                <v-icon color="primary">mdi-information</v-icon>
              </a>
              <a :href="item.branch.gmaps"
                 v-if="item.place.type === 'BUREAU' && item.branch.gmaps" target="_blank">
                <v-icon>mdi-map</v-icon>
              </a>
            </td>
          </tr>
        </template>
      </v-data-table>
      
      <small v-if="store.filteredCurrencies.loaded && store.filteredCurrencies.data">
        <b>{{ store.filteredCurrencies.data.count }}</b> cotizaciones consultadas hace
        <b>{{ formatDistance(store.filteredCurrencies.data.firstQueryResult) }}</b>.
      </small>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { ref, computed, watch, getCurrentInstance } from 'vue'
import { useExchangeStore } from '@/stores/exchange'
import type { Place } from '@/api/ExchangeAPI'
import { formatDistance as formatDistanceFn } from 'date-fns'
import es from 'date-fns/locale/es'

const store = useExchangeStore()
const instance = getCurrentInstance()
const isSmall = ref(window.innerWidth < 600)
const isMD = ref(window.innerWidth < 960)
const baseUrl = import.meta.env.BASE_URL

const placesWithEmpty = ref<Place[]>([])

const headers = computed(() => {
  const baseHeaders = [
    {
      title: '',
      key: 'placeName',
      sortable: false,
      width: '35%',
      class: 'place'
    },
    {
      title: 'Compra',
      key: 'purchasePrice',
      sortable: true,
      align: 'end' as const
    },
    {
      title: 'Venta',
      key: 'salePrice',
      sortable: true,
      align: 'end' as const
    }
  ]
  
  if (!isSmall.value) {
    baseHeaders.push({
      title: ' ',
      key: 'actions',
      sortable: false,
      align: 'end' as const
    } as any)
  }
  
  return baseHeaders
})

// Helper functions to replace filters
const multiply = (value: number, multiplier: number = 1) => {
  if (!multiplier || multiplier < 1 || isNaN(multiplier)) return value
  return value * multiplier
}

const formatNumber = (value: any) => {
  if (typeof value !== 'number') return value
  return value.toLocaleString('es-PY')
}

const formatDistance = (value: string) => {
  if (value) return formatDistanceFn(new Date(value), new Date(), { locale: es })
  return ''
}

// Watch currentPlaces to update placesWithEmpty
watch(() => store.currentPlaces, (places) => {
  placesWithEmpty.value = [{
    name: 'Todos',
    id: -1
  } as Place].concat(places)
}, { immediate: true })
</script>

<style>
    @media (max-width: 599px) {
        .main-table-wrapper {
            padding: 10px !important;
        }

        .name-column {
            padding: 3px 10px !important;
        }

        .title {
            padding-top: 1px !important;
            padding-bottom: 1px !important;
        }
    }

    .input-group__input > input {
        text-align: right !important;
    }

    .input-group__selections__comma {
        margin-left: auto;
        margin-right: auto;
    }

    a,
    u {
        text-decoration: none;
    }

    th.place {
        padding: 0 0 0 10px !important;
    }

    .v-data-table thead tr {
        height: 56px;
    }

    .v-data-table tbody tr {
        border-top: 8px solid transparent;
    }

    .combined-input {
        display: flex;
        gap: 0;
        border-bottom: 1px solid rgba(0, 0, 0, 0.42);
        background-color: white;
        padding: 4px 0;
    }

    .combined-input:hover {
        border-bottom-color: rgba(0, 0, 0, 0.87);
    }

    .combined-input:focus-within {
        border-bottom: 2px solid #1976d2;
    }

    .amount-field {
        flex: 1;
    }

    .amount-field input {
        text-align: right;
    }

    .currency-field {
        max-width: 100px;
        flex-shrink: 0;
        margin-left: 8px;
    }

    .combined-input .v-field__underlay,
    .combined-input .v-field__outline {
        display: none;
    }

    .combined-input .v-input__details {
        display: none;
    }
</style>

