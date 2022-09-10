<template>
  <v-col :cols="cols" :md="md">
        <span
            v-for="label in filterLabels"
            :key="label"
            :class="{
            'label': true,
            'largerLabels': largerLabels,
            'float-end': !floatLeft && !isMobile
          }"
        >
           <v-icon
               class="headerLabelIcon ml-1"
               :size="largerLabels ? 25 : 20"
               :color="getLabelColor(label)"
           >
             mdi-label
           </v-icon>
          {{ idMode ? getLabelNameById(label) : label}}
        </span>
  </v-col>
</template>
<script>
import screenSizeMixin from '@/mixins/screenSizeMixin';
import { mapGetters } from 'vuex';

export default {
  name: 'LabelsList',
  mixins: [screenSizeMixin],
  props: {
    filterLabels: {},
    largerLabels: Boolean,
    cols: String,
    md: String,
    floatLeft: {
      type: Boolean,
      default: false,
    },
    idMode: {
      type: Boolean,
      default: false,
    },
  },

  computed: {
    ...mapGetters(['getLabelByName', 'getLabelById']),
  },

  methods: {
    getLabelNameById(id) {
      const label = this.getLabelById(id);
      if (label && label.name) {
        return label.name;
      }
      return id;
    },

    getLabelColor(value) {
      const storedLabel = this.idMode ? this.getLabelById(value) : this.getLabelByName(value);
      if (storedLabel) {
        return storedLabel.color;
      }
      return '#FFF';
    },
  },
};
</script>
<style scoped>

.headerLabelIcon {
  transform: rotate(90deg);
}

.label {
  color: var(--v-info-darken3) !important;
  font-size: 1em !important;
}

.largerLabels {
  font-size: 1.5em!important;
  margin-right: 10px;
}

@media only screen and (max-width: 1264px) {

  .label {
    font-size: 0.7em !important;
  }

  .largerLabels {
    font-size: 1em !important;
    margin-right: 10px;
  }

}

</style>
