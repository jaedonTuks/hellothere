<template>
  <v-app class="app">
    <AppHeader/>

    <v-main>
        <Loader/>
        <v-container style="width:100%">
          <v-slide-y-transition mode="out-in">
            <router-view/>
          </v-slide-y-transition>
        </v-container>
    </v-main>
  </v-app>
</template>

<script>

import AppHeader from '@/components/AppHeader.vue';

import Loader from '@/Loader.vue';

export default {
  name: 'App',
  components: { AppHeader, Loader },

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
