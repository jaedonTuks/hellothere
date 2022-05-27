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
              @keyup.enter="search"
            />
          </v-col>
      </v-row>
      <v-card
        v-for="email in Object.values(emails)"
        class="mb-5"
        color="accent"
        :key="email.id"
      >
        <v-card-text class="text-h6">
          <span class="date">
            {{ email.formattedDate }}
          </span>
          <span class="from mr-0"> - {{ fromName(email.from) }} </span>
          <span class="ml-2 subject">{{ email.subject }}</span>
        </v-card-text>
      </v-card>
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
    };
  },

  methods: {
    ...mapActions(['fetchEmails', 'searchEmails']),
    ...mapGetters(['getEmails']),

    fromName(emailFrom) {
      const startEmail = emailFrom.indexOf('<');
      const charsToRemove = emailFrom.length - startEmail;

      return emailFrom.slice(0, -charsToRemove);
    },

    search() {
      this.searchingEmails = true;
      this.searchEmails(this.searchString)
        .then(() => {
          this.emails = this.getEmails();
        })
        .finally(() => {
          this.searchingEmails = false;
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
</style>
