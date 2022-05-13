<template>
  <v-card-text>
      <h1>{{ userInfo.username }}</h1>
      <h2>Rank: {{userInfo.rank}}</h2>
      <h3>Stats: {{userInfo.stats}}</h3>
      <h3>Score: {{userInfo.rank}}</h3>
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
