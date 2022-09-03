<template>
  <v-expansion-panel-header
    class="text-h6 hover"
  >
    <v-row align="center">
      <v-col cols="auto">
        <v-simple-checkbox
          v-model="isChecked"
          class="ma-0 mt-1"
          :ripple="false"
          @input="updatingCheckbox"
        />
      </v-col>
      <v-col class="ml-0 pl-0" cols="10" md="8">
         <div class="emailText">
           <span class="ma-0 dateAndFrom">
             {{ emailThread.formattedDate }}  - {{ fromName(emailThread) }}
           </span>
           <span class="ml-2 subject">{{ emailThread.subject }}</span>
          </div>
      </v-col>
      <v-col cols="12" md="3">
        <span
          v-for="label in filterLabels"
          :key="label"
          :class="{
            'label': true,
            'float-end': !isMobile
          }"
        >
           <v-icon
             size="20"
             class="headerLabelIcon ml-1"
             :color="getLabelColor(label)"
           >
             mdi-label
           </v-icon>
          {{label}}
        </span>
      </v-col>
    </v-row>

  </v-expansion-panel-header>
</template>
<script>
import screenSizeMixin from '@/mixins/screenSizeMixin';
import { mapGetters } from 'vuex';

export default {
  name: 'emailHeader',
  mixins: [screenSizeMixin],

  props: {
    emailThread: Object,
  },

  data() {
    return {
      isChecked: false,
      labels: [],
    };
  },

  computed: {
    ...mapGetters(['getThreadLabels', 'getLabelByName']),

    filterLabels() {
      const removedCategory = this.labels.filter((label) => !label.includes('CATEGORY'));
      const removedInbox = removedCategory.filter((label) => label !== 'INBOX');
      return removedInbox
        .map((label) => label.toLowerCase());
    },
  },

  methods: {
    fromName(emailThread) {
      let participants = this.getUsernameOrEmailAddressFromString(emailThread.from);
      if (emailThread.numberOfParticipants > 2) {
        participants += ` - ${emailThread.numberOfParticipants - 1}`;
      }
      return participants;
    },

    getUsernameOrEmailAddressFromString(emailAddress) {
      const startEmail = emailAddress.indexOf('<');
      if (startEmail !== -1) {
        const charsToRemove = emailAddress.length - startEmail;
        return emailAddress.slice(0, -charsToRemove);
      }

      return emailAddress;
    },

    updateLabels() {
      this.labels = this.getThreadLabels(this.emailThread.id);
    },

    deselect() {
      this.isChecked = false;
    },

    updatingCheckbox() {
      if (this.isChecked) {
        this.$emit('selected', this.emailThread.id);
      } else {
        this.$emit('deselected', this.emailThread.id);
      }
    },

    getLabelColor(label) {
      const storedLabel = this.getLabelByName(label);
      if (storedLabel) {
        return storedLabel.color;
      }
      return '#FFF';
    },
  },

  created() {
    this.updateLabels();
  },
};
</script>
<style scoped>

.headerLabelIcon {
  transform: rotate(90deg);
}

.hover:hover {
  filter: brightness(150%);
}

.emailText {
  text-align: start;
}

.subject {
  display: inline-block;
  border-left: 2px solid var(--v-secondary-base) !important;
  padding-left: 10px;
  color: var(--v-info-darken2) !important;
  text-overflow: ellipsis!important;
  white-space: nowrap;
}

.label {
  color: var(--v-info-darken3) !important;
  font-size: 0.9em;
}

.dateAndFrom {
  display: inline-block;
  font-size: 0.9em !important;
}

.subject {
  font-size: 0.8em !important;
}

.label {
  font-size: 0.8em !important;
}

@media only screen and (max-width: 1264px) {
  .emailText {
    width: 50%;
    display: block;
    text-align: start;
    text-overflow: ellipsis!important;
    white-space: nowrap;
    overflow: hidden;
  }

  .dateAndFrom {
    font-size: 0.8em !important;
    display: block;
  }

  .subject {
    font-size: 0.9em !important;
    display: block;
  }

  .label {
    font-size: 0.7em !important;
  }

}

</style>
