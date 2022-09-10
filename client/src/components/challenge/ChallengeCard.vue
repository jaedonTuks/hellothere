<template>
  <v-card
    color="accent"
    min-height="350"
    :class="{
      'pa-5': true,
      'claimed': challenge.isRewardClaimed
    }"
  >
    <v-card-title class="pb-4">
      <v-row>
        <h2>{{ challenge.name }}</h2>
      </v-row>
      <v-progress-circular
        class="float-end progress"
        color="primary"
        :value="percentageComplete"
      >
        <v-expand-x-transition>
          <v-icon color="purpleAccent progress" v-if="challenge.isRewardClaimed">mdi-check</v-icon>
        </v-expand-x-transition>
      </v-progress-circular>
    </v-card-title>
    <div class="gradiantBorderBottom gradiantBorderBottomFullWidth"></div>
    <v-card-text class="mt-2 pa-2">
      <v-row v-if="challenge.isRewardClaimed" class="pa-4">
        <h3 class="mb-3 mr-4 pr-4">Completed</h3>
      </v-row>
      <v-row v-else class="pa-4">
        <h3 class="mb-3 mr-4 pr-4">Current: {{ challenge.progress }}</h3>
        <h3 class="mb-5">Goal: {{ challenge.goal }}</h3>
      </v-row>
      <div class="mb-5 pl-4">{{ challenge.description }}</div>
      <h3 class="mb-3"> Rewards: </h3>
      <h4 class="titleReward normalWeight pl-4 mb-2">Title: {{ challenge.title }}</h4>
      <h4 class="normalWeight pl-4">{{ challenge.reward }} <span class="xp"> XP</span></h4>
    </v-card-text>
    <v-card-actions>
      <v-btn
        v-if="percentageComplete >= 100 && !challenge.isRewardClaimed"
        color="purpleAccent darken-2"
        ref="rewardButton"
        :loading="claimingReward"
        @click="claimReward($event)"
      >
        Claim reward!
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import calculationsMixin from '@/mixins/calculationsMixin';
import { mapActions, mapGetters } from 'vuex';
import party from 'party-js';

export default {
  name: 'ChallengeCard',
  mixins: [calculationsMixin],

  data() {
    return {
      claimingReward: false,
      challenge: {},
    };
  },

  props: {
    challengeId: Number,
  },

  computed: {
    ...mapGetters(['getChallenge']),

    percentageComplete() {
      return this.getPercentage(this.challenge.progress, this.challenge.goal);
    },
  },

  methods: {
    ...mapActions(['claimUserChallengeReward']),

    claimReward(event) {
      this.claimingReward = true;
      this.claimUserChallengeReward(this.challengeId)
        .then(() => {
          this.setChallenge();
          party.confetti(event, {
            count: party.variation.range(20, 40),
          });
        }).finally(() => {
          this.claimingReward = false;
        });
    },

    setChallenge() {
      this.challenge = this.getChallenge(this.challengeId);
    },
  },

  created() {
    this.setChallenge();
  },
};
</script>

<style scoped>
.claimed div {
  opacity: 0.8;
}

.claimed .progress {
  opacity: 1!important;
}

div {
  font-size: 1.05em;
}
</style>
