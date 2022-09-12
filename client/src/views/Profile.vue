<template>
  <div v-if="profileInfo" class="about">
    <v-row dense class="mt-3">
      <v-col cols="12">
        <h1 class="mt-4 mt-lg-1"> {{ profileInfo.leaderboardUsername }}</h1>
      </v-col>
      <v-col v-if="isGamificationEnabled" cols="12">
        <v-row align="center">
          <v-col cols="auto">
            <h2 class="mt-4 mt-lg-1">Title:</h2>
          </v-col>
          <v-col cols="8" lg="2">
            <v-select
              v-model="title"
              label=""
              :disabled="availableTitles.length < 2"
              :items="availableTitles"
              @change="updateTitle"
            />
          </v-col>
        </v-row>
      </v-col>
      <v-col cols="12">
        <h3 class="mt-4 mt-lg-1 normalWeight">Email: {{ profileInfo.email }}</h3>
      </v-col>
      <v-col v-if="isGamificationEnabled" cols="12">
        <h2 class="mt-4 mt-lg-2">Rank on leaderboard: {{ profileInfo.rank }}</h2>
      </v-col>
      <v-col v-if="isGamificationEnabled" cols="12">
        <h2 class="level">Level: {{ Math.floor(profileInfo.totalExperience / 50) }}</h2>
        <h3 class="level normalWeight">
          <span class="xp">XP</span> until next level: {{ 50 - (profileInfo.totalExperience % 50) }}
        </h3>
      </v-col>
    </v-row>

    <v-btn class="mt-4  mr-2" @click="viewingCredits=true">Credits</v-btn>
    <v-btn class="mt-4  mr-2" @click="logout">Logout</v-btn>

    <v-alert
      v-model="viewingCredits"
      border="left"
      colored-border
      type="info"
      elevation="2"
      dismissible
      outlined
      transition="scale-transition"
    >
      <h1>References</h1>
      <div>
        <h3>Loader</h3>
        Rotary from whirl.netlify.app @author jh3y
      </div>
      <div>App header icons and favicon made by <a href="https://www.freepik.com" title="Freepik">
        Freepik </a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com'</a>
      </div>
      <h3>
        See <a href="https://github.com/Jaedon-Heger/hellothere">github</a> pom.xml and package.json for idea of what packages were used
      </h3>
    </v-alert>

    <div class="mb-4 mt-4 gradiantBorderBottom gradiantBorderBottomFullWidth"/>
    <v-tabs
      v-if="isGamificationEnabled"
      v-model="selectedProfileViewIndex"
      centered
      center-active
      grow
      show-arrows
      background-color="background"
      class="mb-4"
    >
      <v-tab>
        <v-icon class="mr-2">mdi-chart-line</v-icon>
        Personal Stats
      </v-tab>
      <v-tab>
        <v-icon class="mr-2">mdi-checkbox-marked-circle-plus-outline</v-icon>
        Challenges
      </v-tab>
      <v-tab>
        <v-icon class="mr-2">mdi-cog</v-icon>
        Settings
      </v-tab>
    </v-tabs>

    <v-tabs-items
      v-if="isGamificationEnabled"
      v-model="selectedProfileViewIndex"
    >
      <v-tab-item style="background-color: #343E59">
        <v-row align="center" align-content="center">
          <StatsCard
            title="Experience overview"
            type="bar"
            :series="experienceSeries"
          >
            <span class="xp">Total XP</span>: {{ profileInfo.totalExperience }}
          </StatsCard>
          <StatsCard
            title="Emails Overview"
            type="radialBar"
            :series="getTotalsRadialSeries()"
            :labels="getTotalsLabels()"
          >
            Total Emails: {{ profileInfo.messageTotalsSummary.totalEmails }}
          </StatsCard>
          <StatsCard
            title="Challenges Completed"
            type="radialBarPercentage"
            :series="challengesCompletedPercentageSeries"
          />
        </v-row>
      </v-tab-item>
      <v-tab-item style="background-color: #343E59">
        <ChallengesOverview/>
      </v-tab-item>
      <v-tab-item style="background-color: #343E59">
        <Settings/>
      </v-tab-item>
    </v-tabs-items>

    <v-row v-if="!isGamificationEnabled" justify="center" align="center">
      <v-col cols="12">
        <h1 style="text-align: center">
          Keep your eyes peeled. Challenges stats and more will be available here next week!
        </h1>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import StatsCard from '@/components/Leaderboard/StatsCard.vue';
import ChallengesOverview from '@/components/challenge/ChallengesOverview.vue';
import { mapActions, mapGetters, mapState } from 'vuex';
import ScreenSizeMixin from '@/mixins/screenSizeMixin';
import calculationsMixin from '@/mixins/calculationsMixin';
import Settings from '@/components/Settings.vue';
import featureFlags from '@/mixins/featureFlags';
import EventBus from '@/EventBus';

export default {
  name: 'Profile',
  components: { StatsCard, ChallengesOverview, Settings },
  mixins: [ScreenSizeMixin, calculationsMixin, featureFlags],

  data() {
    return {
      title: null,
      profileInfo: null,
      availableTitles: [],
      editingUsername: false,
      viewingCredits: false,
      newUserName: '',
      selectedProfileViewIndex: 0,
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
    ...mapActions(['fetchUserInfo', 'sendLogoutRequest', 'sendUpdateTitleRequest']),

    getTotalsPercentage(value) {
      return this.getPercentage(value, this.profileInfo.messageTotalsSummary.totalEmails);
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

    logout() {
      this.sendLogoutRequest()
        .then(() => {
          this.$router.push({ name: 'Login' });
        });
    },

    updateTitle() {
      if (this.profileInfo.title !== this.title) {
        this.sendUpdateTitleRequest({ newTitle: this.title });
      }
    },

  },

  created() {
    this.fetchUserInfo().then(() => {
      this.profileInfo = this.getProfile();
      this.availableTitles = this.profileInfo.availableTitles;
      this.title = this.profileInfo.title;
      this.newUserName = this.profileInfo.leaderboardUsername;
    });

    EventBus.$on('updated-profile-titles', () => {
      console.log('updating profile');
      this.profileInfo = this.getProfile();
      this.availableTitles = this.profileInfo.availableTitles;
    });
  },

  beforeDestroy() {
    EventBus.$off('updated-profile-titles');
  },
};
</script>

<style scoped>
.borderTop {
  border-top: solid 2px white;
}

h2 {
  font-size: 1.4em;
  opacity: 0.9;
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
