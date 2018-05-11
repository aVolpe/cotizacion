import {MetaInfo} from 'vue-meta';
import Vue from "vue";

declare module '*.vue' {
    export default Vue;
}

declare module "json!*" {
    let json: any;
    export = json;
}

declare module "*.json" {
    const value: any;
    export default value;
}

declare module "vue/types/vue" {
    interface Vue {
        metaInfo?: MetaInfo | (() => MetaInfo)
    }
}
