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
          <emailHeader
            :email="email"
          />
          <employeeBodyContent
            :email="email"
          />
        </v-expansion-panel>
      </v-expansion-panels>
    </v-col>
  </v-row>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import loadingMixin from '@/loadingMixin';
import EmployeeBodyContent from '@/views/EmailBodyContent.vue';
import EmailHeader from '@/views/EmailHeader.vue';

export default {
  name: 'Home',
  components: { EmailHeader, EmployeeBodyContent },
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
          text: 'Important',
          value: 'IMPORTANT',
          disabled: false,
          divider: false,
        },
        {
          text: 'Sent',
          value: 'SENT',
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
          text: 'Starred',
          value: 'STARRED',
          disabled: false,
          divider: false,
        },
        {
          text: 'Unread',
          value: 'UNREAD',
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
    sendReply(email) {
      console.log('replying to email');
      console.log(email);
    },

    getFullEmail(email) {
      if (email.body) {
        return;
      }

      this.fetchFullEmail(email.id)
        .then(() => {
          const fullEmail = this.getOneEmail(email.id);
          const outdatedEmail = this.emails
            .find((searchingEmail) => searchingEmail.id === email.id);

          outdatedEmail.body = fullEmail.body;
        });
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

.expansionPanel {
  background-color: var(--v-accent-base) !important;
}

</style>
