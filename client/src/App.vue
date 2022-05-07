<template>
  <v-app class="app">
    <AppHeader/>

    <v-main>
      <div class="home">
        <v-container style="width:100%">
          <router-view/>
        </v-container>
      </div>
    </v-main>
  </v-app>
</template>

<script>

import AppHeader from '@/components/AppHeader.vue';

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
  color: #FFF !important;
}

.app{
 background-color: #343E59!important;
}
</style>
