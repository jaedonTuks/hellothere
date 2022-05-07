<template>
  <v-app class="app">
    <AppHeader/>

    <v-main>
        <v-container v-if="loading" fill-height fluid>
          <v-row align="center"
                 justify="center">
            <div class="loader rotary"/>
          </v-row>
        </v-container>
        <v-container v-else style="width:100%">
          <v-slide-y-transition mode="out-in">
            <router-view/>
          </v-slide-y-transition>
        </v-container>
    </v-main>
  </v-app>
</template>

<script>

import AppHeader from '@/components/AppHeader.vue';
import { mapMutations, mapState } from 'vuex';

export default {
  name: 'App',
  components: { AppHeader },
  data: () => ({
    //
  }),

  computed: {
    ...mapState(['loading']),
  },

  methods: {
    ...mapMutations(['updateLoading']),
  },
};
</script>
<style>
@import './assets/styles/loader.css';

div {
  color: #FFF !important;
}

.app {
  background-color: #343E59 !important;
}
</style>
