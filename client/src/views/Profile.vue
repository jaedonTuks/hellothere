<template>
  <div v-if="profileInfo" class="about">
    <h1 class="username">{{ profileInfo.username }}</h1>
    <h3 class="mt-4 mt-lg-1">Total XP: {{ profileInfo.totalExperience }}</h3>
    <h3 class="mt-4 mt-lg-1">This weeks XP: {{ getCurrentWeeksXP() }}</h3>
    <h3 class="mt-4 mt-lg-2">Rank on leaderboard: {{ profileInfo.rank }}</h3>

    <v-row>

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

    <h2 class="mt-5">Weekly stats</h2>
    <v-row>
      <StatsCard
        title="Bar"
        type="bar"
        :chart-options="barChart.chartOptions"
        :series="barChart.series"
      />
      <StatsCard
        title="Radial Bar"
        type="radialBar"
        :chart-options="radialBarOptions.plotOptions"
        :series="radialBarOptions.series"
      />
    </v-row>
  </div>
</template>

<script>
import StatsCard from '@/components/Leaderboard/StatsCard.vue';
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'Profile',
  components: { StatsCard },
  data() {
    return {
      profileInfo: null,
      radialBarOptions: {
        chart: {
          type: 'radialBar',
          height: 350,
          width: 380,
        },
        plotOptions: {
          radialBar: {
            size: undefined,
            inverseOrder: true,
            hollow: {
              margin: 5,
              size: '48%',
              background: 'transparent',

            },
            track: {
              show: false,
            },
            startAngle: -180,
            endAngle: 180,

          },
        },
        stroke: {
          lineCap: 'round',
        },
        series: [71, 63, 77],
        labels: ['June', 'May', 'April'],
        legend: {
          show: true,
          floating: true,
          position: 'right',
          offsetX: 70,
          offsetY: 240,
        },
      },
      barChart: {
        chartOptions: {
          chart: {
            id: `vuechart-${this.title}`,
          },
          xaxis: {
            categories: [1991, 1992, 1993, 1994, 1995, 1996, 1997, 1998],
          },
        },
        series: [{
          name: 'series-1',
          data: [30, 40, 45, 50, 49, 60, 70, 81],
        }],
      },
    };
  },

  computed: {
    ...mapGetters(['getProfile']),
  },

  methods: {
    ...mapActions(['fetchUserInfo']),

    getBadges() {
      if (this.profileInfo.badges) {
        return this.profileInfo.badges;
      }
      return 'Complete challenges to earn badges';
    },

    getCurrentWeeksXP() {
      if (this.profileInfo && this.profileInfo.currentWeekStats) {
        return this.profileInfo.currentWeekStats.experience;
      }

      return 0;
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

@media only screen and (max-width: 1264px) {
  h1.username {
    font-size: 1.3em!important;
  }

  h2 {
    font-size: 1.4em;
  }
}
</style>
