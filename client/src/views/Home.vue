<template>
    <div class="home">
        <Exchange msg="test"/>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from 'vue-property-decorator';
    import Exchange from '../components/Exchange'; // @ is an alias to /src

    @Component({
        components: {
            Exchange,
        },
    })
    export default class Home extends Vue {
    }
</script>
