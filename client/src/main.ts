import App from './App.vue';
import router from './router';
import Meta from 'vue-meta';
// import moment from 'moment';
import store from './store';
import './initVuetify';
import './registerServiceWorker';
import {distanceInWords, format} from 'date-fns';
import es from 'date-fns/locale/es';
import Vue from 'vue';

Vue.use(Meta, {
    keyName: 'head'
});

Vue.config.productionTip = false;

Vue.filter('multiply', (value: number, multiplier: number = 1) => {
    if (!multiplier || multiplier < 1 || isNaN(multiplier)) return value;
    return value * multiplier;
});

Vue.filter('fn', (value: any) => {
    if (typeof value !== 'number') return value;
    return value.toLocaleString('es-PY');
});

Vue.filter('fd', (value: any, desiredFormat: string | null) => {

    const finalFormat = desiredFormat ? desiredFormat : 'MM/DD/YYYY hh:mm';
    if (value) return format(value, finalFormat);
    return '';
    // if (value) return format(String(value)).format(finalFormat);
});

Vue.filter('mfn', (value: string) => {
    if (value) return distanceInWords(value, new Date(), {locale: es});
    return '';
});

/**
 * This is a dirty hack
 *
 * Vuetify removes the '#app' component from the tree, so if we
 * first 'prerender' the app, the div disappear and we can't mount
 * the main Component, so to fix this we see what is the div that
 * the pre-rendering keeps, and use it to the start Vue.
 *
 * The div that Vuetify doesn't remove is the main 'v-app' id (see
 * App.vue), so we need to use that node instead of '#app'.
 *
 * The second time we 'mount' vue it occur something called hydration,
 * which means that vue is inserted in the 'plain' html
 *
 */
let whereToMount = 'app';
if (!document.getElementById(whereToMount)) whereToMount = 'inspire';

const app = new Vue({
    render: (h: any) => h(App),
    router,
    store,
    el: '#' + whereToMount
} as any);

console.log('loaded', app);
