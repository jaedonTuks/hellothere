<template>
  <v-container v-if="loading" fill-height fluid>
    <v-row align="center"
           justify="center">
      <div class="loader rotary"/>
    </v-row>
  </v-container>
</template>
<script>
import loadingMixin from '@/mixins/loadingMixin';

export default {
  name: 'Loader',
  mixins: [loadingMixin],
};
</script>
<style>
@import '../assets/styles/loader.css';

div {
  color: #FFF !important;
}

</style>
