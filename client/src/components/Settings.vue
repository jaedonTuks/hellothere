<template>
  <v-row class="mt-4 pa-3">
    <v-col cols="12">
      <v-row
        align="center"
        justify="center"
      >
        <v-col
          v-for="(label, index) in labels"
          class="ma-2 pa-3"
          cols="2"
          :key="`label-${index}`"
        >
          <v-card
            class="pa-3"
            color="accent darken-1"
          >
            <v-card-title class="borderBottom">
              <v-icon :color="label.color" class="mr-3">mdi-label</v-icon>
              {{ label.name }}
            </v-card-title>
            <v-card-text>
              <v-checkbox
                v-model="label.isViewable"
                class="mr-2"
                label="Is Quick Filter Visible"
                color="secondary"
                @change="updateViewable(label)"
              />
              <v-expansion-panels>
                <v-expansion-panel>
                  <v-expansion-panel-header>Manage Color</v-expansion-panel-header>
                  <v-expansion-panel-content>
                    <v-color-picker
                      v-model="colors[index]"
                      hide-mode-switch
                      hide-inputs
                      hide-canvas
                      mode="hexa"
                    />
                    <v-btn
                      width="100%"
                      class="mt-2"
                      color="secondary"
                      :loading="loading[index]"
                      @click="updateColor(index)"
                    >
                      Save Label Color
                    </v-btn>
                  </v-expansion-panel-content>
                </v-expansion-panel>
              </v-expansion-panels>
            </v-card-text>
          </v-card>
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
      loading: [],
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
