<template>
  <v-row class="mt-4 pa-3">
    <v-col cols="12">
      <h2 class="mt-5">Challenges</h2>
    </v-col>
    <v-col cols="12">
      <v-row>
        <v-col
          v-for="(challenge, index) in challenges"
          :key="index"
          cols="4"
        >
          <ChallengeCard
            :challenge="challenge"
          />
        </v-col>
      </v-row>
    </v-col>
  </v-row>
</template>

<script>
import ChallengeCard from '@/components/challenge/ChallengeCard.vue';
import { mapActions, mapState } from 'vuex';

export default {
  name: 'ChallengesOverview',
  components: { ChallengeCard },

  data() {
    return {
      loadingChallenges: false,
    };
  },

  computed: {
    ...mapState(['challenges']),
  },

  methods: {
    ...mapActions(['fetchUserChallenges']),
  },

  created() {
    this.fetchUserChallenges()
      .finally(() => {
        this.loadingChallenges = false;
      });
  },
};
</script>

<style scoped>

</style>
