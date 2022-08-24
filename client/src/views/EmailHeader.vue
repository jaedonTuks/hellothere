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
      <v-col class="ml-0 pl-0" cols="8">
         <span class="emailText">
           <span class="ma-0 dateAndFrom">
             {{ emailThread.formattedDate }}  - {{ fromName(emailThread) }}
           </span>
           <span class="ml-2 subject">{{ emailThread.subject }}</span>
          </span>
      </v-col>
      <v-col cols="3">
        <span
          v-for="label in filterLabels"
          :key="label"
          class="float-end label"
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
  padding-left: 10px
}

.subject {
  color: var(--v-info-darken2) !important;
}

.label {
  color: var(--v-info-darken3) !important;
  font-size: 0.9em;
}

.dateAndFrom {
  display: inline-block;
  text-overflow: ellipsis;
}

.dateAndFrom {
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
    width: 100%;
  }

  .dateAndFrom {
    font-size: 0.8em !important;
  }

  .subject {
    font-size: 0.9em !important;
  }

  .label {
    font-size: 0.5em !important;
  }

}

</style>
