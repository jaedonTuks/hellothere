<template>
  <v-snackbar
      v-model="displaySnackBar"
      right
      top
      text
      style="margin-top: 80px;"
      elevation="10"
      color="red accent-2"
  >
    Testing
  </v-snackbar>
</template>
<script>
import { mapState } from 'vuex';

export default {
  name: 'snackBar',

  computed: {
    ...mapState(['displaySnackBar', 'snackBarColor']),
  },
};
</script>
<style>
@import './assets/styles/loader.css';

div {
  color: #FFF !important;
}

html {
  overflow-y: auto
}
</style>
