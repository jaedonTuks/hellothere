<template>
  <v-col
    v-if="chartOptions && series"
    cols="12"
    lg="4"
    class="mt-5"
  >
    <v-card
      color="accent"
      class="pa-5"
      elevation="4"
      :min-height="isMobile ? 200 : 520"
    >
      <v-card-title class="mb-3"><h2>{{ title }}</h2></v-card-title>
      <v-card-subtitle>
        <h3>
          <slot/>
        </h3>
      </v-card-subtitle>
      <v-card-text>
          <apexchart
            :type="apexChartType"
            :options="chartOptions"
            :series="series"
            style="margin: 0 auto"
          />
      </v-card-text>
    </v-card>
  </v-col>
</template>
<script type="module">
import screenSizeMixin from '@/mixins/screenSizeMixin';

export default {
  name: 'StatsCard',

  mixins: [screenSizeMixin],

  data() {
    return {
      chartHeight: 350,
    };
  },

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
      switch (this.type) {
        case 'radialBar':
          return this.getGenericRadialChartOptions();
        case 'radialBarPercentage':
          return this.getGenericRadialBarProgressOptions();
        default:
          return this.getGenericBarChartOptions();
      }
    },

    apexChartType() {
      switch (this.type) {
        case 'radialBar':
        case 'radialBarPercentage':
          return 'radialBar';
        default:
          return 'bar';
      }
    },

    chartWidth() {
      return 350;
    },

    legend() {
      return {
        horizontalAlign: 'center',
        show: true,
        floating: true,
        position: 'bottom',
        fontSize: this.isMobile ? '15px' : '20px',
        labels: {
          colors: ['#FFF'],
        },
      };
    },

    radialDataLabels() {
      return {
        name: {
          fontSize: this.isMobile ? '15px' : '30px',
          show: true,
        },
        value: {
          color: '#FFF',
          fontSize: '20px',
          show: true,
        },
      };
    },
  },

  methods: {

    getGenericRadialChartOptions() {
      return {
        chart: {
          type: 'radialBar',
          height: this.chartHeight,
          width: this.chartWidth,
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
            dataLabels: this.radialDataLabels,
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
        legend: this.legend,
      };
    },

    getGenericBarChartOptions() {
      return {
        chart: {
          height: 350,
          type: 'bar',
          toolbar: {
            show: false,
          },
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
        tooltip: {
          enabled: false,
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

    getGenericRadialBarProgressOptions() {
      let label = 'Challenges Completed';
      if (this.isMobile) {
        label = '';
      }
      return {
        chart: {
          height: 280,
          type: 'radialBar',
        },

        series: [67],
        colors: ['#87f0fc'],
        plotOptions: {
          radialBar: {
            hollow: {
              margin: 0,
              size: '70%',
              background: '#293450',
            },
            track: {
              dropShadow: {
                enabled: true,
                top: 2,
                left: 0,
                blur: 4,
                opacity: 0.15,
              },
            },
            dataLabels: {
              name: {
                offsetY: -10,
                color: '#fff',
                fontSize: '20px',
              },
              value: {
                color: '#fff',
                fontSize: '30px',
                show: true,
              },
            },
          },
        },
        fill: {
          type: 'gradient',
          gradient: {
            shade: 'dark',
            type: 'vertical',
            colorStops: [
              {
                offset: 0,
                color: '#87f0fc',
                opacity: 1,
              },
              {
                offset: 60,
                color: '#bf78fb',
                opacity: 1,
              },
              {
                offset: 100,
                color: '#fa73c6',
                opacity: 1,
              },
            ],
          },
        },
        stroke: {
          lineCap: 'round',
        },
        labels: [label],
      };
    },

  },
};
</script>

<style scoped>
h2 {
  word-break: break-word;
}
</style>
