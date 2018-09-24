import 'vuetify/src/stylus/app.styl';
import theme from './theme';


import VImg from 'vuetify/es5/components/VImg';

import Vue from 'vue';
import Vuetify from 'vuetify/es5/components/Vuetify';
import VApp from 'vuetify/es5/components/VApp';
import VBtn from 'vuetify/es5/components/VBtn';
import VCard from 'vuetify/es5/components/VCard';
import VDataIterator from 'vuetify/es5/components/VDataIterator';
import VDataTable from 'vuetify/es5/components/VDataTable';
import VDialog from 'vuetify/es5/components/VDialog';
import VFooter from 'vuetify/es5/components/VFooter';
import VGrid from 'vuetify/es5/components/VGrid';
import VIcon from 'vuetify/es5/components/VIcon';
import VList from 'vuetify/es5/components/VList';
import VNavigationDrawer from 'vuetify/es5/components/VNavigationDrawer';
import VPagination from 'vuetify/es5/components/VPagination';
import VProgressCircular from 'vuetify/es5/components/VProgressCircular';
import VSelect from 'vuetify/es5/components/VSelect';
import VSubheader from 'vuetify/es5/components/VSubheader';
import VTabs from 'vuetify/es5/components/VTabs';
import VTextField from 'vuetify/es5/components/VTextField';
import VToolbar from 'vuetify/es5/components/VToolbar';
import VDivider from 'vuetify/es5/components/VDivider';


Vue.use(Vuetify, {
    components: {
        VApp,
        VBtn,
        VCard,
        VDataIterator,
        VDataTable,
        VDialog,
        VFooter,
        VGrid,
        VIcon,
        VList,
        VNavigationDrawer,
        VPagination,
        VProgressCircular,
        VSelect,
        VSubheader,
        VTabs,
        VTextField,
        VToolbar,
        VImg,
        VDivider
    },
    theme
});

export const data = {
    ok: true
};
