<template>
  <v-card v-if="props.data">
    <span v-if="props.data.place.type !== 'BANK'">
      <v-img v-if="props.data.branch.image" :src="props.data.branch.image" height="200"></v-img>
      <v-img v-else
        :src="'https://dummyimage.com/700x400/3F51B5/fff.png?text=' + props.data.place.name"
        height="200"
      />
    </span>
    <span v-else>
      <v-img 
        :src="'https://dummyimage.com/700x400/3F51B5/fff.png?text=' + props.data.place.name"
        height="200"
      />
    </span>
    
    <v-card-title v-if="props.data.place.type !== 'BANK'">
      <h3 class="text-h5 text-center w-100">
        {{ props.data.branch.name }} - {{ props.data.place.name }}
      </h3>
    </v-card-title>

    <v-card-text>
      <v-container fluid>
        <v-row>
          <v-col cols="4">
            <div class="text-subtitle-2">Compra de {{ props.data.exchange.currency }}</div>
          </v-col>
          <v-col cols="6">
            <div class="text-subtitle-2 text-right"><b>{{ formatNumber(props.data.exchange.purchasePrice) }}</b></div>
          </v-col>
        </v-row>
        
        <v-row>
          <v-col cols="4">
            <div class="text-subtitle-2">Venta de {{ props.data.exchange.currency }}</div>
          </v-col>
          <v-col cols="6" class="text-right">
            <div class="text-subtitle-2 text-right"><b>{{ formatNumber(props.data.exchange.salePrice) }}</b></div>
          </v-col>
        </v-row>
        
        <v-row v-if="props.data.branch && props.data.branch.email">
          <v-col cols="4">
            <div class="text-subtitle-2">Email</div>
          </v-col>
          <v-col cols="8">
            <div class="text-subtitle-2"><a :href="'mailto:' + props.data.branch.email">{{ props.data.branch.email }}</a></div>
          </v-col>
        </v-row>
        
        <v-row v-if="props.data.branch && props.data.branch.phoneNumber">
          <v-col cols="4">
            <div class="text-subtitle-2">Telefono</div>
          </v-col>
          <v-col cols="8">
            <div class="text-subtitle-2">{{ props.data.branch.phoneNumber }}</div>
          </v-col>
        </v-row>
        
        <v-row v-if="props.data.branch && props.data.branch.schedule">
          <v-col cols="4">
            <div class="text-subtitle-2">Horario de atención</div>
          </v-col>
          <v-col cols="8">
            <div class="text-subtitle-2">{{ props.data.branch.schedule }}</div>
          </v-col>
        </v-row>
        
        <v-row v-if="props.data.place.branches && props.data.place.branches.length">
          <v-data-table
            :headers="headers"
            :items="props.data.place.branches"
            :items-per-page="itemsPerPage"
            v-model:page="page"
            :sort-by="[{ key: 'name', order: 'asc' }]"
            item-value="id"
            class="branches"
            density="compact"
          >
            <template v-slot:item="{ item }">
              <tr>
                <td class="text-left name-column">
                  <a :href="item.gmaps" v-if="item.gmaps" target="_blank">
                    <v-icon>mdi-map</v-icon>
                    <b>{{ item.name }}</b>
                  </a>
                  <span v-else>
                    <b>{{ item.name }}</b>
                  </span>
                </td>
                <td class="text-right" v-html="getSchedule(item)"></td>
                <td class="text-right" v-html="getPhone(item)"></td>
              </tr>
            </template>
          </v-data-table>
        </v-row>
      </v-container>
    </v-card-text>
    
    <v-card-actions>
      <v-spacer />
      <v-btn 
        :href="props.data.branch.gmaps" 
        v-if="props.data.place.type === 'BUREAU' && props.data.branch.gmaps" 
        target="_blank"
        variant="text"
        color="orange"
      >
        Ir al mapa
      </v-btn>
      <v-btn variant="text" color="blue" @click="emit('ok')">Aceptar</v-btn>
    </v-card-actions>
    
    <v-card-actions class="footpart">
      <v-container fluid>
        <v-row>
          <small class="footnote">
            Estos datos fueron obtenidos de la página web de cada casa de cambios.
            Si tienes mas datos de esta casa de cambios, <a href="mailto:arturovolpe@gmail.com"> envianos un mail</a>.
          </small>
        </v-row>
        <br/>
        <v-row>
          <v-spacer />
          <small class="footnote">
            Consultado el {{ formatDate(props.data.exchange.date, "dd/MM/yyyy 'a las' HH:mm") }}
          </small>
          <v-spacer />
        </v-row>
      </v-container>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { format } from 'date-fns'
import type { Branch } from '@/api/ExchangeAPI'

interface Props {
  data?: any
}

const props = defineProps<Props>()
const emit = defineEmits<{
  ok: []
}>()

const isSmall = ref(window.innerWidth < 600)
const page = ref(1)
const itemsPerPage = 3

const headers = [
  { title: 'Nombre', key: 'name', align: 'start' as const, sortable: true },
  { title: 'Horario', key: 'schedule', sortable: false, align: 'end' as const },
  { title: 'Teléfono', key: 'phone', sortable: false, align: 'end' as const }
]

const formatNumber = (value: any) => {
  if (typeof value !== 'number') return value
  return value.toLocaleString('es-PY')
}

const formatDate = (value: any, desiredFormat: string) => {
  if (value) return format(new Date(value), desiredFormat)
  return ''
}

const getSchedule = (branch: Branch) => {
  if (!branch || !branch.schedule) return ''

  return branch.schedule
    .toLowerCase()
    .replace(/\n/g, ' ')
    .replace(/lunes/, 'L')
    .replace(/martes/, 'Ma')
    .replace(/miercoles/, 'Mi')
    .replace(/jueves/, 'J')
    .replace(/viernes/, 'V')
    .replace(/sabado/, 'S')
    .replace(/sábado/, 'S')
    .replace(/domingo/, 'D')
    .replace(/lunes_viernes/, 'L a V')
    .replace(/sbado/, 'S')
    .replace(/domingo/, 'D')
    .replace(/hs /, '')
    .replace(/0 a /, '0-')
    .replace(/ - /g, '-')
    .replace(/\./, '<br />')
    .replace(/(\d) (S|D)/g, '$1<br />$2')
    .trim()
}

const getPhone = (branch: Branch) => {
  if (!branch || !branch.phoneNumber) return ''

  return branch.phoneNumber
    .replace(/.*Cel.:/, '')
    .replace(/\(RA\)/, '')
    .replace(/\/.*/, '')
    .replace(/y .*/, '')
    .trim()
}
</script>

<style scoped>
    @media (max-width: 599px) {
        .container.fluid {
            padding: 0 !important;
        }
    }

    .footnote {
        text-align: center
    }

    .footpart {
        background-color: lightgray;
    }

</style>

<style>
    div.branches {
        width: 100%;
    }

    div.branches tbody td {
        padding: 0 4px !important;
    }
</style>
