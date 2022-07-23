<template>
  <v-btn
    class="button"
    color="secondary"
    fixed
    bottom
    right
    @click="setComposingEmail(true)"
  >
    Compose
    <v-icon class="ml-2">mdi-pencil</v-icon>
  </v-btn>
</template>

<script>
import { mapMutations } from 'vuex';

export default {
  name: 'SendActionButton',

  methods: {
    ...mapMutations(['setComposingEmail']),
  },
};
</script>

<style scoped>
.button {
  z-index: 100!important;
  margin-bottom: 60px;
  border-radius: 8px;
}
</style>
