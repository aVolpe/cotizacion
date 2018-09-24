<template>
    <div class="no-back">
        <img :src="`${baseUrl}imgs/36.svg`">
        <div>
            <h1>El servidor esta caido</h1>
            <p>Vuelve a intentar en unos momentos!</p>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component({})
    export default class BackendDown extends Vue {
        baseUrl: string | undefined;


        constructor() {
            super();
            this.baseUrl = process.env.BASE_URL;
        }

    }
</script>

<style scoped>

    .no-back > img {
        width: 50vw;
        max-width: 400px;
    }

</style>
