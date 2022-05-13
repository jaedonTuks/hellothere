<template>
  <div class="about">
    <h1>{{ username }}</h1>
    <h3>Rank: {{ rank }}</h3>
    <h2 class="mt-5">Badges</h2>

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
import StatsCard from '@/components/StatsCard.vue';

export default {
  name: 'Profile',
  components: { StatsCard },
  data() {
    return {
      username: 'username',
      rank: 'Newbie',
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
};
</script>
