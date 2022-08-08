<template>
  <div v-if="profileInfo" class="about">
    <h1 class="username">{{ profileInfo.username }}</h1>
    <h3 class="mt-4 mt-lg-1">Total XP: {{ profileInfo.totalExperience }}</h3>
    <h3 class="mt-4 mt-lg-1">This weeks XP: {{ currentWeekXP }}</h3>
    <h3 class="mt-4 mt-lg-2">Rank on leaderboard: {{ profileInfo.rank }}</h3>
    <div class="mb-4 mt-4 gradiantBorderBottom gradiantBorderBottomFullWidth"/>

    <v-row class="mt-4 pa-2">
      <v-col cols="12" lg="6">
        <h2 class="mt-5">Badges</h2>
        <div>{{ getBadges() }}</div>
      </v-col>
      <v-col cols="12" lg="6">
        <h2 class="mt-5">Challenges</h2>

        <h3>Generals</h3>
        <h3>Daily</h3>
        <h3>Weekly</h3>
      </v-col>
    </v-row>
    <div class="mt-5 mb-4 gradiantBorderBottom gradiantBorderBottomFullWidth"/>

    <v-row class="pa-3">
      <h2>Weekly stats</h2>
    </v-row>
    <v-row>
      <v-col cols="12 mb-0">
        <span class="mr-2">Total Emails:</span>
        <span>{{profileInfo.messageTotalsSummary.totalEmails}}</span>
      </v-col>
      <StatsCard
        title="Weekly experience overview"
        type="bar"
        :series="getExperienceSeries()"
      />
      <StatsCard
        title="Interaction Overview"
        type="radialBar"
        :series="getTotalsRadialSeries()"
        :labels="getTotalsLabels()"
      />
      <StatsCard
        title="Challenges Completed"
        type="radialBarPercentage"
        :series="getChallengesCompletedPercentageSeries()"
      />
    </v-row>
  </div>
</template>

<script>
import StatsCard from '@/components/Leaderboard/StatsCard.vue';
import { mapActions, mapGetters } from 'vuex';
import ScreenSizeMixin from '@/mixins/screenSizeMixin';

export default {
  name: 'Profile',
  components: { StatsCard },
  mixins: [ScreenSizeMixin],

  data() {
    return {
      profileInfo: null,
    };
  },

  computed: {
    ...mapGetters(['getProfile']),

    currentWeekXP() {
      if (this.profileInfo && this.profileInfo.currentWeekStats) {
        return this.profileInfo.currentWeekStats.experience;
      }

      return 0;
    },

    totalRead() {
      return this.getTotalsPercentage(this.profileInfo.messageTotalsSummary.totalRead);
    },

    totalLabeled() {
      return this.getTotalsPercentage(this.profileInfo.messageTotalsSummary.totalLabeled);
    },

    totalReplied() {
      return this.getTotalsPercentage(this.profileInfo.messageTotalsSummary.totalReplied);
    },
  },

  methods: {
    ...mapActions(['fetchUserInfo']),

    getTotalsPercentage(value) {
      return Math.round((value / this.profileInfo.messageTotalsSummary.totalEmails) * 100);
    },

    getBadges() {
      if (this.profileInfo.badges) {
        return this.profileInfo.badges;
      }
      return 'Complete challenges to earn badges';
    },

    getChallengesCompletedPercentageSeries() {
      // todo implement correctly
      return [100];
    },

    getTotalsRadialSeries() {
      return [
        this.totalRead,
        this.totalLabeled,
        this.totalReplied,
      ];
    },

    getTotalsLabels() {
      if (!this.isMobile) {
        return ['Read', 'Labeled', 'Replied'];
      }
      return [
        `Read ${this.totalRead}%`,
        `Labeled ${this.totalLabeled}%`,
        `Replied ${this.totalReplied}%`,
      ];
    },

    getExperienceSeries() {
      const xpArray = [];

      this.profileInfo.orderedWeekStats.forEach((stat, index) => {
        xpArray.push({
          x: `Week ${index + 1} xp`,
          y: `${stat.experience} xp`,
        });
      });
      return [{ data: xpArray }];
    },

    getExperienceLabels() {
      return [];
    },

  },

  created() {
    this.fetchUserInfo().then(() => {
      this.profileInfo = this.getProfile();
    });
  },
};
</script>

<style scoped>
.borderTop {
  border-top: solid 2px white;
}

@media only screen and (max-width: 1264px) {
  h1.username {
    font-size: 1.3em !important;
  }

  h2 {
    font-size: 1.4em;
  }
}
</style>
