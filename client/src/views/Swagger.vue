<template>
  <div>
    <v-card>
      <v-card-title>
        <v-row justify="center" align="center">
          <v-col cols="12" md="3" class="text-center mt-1">
            Explorador de API:
          </v-col>
        </v-row>
      </v-card-title>

      <v-card-text>
        <iframe :src="swaggerUrl" class="swagger-iframe">
        </iframe>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { useHead } from '@unhead/vue'

const swaggerUrl = import.meta.env.VITE_SWAGGER

useHead({
  title: 'API'
})
</script>

<style>
    .swagger-iframe {
        width: 100%;
        height: 90vh;

    }
</style>
