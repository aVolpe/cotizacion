import { createRouter, createWebHistory } from 'vue-router'
import About from './views/About.vue'
import LatestExchange from './views/LatestExchange.vue'
import BackendDown from './views/BackendDown.vue'
import Licenses from './views/Licenses.vue'
import Swagger from './views/Swagger.vue'
import Map from './views/Map.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'latest',
      component: LatestExchange
    },
    {
      path: '/about',
      name: 'about',
      component: About
    },
    {
      path: '/map',
      name: 'map',
      component: Map
    },
    {
      path: '/licenses',
      name: 'licenses',
      component: Licenses
    },
    {
      path: '/swagger',
      name: 'swagger',
      component: Swagger
    },
    {
      path: '/no-back',
      name: 'no-back',
      component: BackendDown
    }
  ]
})

export default router
