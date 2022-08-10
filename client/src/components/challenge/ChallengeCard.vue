<template>
  <v-card color="accent" class="pa-5">
    <v-card-title class="borderBottom pb-4">
      <v-row>
       <h3>{{ challenge.name }}</h3>
      </v-row>
      <v-progress-circular class="float-end" :value="percentageComplete"/>
    </v-card-title>
    <v-card-text class="mt-2 pa-2">
      <v-row class="pa-4">
        <span class="mb-3 mr-4 pr-4">Current: {{ challenge.progress }}</span>
        <span class="mb-5">Goal: {{ challenge.goal }}</span>
      </v-row>
      <div class="mb-5">{{ challenge.description }}</div>
      <div class="mb-3"> Rewards: {{ challenge.reward }} xp</div>
    </v-card-text>
    <v-card-actions>
      <v-btn
        v-if="percentageComplete >= 100 && !challenge.isRewardClaimed"
        color="purpleAccent darken-2"
      >
        Claim reward!
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import calculationsMixin from '@/mixins/calculationsMixin';

export default {
  name: 'ChallengeCard',
  mixins: [calculationsMixin],

  props: {
    challenge: Object,
  },

  computed: {
    percentageComplete() {
      return this.getPercentage(this.challenge.progress, this.challenge.goal);
    },
  },
};
</script>

<style scoped>
  div {
    font-size: 1.05em;
  }
</style>
