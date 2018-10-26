import {createDecorator} from "vue-class-component";
import {MetaInfo} from "vue-meta";

export const Meta = (data: MetaInfo) => {
    return createDecorator((options: any) => {
        options.head = data;
    });
};

export const MetaMethod = createDecorator((options: any, key) => {
    if (!options || !options.methods) return;
    options.head = options.methods[key];
});