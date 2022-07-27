<template>
  <v-expansion-panel-header
    class="text-h6 hover"
  >
     <span class="emailText">
         <span class="ma-0 dateAndFrom">
                  {{ emailThread.formattedDate }}  - {{ fromName(emailThread.from) }}
         </span>
         <span class="ml-2 subject">{{ emailThread.subject }}</span>
     </span>
    <span
      v-if="isNotMobile"
      class="float-end label"
    >
      {{ filterLabels }}
    </span>
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
      labels: [],
    };
  },

  computed: {
    ...mapGetters(['getThreadLabels']),

    filterLabels() {
      const removedCategory = this.labels.filter((label) => !label.includes('CATEGORY'));
      const removedInbox = removedCategory.filter((label) => label !== 'INBOX');
      return removedInbox
        .map((label) => label.toLowerCase())
        .join(',');
    },
  },

  methods: {
    fromName(emailFrom) {
      const startEmail = emailFrom.indexOf('<');
      const charsToRemove = emailFrom.length - startEmail;

      return emailFrom.slice(0, -charsToRemove);
    },

    updateLabels() {
      this.labels = this.getThreadLabels(this.emailThread.id);
    },
  },

  created() {
    this.updateLabels();
  },
};
</script>
<style scoped>
.hover:hover {
  filter: brightness(150%);
}

.emailText {
  width: 90%;
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
  color: var(--v-info-darken4) !important;
}

.dateAndFrom {
  display: inline-block;
  text-overflow: ellipsis;
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
