import Vue from "vue";
import Router from "vue-router";
import About from "./views/About.vue";
import LatestExchange from "./views/LatestExchange.vue";
import BackendDown from "./views/BackendDown.vue";
import Licenses from "./views/Licenses.vue";
import Swagger from "./views/Swagger.vue";
import Map from "./views/Map.vue";

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: "/",
            name: "latest",
            component: LatestExchange
        },
        {
            path: "/about",
            name: "about",
            component: About
        },
        {
            path: "/map",
            name: "map",
            component: Map
        },
        {
            path: "/licenses",
            name: "licenses",
            component: Licenses
        },
        {
            path: "/swagger",
            name: "",
            component: Swagger
        },
        {
            path: "/no-back",
            name: "",
            component: BackendDown
        },
    ]
});
