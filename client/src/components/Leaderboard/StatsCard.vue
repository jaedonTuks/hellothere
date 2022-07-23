<template>
  <v-col  cols="12" lg="4" class="mt-5">
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
    chartOptions: Object,
    series: Array,
  },
};
</script>
