<template>
  <v-expansion-panel-header class="text-h6">
            <span style="width:90%">
              <span class="ma-0 dateAndFrom">
                {{ email.formattedDate }}  - {{ fromName(email.from) }}
              </span>
              <span class="ml-2 subject">{{ email.subject }}</span>
            </span>
    <span class="float-end label">{{ filterLabels(email.labelIds) }}</span>
  </v-expansion-panel-header>
</template>
<script>
export default {
  name: 'emailHeader',
  props: {
    email: Object,
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

</style>