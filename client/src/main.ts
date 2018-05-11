import Vue from 'vue'
import App from './App.vue'
import router from './router'
import Vuetify from 'vuetify'
import Meta from 'vue-meta';
import moment from 'moment'
import colors from 'vuetify/es5/util/colors'


Vue.config.productionTip = false;
Vue.use(Vuetify, {
    theme: {
        primary: colors.indigo.darken3, // #E53935
        secondary: colors.red.lighten4, // #FFCDD2
        accent: colors.indigo.base // #3F51B5
    }
});
Vue.use(Meta, {
    keyName: 'head'
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

new Vue({
    router,
    render: h => h(App)
}).$mount('#app');

