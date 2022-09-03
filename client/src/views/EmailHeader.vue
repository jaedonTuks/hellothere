<template>
    <v-row
      class="pl-4 pr-4 hover headerRow"
      align="center"
    >
      <v-col class="ml-0 dateAndFrom" cols="4" md="3">
          <v-simple-checkbox
            v-model="isChecked"
            class="ma-0 pa-1 mt-1 text-h6 checkbox"
            :ripple="false"
            @input="updatingCheckbox"
          />
        <span v-if="!isMobile">{{ emailThread.formattedDate }}-</span>  {{ fromName(emailThread) }}
      </v-col>
      <v-col cols="8" md="6" class="subject">
        {{ emailThread.subject }}
      </v-col>
      <LabelsList
        v-if="!isMobile"
        cols="12"
        md="3"
        :filter-labels="filterLabels"
      />
    </v-row>
</template>
<script>
import screenSizeMixin from '@/mixins/screenSizeMixin';
import { mapGetters } from 'vuex';
import LabelsList from '@/views/LabelsList.vue';

export default {
  name: 'emailHeader',
  components: { LabelsList },
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
    ...mapGetters(['getThreadLabels']),

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
  },

  created() {
    this.updateLabels();
  },
};
</script>
<style scoped>
.checkbox {
  display: inline-block;
}

.headerRow:hover {
  filter: brightness(150%);
  cursor: pointer;
}

.subject::before {
  content:'';
  height: 110%;
  margin-right: 10px;
  border-left: 2px solid var(--v-secondary-base) !important;
}

.subject {
  display: inline-block;
  color: var(--v-info-darken2) !important;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 1.2em !important;
  text-align: start;
}

.dateAndFrom {
  display: inline-block;
  white-space: nowrap;
  font-size: 1.2em !important;
  text-align: start;
  text-overflow: ellipsis;
  overflow: hidden;
}

@media only screen and (max-width: 1264px) {
  .dateAndFrom {
    font-size: 0.8em !important;
  }

  .subject {
    font-size: 0.9em !important;
  }

  .headerRow {
    overflow: hidden;
  }
}

</style>
