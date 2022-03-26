<template>
  <v-app>
    <AppHeader/>

    <v-main>
      <router-view/>
    </v-main>
  </v-app>
</template>

<script>

import AppHeader from '@/AppHeader.vue';

export default {
  name: 'App',
  components: { AppHeader },
  data: () => ({
    //
  }),
};
</script>
<style>
div {
  color: var(--v-accent-darken1)!important;
}
</style>
