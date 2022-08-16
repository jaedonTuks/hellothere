<template>
  <v-row class="mt-4 pa-3">
    <v-col cols="12">
      <h2 class="mt-5">Label Settings</h2>
    </v-col>
    <v-col cols="12">
      <v-row
        align="center"
        justify="center"
      >
        <v-col
          v-for="(label, index) in labels"
          class="ma-2 pa-3 borderTop"
          cols="2"
          :key="`label-${index}`"
        >
          <v-row>
            <v-col cols="12">
              <v-icon :color="label.color">mdi-label</v-icon>
              {{ label.name }}
            </v-col>
            <v-col cols="12">
              <v-checkbox
                v-model="label.isViewable"
                class="mr-2"
                label="Is Quick Filter Visible"
                @change="updateViewable(label)"
              />
              <v-color-picker
                v-model="colors[index]"
                hide-mode-switch
                mode="hexa"
              />
              <v-btn
                width="100%"
                class="mt-2"
                @click="updateColor(index)"
              >
                Save Label Color
              </v-btn>
            </v-col>
          </v-row>
        </v-col>
      </v-row>

    </v-col>

  </v-row>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'ChallengesOverview',

  data() {
    return {
      loadingLabels: true,
      labels: [],
      colors: [],
    };
  },

  computed: {
    ...mapGetters(['getLabels']),
  },

  methods: {
    ...mapActions(['fetchLabels', 'updateLabelVisibility', 'updateLabelColor']),

    updateViewable(label) {
      const payload = {
        labelId: label.id,
        isViewable: label.isViewable,
      };

      this.updateLabelVisibility(payload);
    },

    updateColor(index) {
      const label = this.labels[index];
      const color = this.colors[index];
      if (label && color) {
        label.color = color;
        const payload = {
          labelId: label.id,
          color: label.color.slice(0, -2),
        };
        this.updateLabelColor(payload);
      }
    },
  },

  created() {
    this.fetchLabels().finally(() => {
      this.labels = this.getLabels();
      this.colors = this.labels.map((label) => label.color);
      this.loadingLabels = false;
    });
  },
};
</script>

<style scoped>

</style>
