import Vue from 'vue'
import App from './App.vue'
import router from './router'
import Vuetify from 'vuetify'
import colors from 'vuetify/es5/util/colors'


Vue.config.productionTip = false;
Vue.use(Vuetify, {
    theme: {
        primary: colors.indigo.darken3, // #E53935
        secondary: colors.red.lighten4, // #FFCDD2
        accent: colors.indigo.base // #3F51B5
    }
});

new Vue({
    router,
    render: h => h(App)
}).$mount('#app')

