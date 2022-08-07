<template>
  <v-col v-if="chartOptions && series" cols="12" lg="4" class="mt-5">
    <v-card color="accent" class="pa-5">
      <v-card-title>{{ title }}</v-card-title>
      <v-card-text>
        <apexchart
          :type="type"
          :options="chartOptions"
          :series="series"
        />
      </v-card-text>
    </v-card>
  </v-col>
</template>
<script type="module">
export default {
  name: 'StatsCard',

  props: {
    title: {
      type: String,
      required:
        true,
    },
    type: {
      type: String,
      required:
        true,
    },
    series: {
      type: Array,
      required:
        true,
    },
    labels: Array,
  },

  computed: {
    chartOptions() {
      if (this.type === 'radialBar') {
        return this.getGenericRadialChartOptions();
      }

      return this.getGenericBarChartOptions();
    },
  },

  methods: {

    getGenericRadialChartOptions() {
      return {
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
            dataLabels: {
              name: {
                fontSize: '30px',
                show: true,
              },
              value: {
                color: '#FFF',
                fontSize: '20px',
                show: true,
              },
            },
          },
          total: {
            show: true,
            label: 'Total',
          },
        },
        stroke: {
          lineCap: 'round',
        },
        labels: this.labels,
        legend: {
          horizontalAlign: 'center',
          show: true,
          floating: true,
          position: 'bottom',
          fontSize: '20px',
          labels: {
            colors: ['#FFF'],
          },
        },
      };
    },

    getGenericBarChartOptions() {
      return {
        chart: {
          height: 350,
          type: 'bar',
        },
        plotOptions: {
          bar: {
            columnWidth: '45%',
            distributed: true,
          },
        },
        legend: {
          show: false,
        },
        xaxis: {
          tooltip: {
            enabled: false,
          },
          labels: {
            style: {
              fontSize: '15px',
              colors: '#FFF',
            },
          },
        },
        yaxis: {
          tooltip: {
            enabled: false,
          },
          labels: {
            style: {
              fontSize: '15px',
              colors: '#FFF',
            },
          },
        },
      };
    },
  },
};
</script>
