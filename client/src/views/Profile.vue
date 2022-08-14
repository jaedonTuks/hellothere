<template>
  <div v-if="profileInfo" class="about">
    <v-row dense class="mt-3">
      <v-col cols="2">
        <v-text-field
          v-model="newUserName"
          flat
          background-color="background"
          ref="usernameField"
          style="font-size: 2em; margin: 0"
          hide-details="auto"
          class="pl-0 ml-0"
          :solo="!editingUsername"
          :append-icon="editingUsername ? 'mdi-check' : 'mdi-pencil' "
          :readonly="!editingUsername"
          @click:append="toggleEditUsername"
        />
      </v-col>
      <v-col cols="12">
        <h3 class="mt-4 mt-lg-1">Email: {{ profileInfo.email }}</h3>
      </v-col>
      <v-col cols="12">
        <h3 class="mt-4 mt-lg-1">Total XP: {{ profileInfo.totalExperience }}</h3>
      </v-col>
      <v-col cols="12">
        <h3 class="mt-4 mt-lg-1">This weeks XP: {{ currentWeekXP }}</h3>
      </v-col>
      <v-col cols="12">
        <h3 class="mt-4 mt-lg-2">Rank on leaderboard: {{ profileInfo.rank }}</h3>
      </v-col>
      <v-col cols="12" lg="6">
        <h2 class="mt-5">Badges</h2>
        <div>{{ getBadges() }}</div>
      </v-col>
    </v-row>

    <v-btn class="mt-4  mr-2" color="secondary" @click="logout">Logout</v-btn>
    <div class="mb-4 mt-4 gradiantBorderBottom gradiantBorderBottomFullWidth"/>
    <ChallengesOverview/>
    <div class="mt-5 mb-4 gradiantBorderBottom gradiantBorderBottomFullWidth"/>

    <v-row class="pa-3">
      <h2>Weekly stats</h2>
    </v-row>
    <v-row>
      <v-col cols="12 mb-0">
        <span class="mr-2">Total Emails:</span>
        <span>{{ profileInfo.messageTotalsSummary.totalEmails }}</span>
      </v-col>
      <StatsCard
        title="Weekly experience overview"
        type="bar"
        :series="experienceSeries"
      />
      <StatsCard
        title="Emails Overview"
        type="radialBar"
        :series="getTotalsRadialSeries()"
        :labels="getTotalsLabels()"
      />
      <StatsCard
        title="Challenges Completed"
        type="radialBarPercentage"
        :series="challengesCompletedPercentageSeries"
      />
    </v-row>
  </div>
</template>

<script>
import StatsCard from '@/components/Leaderboard/StatsCard.vue';
import ChallengesOverview from '@/components/challenge/ChallengesOverview.vue';
import { mapActions, mapGetters, mapState } from 'vuex';
import ScreenSizeMixin from '@/mixins/screenSizeMixin';
import calculationsMixin from '@/mixins/calculationsMixin';

export default {
  name: 'Profile',
  components: { StatsCard, ChallengesOverview },
  mixins: [ScreenSizeMixin, calculationsMixin],

  data() {
    return {
      profileInfo: null,
      editingUsername: false,
      newUserName: '',
    };
  },

  computed: {
    ...mapGetters(['getProfile', 'getCompletedChallenges']),
    ...mapState(['challenges']),

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

    challengesCompleted() {
      return this.getCompletedChallenges().length;
    },

    challengesCount() {
      return this.challenges.length;
    },

    challengesCompletedPercentageSeries() {
      return [this.getPercentage(this.challengesCompleted, this.challengesCount)];
    },

    experienceSeries() {
      const xpArray = [];

      this.profileInfo.orderedWeekStats.forEach((stat, index) => {
        xpArray.push({
          x: `Week ${index + 1} xp`,
          y: `${stat.experience} xp`,
        });
      });
      return [{ data: xpArray }];
    },
  },

  methods: {
    ...mapActions(['fetchUserInfo', 'sendLogoutRequest', 'sendUpdateUsernameRequest']),

    getTotalsPercentage(value) {
      return this.getPercentage(value, this.profileInfo.messageTotalsSummary.totalEmails);
    },

    getBadges() {
      if (this.profileInfo.badges) {
        return this.profileInfo.badges;
      }
      return 'Complete challenges to earn badges';
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

    toggleEditUsername() {
      this.editingUsername = !this.editingUsername;
      if (this.editingUsername) {
        this.$refs.usernameField.focus();
      } else {
        this.sendUpdateUsernameRequest({ newUsername: this.newUserName })
          .then(() => {
            this.newUserName = this.profileInfo.leaderboardUsername;
          });
      }
    },

    logout() {
      this.sendLogoutRequest()
        .then(() => {
          this.$router.push({ name: 'Login' });
        });
    },

  },

  created() {
    this.fetchUserInfo().then(() => {
      this.profileInfo = this.getProfile();
      this.newUserName = this.profileInfo.leaderboardUsername;
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
