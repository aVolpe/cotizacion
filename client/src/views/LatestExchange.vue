<template>
    <div v-if="currencies.loaded">
        <ListOfExchanges></ListOfExchanges>
        <v-dialog v-model="exchangeDialog.show" max-width="500px" :fullscreen="isSmall">
            <ExchangeData v-on:ok="hideExchangeDialog" :data="exchangeDialog.data"></ExchangeData>
        </v-dialog>
    </div>
    <div v-else>
        <img :src="`${baseUrl}imgs/loading.svg`"
             style="margin: auto; width: 200px; height: 200px; max-width: 20vw; max-height: 20vw">
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import ExchangeData from "@/components/ExchangeData.vue";
    import MapOfExchanges from "@/components/MapOfExchanges.vue";
    import ListOfExchanges from "@/components/ListOfExchanges.vue";
    import {MetaMethod} from "@/decorators";
    import {Action, State} from "vuex-class";
    import {Loaded} from '@/store';

    @Component({
        components: {
            ExchangeData,
            MapOfExchanges,
            ListOfExchanges
        }
    })
    export default class LatestExchange extends Vue {
        @Action fetchCurrencies!: (isoCode: string) => void;
        @Action hideExchangeDialog!: () => void;
        @State exchangeDialog!: { loading: boolean; show: boolean };
        @State current!: any;
        @State currencies!: Loaded<string[]>;
        tabModel: number = 0;
        isSmall: boolean;
        baseUrl: string | undefined;

        constructor() {
            super();
            this.isSmall = window.innerWidth < 600;
            this.baseUrl = process.env.BASE_URL;
        }

        @MetaMethod
        meta() {
            return {
                title: `Cotización de ${this.current.currency}`,
                titleTemplate: undefined
            };
        }

        mounted() {
            this.fetchCurrencies(this.$route.query.moneda);
        }

    }
</script>
