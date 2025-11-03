import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createHead } from '@unhead/vue'
import VueGoogleMaps from '@fawmi/vue-google-maps'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
import './registerServiceWorker'
import { formatDistance, format } from 'date-fns'
import es from 'date-fns/locale/es'

const app = createApp(App)
const pinia = createPinia()
const head = createHead()

app.use(pinia)
app.use(router)
app.use(vuetify)
app.use(head)
app.use(VueGoogleMaps, {
  load: {
    key: 'AIzaSyAcZVbIDdV7dM8nAiIPsCXnjUY51gXZPJI'
  }
})

// Global properties (replacing filters)
app.config.globalProperties.$filters = {
  multiply: (value: number, multiplier: number = 1) => {
    if (!multiplier || multiplier < 1 || isNaN(multiplier)) return value
    return value * multiplier
  },
  fn: (value: any) => {
    if (typeof value !== 'number') return value
    return value.toLocaleString('es-PY')
  },
  fd: (value: any, desiredFormat: string | null = null) => {
    const finalFormat = desiredFormat ? desiredFormat : 'dd/MM/yyyy hh:mm'
    if (value) return format(new Date(value), finalFormat)
    return ''
  },
  mfn: (value: string) => {
    if (value) return formatDistance(new Date(value), new Date(), { locale: es })
    return ''
  }
}

app.mount('#app')

