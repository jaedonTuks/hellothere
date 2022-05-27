<template>
  <v-row align="center" justify="center">
    <v-col class="mt-5">
      <v-row>
        <v-col cols="6">
          <v-text-field
            v-model="searchString"
            label="Search"
            append-icon="mdi-magnify"
            dark
            :loading="searchingEmails"
            :disabled="filteringEmails || searchingEmails"
            @keyup.enter="search(true)"
          />
        </v-col>
        <v-col cols="6">
          <v-autocomplete
            v-model="labels"
            dark
            chips
            deletable-chips
            dense
            multiple
            :items="filterItems"
            append-icon="mdi-filter"
            :loading="filteringEmails"
            :disabled="filteringEmails || searchingEmails"
            @change="search(false)"
          />
        </v-col>
      </v-row>
      <v-expansion-panels dark>
        <v-expansion-panel
          v-for="email in Object.values(emails)"
          class="mb-5 expansionPanel"
          color="accent"
          :key="email.id"
          @change="getFullEmail(email)"
        >
          <v-expansion-panel-header class="text-h6">
            <span style="width:90%">
              <span class="ma-0 dateAndFrom">
                {{ email.formattedDate }}  - {{ fromName(email.from) }}
              </span>
              <span class="ml-2 subject">{{ email.subject }}</span>
            </span>
            <span class="float-end label">{{ filterLabels(email.labelIds) }}</span>
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            {{
              email.body
            }}

            <v-textarea
              outlined
              v-model="reply"
              class="mt-5"
              name="input-7-4"
              label="Reply"
              append-icon="mdi-send"
              @click:append="sendReply(email)"
              @keyup.ctrl.enter="sendReply(email)"
            ></v-textarea>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-col>
  </v-row>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import loadingMixin from '@/loadingMixin';

export default {
  name: 'Home',

  mixins: [loadingMixin],

  data() {
    return {
      emails: [],
      searchString: '',
      searchingEmails: false,
      filteringEmails: false,
      reply: '',
      filterItems: [
        {
          text: 'Unread',
          value: 'UNREAD',
          disabled: false,
          divider: false,
        },
        {
          text: 'Spam',
          value: 'SPAM',
          disabled: false,
          divider: false,
        },
        {
          text: 'Important',
          value: 'IMPORTANT',
          disabled: false,
          divider: false,
        },
      ],
      labels: [],
    };
  },

  computed: {
    ...mapGetters(['getOneEmail', 'getEmails']),
  },

  methods: {
    ...mapActions(['fetchFullEmail', 'fetchEmails', 'searchEmails']),

    fromName(emailFrom) {
      const startEmail = emailFrom.indexOf('<');
      const charsToRemove = emailFrom.length - startEmail;

      return emailFrom.slice(0, -charsToRemove);
    },

    sendReply(email) {
      console.log('replying to email');
      console.log(email);
    },

    getFullEmail(email) {
      this.fetchFullEmail(email.id)
        .then(() => {
          const fullEmail = this.getOneEmail(email.id);
          const outdatedEmail = this.emails
            .find((searchingEmail) => searchingEmail.id === email.id);

          outdatedEmail.body = fullEmail.body;
        });
    },

    filterLabels(labelIds) {
      const removedCategory = labelIds.filter((label) => !label.includes('CATEGORY'));
      const removedInbox = removedCategory.filter((label) => label !== 'INBOX');
      return removedInbox
        .map((label) => label.toLowerCase())
        .join(',');
    },

    search(isSearchLoading) {
      this.searchingEmails = isSearchLoading;
      this.filteringEmails = !isSearchLoading;
      const payload = {
        searchString: this.searchString,
        labels: this.labels.join(','),
      };
      this.searchEmails(payload)
        .then(() => {
          this.emails = this.getEmails();
        })
        .finally(() => {
          this.searchingEmails = false;
          this.filteringEmails = false;
        });
    },
  },

  created() {
    this.setLoading(true);
    this.fetchEmails()
      .finally(() => {
        this.emails = this.getEmails();
        this.setLoading(false);
      });
  },
};
</script>

<style scoped>
.subject {
  display: inline-block;
  border-left: 2px solid var(--v-secondary-base) !important;
  padding-left: 10px
}

.expansionPanel {
  background-color: var(--v-accent-base) !important;
}

.subject {
  color: var(--v-info-darken2) !important;
}

.label {
  color: var(--v-info-darken4) !important;
}
</style>
