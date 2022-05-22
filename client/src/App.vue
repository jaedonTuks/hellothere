<template>
  <v-app class="app">
    <AppHeader v-if="shouldDisplayHeader"/>

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

  computed: {
    shouldDisplayHeader() {
      return this.$route.name !== 'Login';
    },
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
