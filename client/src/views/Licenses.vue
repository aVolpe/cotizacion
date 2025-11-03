<template>
  <div class="no-back">
    <v-card class="elevation-4">
      <v-toolbar color="indigo" dark>
        <v-spacer></v-spacer>
        <v-toolbar-title>Licencias utilizadas</v-toolbar-title>
        <v-spacer></v-spacer>
      </v-toolbar>

      <v-tabs v-model="tabModel" color="indigo" slider-color="white" centered>
        <v-tab>Back-end</v-tab>
        <v-tab>Front-end</v-tab>
        <v-tab>Otras</v-tab>
      </v-tabs>

      <v-window v-model="tabModel">
        <v-window-item>
          <v-card flat>
            <v-container fluid>
              <v-row>
                <v-col
                  v-for="item in backend"
                  :key="item.name"
                  cols="12"
                  sm="6"
                  md="5"
                  lg="4"
                >
                  <v-card>
                    <v-card-title>
                      <h4>{{ item.name }}</h4>
                      <v-spacer></v-spacer>
                      <a :href="item.url" target="_blank">
                        <v-icon>mdi-launch</v-icon>
                      </a>
                    </v-card-title>
                    <v-divider></v-divider>
                    <v-list density="compact">
                      <v-list-item>
                        <v-list-item-title>Licencia:</v-list-item-title>
                        <template v-slot:append>
                          <div class="text-right">{{ item.license }}</div>
                        </template>
                      </v-list-item>
                      <v-list-item>
                        <v-list-item-title>Group:</v-list-item-title>
                        <template v-slot:append>
                          <div class="text-right">{{ item.groupId }}</div>
                        </template>
                      </v-list-item>
                      <v-list-item>
                        <v-list-item-title>Id:</v-list-item-title>
                        <template v-slot:append>
                          <div class="text-right">{{ item.artifactId }}</div>
                        </template>
                      </v-list-item>
                      <v-list-item>
                        <v-list-item-title>Version:</v-list-item-title>
                        <template v-slot:append>
                          <div class="text-right">{{ item.version }}</div>
                        </template>
                      </v-list-item>
                    </v-list>
                  </v-card>
                </v-col>
              </v-row>
            </v-container>

            <h5 class="text-center pa-4">
              Estas dependencias se encuentran en el
              <a href="https://github.com/aVolpe/cotizacion/blob/master/pom.xml" target="_blank">
                pom.xml
                <v-icon size="small">mdi-launch</v-icon>
              </a>
            </h5>
          </v-card>
        </v-window-item>

        <v-window-item>
          <v-card flat>
            <v-container fluid>
              <v-row>
                <v-col
                  v-for="item in frontend"
                  :key="item.name"
                  cols="12"
                  sm="6"
                  md="5"
                  lg="4"
                >
                  <v-card>
                    <v-card-title>
                      <h4>{{ item.name }}</h4>
                      <v-spacer></v-spacer>
                      <a :href="item.link" target="_blank">
                        <v-icon>mdi-launch</v-icon>
                      </a>
                    </v-card-title>
                    <v-divider></v-divider>
                    <v-list density="compact">
                      <v-list-item>
                        <v-list-item-title>Licencia:</v-list-item-title>
                        <template v-slot:append>
                          <div class="text-right">{{ item.licenseType }}</div>
                        </template>
                      </v-list-item>
                      <v-list-item>
                        <v-list-item-title>Version:</v-list-item-title>
                        <template v-slot:append>
                          <div class="text-right">{{ item.comment }}</div>
                        </template>
                      </v-list-item>
                    </v-list>
                  </v-card>
                </v-col>
              </v-row>
            </v-container>
            <h5 class="text-center pa-4">
              Estas dependencias se encuentran en el
              <a href="https://github.com/aVolpe/cotizacion/blob/master/client/package.json" target="_blank">
                package.json
                <v-icon size="small">mdi-launch</v-icon>
              </a>
            </h5>
          </v-card>
        </v-window-item>

        <v-window-item>
          <v-card flat>
            <v-container fluid>
              <v-row>
                <v-col
                  v-for="item in other"
                  :key="item.name"
                  cols="12"
                  sm="6"
                  md="5"
                  lg="4"
                >
                  <v-card>
                    <v-card-title>
                      <h4>{{ item.name }}</h4>
                      <v-spacer></v-spacer>
                      <a :href="item.url" target="_blank">
                        <v-icon>mdi-launch</v-icon>
                      </a>
                    </v-card-title>
                    <v-divider></v-divider>
                    <v-list density="compact">
                      <v-list-item>
                        <v-list-item-title>Autor:</v-list-item-title>
                        <template v-slot:append>
                          <div class="text-right">{{ item.author }}</div>
                        </template>
                      </v-list-item>
                      <v-list-item>
                        <v-list-item-title>Licencia:</v-list-item-title>
                        <template v-slot:append>
                          <div class="text-right">{{ item.license }}</div>
                        </template>
                      </v-list-item>
                    </v-list>
                  </v-card>
                </v-col>
              </v-row>
            </v-container>
          </v-card>
        </v-window-item>
      </v-window>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useHead } from '@unhead/vue'
import { LicenseAPI } from '@/api/LicenseAPI'
import licensesData from '@/licenses.json'

useHead({
  title: 'Licencias'
})

const tabModel = ref(0)
const baseUrl = import.meta.env.BASE_URL
const backend = ref<any[]>([])
const frontend = ref<any[]>(licensesData as any)
const other = ref([
  {
    name: 'lukaszadam Free Illustrations',
    author: 'Lukasz Dzikowski',
    url: 'https://lukaszadam.com/illustrations',
    license: 'MIT License'
  },
  {
    name: 'github-corners',
    author: 'Tim Holman',
    url: 'https://github.com/tholman/github-corners/',
    license: 'MIT License'
  }
])

onMounted(async () => {
  const response = await LicenseAPI.get()
  backend.value = response.dependencies
})
</script>

<style scoped>

    .no-back > img {
        width: 50vw;
        max-width: 400px;
    }

</style>
