<template>
  <v-card-text class="mt-4">
      <h1>{{ userInfo.username }}</h1>
      <h2 class="mt-5">Total xp earned: {{userInfo.totalXp}}</h2>
      <h3 class="mt-3">Challenges completed: {{userInfo.challengesCompleted}}</h3>
  </v-card-text>
</template>

<script>
export default {
  name: 'LeaderBoardCardInfo',
  props: {
    userInfo: Object,
  },
};
</script>

<style scoped>

</style>
