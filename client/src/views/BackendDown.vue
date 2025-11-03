<template>
  <div class="no-back">
    <img :src="`${baseUrl}imgs/36.svg`">
    <div>
      <h1>El servidor esta caido</h1>
      <p>Vuelve a intentar en unos momentos!</p>
    </div>
  </div>
</template>

<script setup lang="ts">
const baseUrl = import.meta.env.BASE_URL
</script>

<style scoped>

    .no-back > img {
        width: 50vw;
        max-width: 400px;
    }

</style>
