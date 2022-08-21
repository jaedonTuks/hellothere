<template>
  <v-dialog-transition>
    <v-card
        v-show="emailThreads.length === 0"
        class="mt-8"
        justify="center"
        color="background"
        height="100%"
        width="100%"
        elevation="0"
    >
      <v-progress-linear
          v-show="filteringEmails"
          indeterminate
          color="primary"
      />
      <v-row
          class="noEmails"
          justify="center"
          align="center"
      >
        <h1 style="text-align: center">
          No emails available for selection <br>
          Try selecting a different label
        </h1>
      </v-row>
    </v-card>
  </v-dialog-transition>
</template>
<script>
export default {
  name: 'NoEmailsCard',
  props: {
    emailThreads: {},
    filteringEmails: {},
  },
};
</script>
<style scoped>

.noEmails {
  height: 60vh !important;
}
</style>
