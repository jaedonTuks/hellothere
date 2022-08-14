<template>
  <v-card
    color="accent"
    min-height="320"
    :class="{
      'pa-5': true,
      'claimed': challenge.isRewardClaimed
    }"
  >
    <v-card-title class="borderBottom pb-4">
      <v-row>
        <h3>{{ challenge.name }}</h3>
      </v-row>
      <v-progress-circular
        class="float-end progress"
        color="purpleAccent"
        :value="percentageComplete"
      >
        <v-expand-x-transition>
          <v-icon color="purpleAccent progress" v-if="challenge.isRewardClaimed">mdi-check</v-icon>
        </v-expand-x-transition>
      </v-progress-circular>
    </v-card-title>
    <v-card-text class="mt-2 pa-2">
      <v-row v-if="challenge.isRewardClaimed" class="pa-4">
        <span class="mb-3 mr-4 pr-4">Completed</span>
      </v-row>
      <v-row v-else class="pa-4">
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
