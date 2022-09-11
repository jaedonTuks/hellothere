<template>
  <v-card-text class="mt-4">
    <h1>{{ userInfo.username }}</h1>
    <h2 v-if="claimedSpot" class="mt-7">
      Title: {{ userInfo.title }}
    </h2>
    <h3 class="mt-7 normalWeight">
      Total xp earned: {{ userInfo.totalXp }}
    </h3>
    <h3 class="mt-3 normalWeight">
      Challenges completed: {{ userInfo.challengesCompleted }}
    </h3>
  </v-card-text>
</template>

<script>
export default {
  name: 'LeaderBoardCardInfo',
  props: {
    userInfo: Object,
  },

  computed: {
    claimedSpot() {
      return this.userInfo.username !== 'Unclaimed';
    },
  },
};
</script>

<style scoped>

</style>
