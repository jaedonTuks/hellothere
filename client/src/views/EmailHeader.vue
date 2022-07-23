<template>
  <v-expansion-panel-header class="text-h6">
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
      {{ filterLabels(emailThread.labelIds) }}
    </span>
  </v-expansion-panel-header>
</template>
<script>
import screenSizeMixin from '@/mixins/screenSizeMixin';

export default {
  name: 'emailHeader',
  mixins: [screenSizeMixin],

  props: {
    emailThread: Object,
  },

  methods: {
    fromName(emailFrom) {
      const startEmail = emailFrom.indexOf('<');
      const charsToRemove = emailFrom.length - startEmail;

      return emailFrom.slice(0, -charsToRemove);
    },

    filterLabels(labelIds) {
      const removedCategory = labelIds.filter((label) => !label.includes('CATEGORY'));
      const removedInbox = removedCategory.filter((label) => label !== 'INBOX');
      return removedInbox
        .map((label) => label.toLowerCase())
        .join(',');
    },
  },
};
</script>
<style scoped>
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
    font-size: 0.8em!important;
  }
  .subject {
    font-size: 0.9em!important;
  }

  .label {
    font-size: 0.5em!important;
  }

}

</style>
