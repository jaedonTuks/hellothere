<template>
  <v-menu
    v-model="isLabelMenuOpen"
    bottom
    style="overflow-y: hidden"
    transition="slide-y-transition"
    ref="labelMenu"
    :close-on-content-click="false"
    @click="isLabelMenuOpen=true"
  >
    <template v-slot:activator="{ on, attrs }">
      <v-btn
        dark
        small
        class="mt-0"
        elevation="0"
        :color="buttonBackground"
        v-bind="attrs"
        v-on="on"
      >
        <v-icon>mdi-label</v-icon>
      </v-btn>
    </template>
    <v-list
      style="max-height: 500px; padding: 10px"
      class="overflow-y-auto backgroundDark"
    >
      <v-list-item-title class="borderBottom">
        <v-icon medium class="mr-2">mdi-label</v-icon>
        Manage label
      </v-list-item-title>
      <v-list-item
        v-for="(label, index) in manageableLabels"
        :key="index"
      >
        <v-list-item-action>
          <v-checkbox
            v-model="labelCheckboxSelected[index]"
            @change="newLabelSelected($event, label)"
          />
        </v-list-item-action>
        <v-list-item-title :style="{ 'color': label.color }">
          {{ label.name }}
        </v-list-item-title>
      </v-list-item>
    </v-list>
    <v-row v-if="!hideButtons" justify="end" class="backgroundDark pa-3">
      <v-col class="borderTop" cols="6">
        <v-btn
          color="secondary"
          :disabled="selectedLabels.length === 0"
          :loading="labelRemoveLoading"
          @click="sendUpdateLabelsRequest(true)"
        >
          Remove labels
        </v-btn>
      </v-col>
      <v-col class="borderTop" cols="6">
        <v-btn
          color="secondary"
          class="float-end"
          :disabled="selectedLabels.length === 0"
          :loading="labelAddLoading"
          @click="sendUpdateLabelsRequest(false)"
        >
          Add labels
        </v-btn>
      </v-col>
    </v-row>
  </v-menu>
</template>
<script>
export default {
  name: 'LabelMenu',
  props: {
    labelAddLoading: Boolean,
    labelRemoveLoading: Boolean,
    hideButtons: {
      type: Boolean,
      default: false,
    },
    buttonBackground: {
      type: String,
      default: 'background',
    },
    manageableLabels: Array,
    selectedLabels: Array,
  },

  data() {
    return {
      isLabelMenuOpen: false,
      labelCheckboxSelected: [],
    };
  },

  methods: {
    newLabelSelected(selected, label) {
      this.$emit('change', selected, label);
    },

    sendUpdateLabelsRequest(isRemoving) {
      this.$emit('send-update-request', isRemoving);
    },

    closeMenu() {
      this.isLabelMenuOpen = false;
      this.labelCheckboxSelected = [];
    },
  },
};
</script>
<style scoped>
.borderTop {
  border-top: 2px solid var(--v-info-darken4) !important;
}

.backgroundDark {
  background-color: var(--v-accent-darken2) !important;
}

.v-slide-group__prev v-slide-group__prev--disabled {
  display: none !important;
}

.v-menu__content {
  overflow-y: hidden !important;
}

</style>
