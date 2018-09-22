console.log('iniciando');

import Vue from 'vue'
import App from './App.vue'
import router from './router'
import Vuetify from 'vuetify'
import Meta from 'vue-meta';
import moment from 'moment'
import store from './store';
import './registerServiceWorker';
import theme from './theme';
// import 'babel-polyfill'


Vue.config.productionTip = false;
Vue.use(Vuetify, {theme});
Vue.use(Meta, {
    keyName: 'head'
});

Vue.filter('multiply', function (value: number, multiplier: number = 1) {
    if (!multiplier || multiplier < 1 || isNaN(multiplier)) return value;
    return value * multiplier;
});

Vue.filter('fn', function (value: number) {
    if (typeof value !== "number") {
        return value;
    }
    return value.toLocaleString('es-PY');
});

Vue.filter('fd', function (value: string, format: string) {

    (<any> window).m = moment;
    const finalFormat = format ? format : 'MM/DD/YYYY hh:mm';
    if (value) {
        return moment(String(value)).format(finalFormat)
    }
});

Vue.filter('mfn', function (value: string) {

    if (value) {
        return moment(String(value)).locale('es').fromNow();
    }
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
 **/
let whereToMount = 'app';
if (!document.getElementById(whereToMount)) {
    whereToMount = 'inspire';
}

new Vue({
    render: h => h(App),
    router,
    store,
    el: '#' + whereToMount
});

