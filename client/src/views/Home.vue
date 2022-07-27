<template>
  <v-row align="center" justify="center">
    <ComposeEmailDialog/>
    <v-col style="margin-bottom: 60px;" class="mt-5">
      <v-row align-content="start">
        <v-col
          class="pb-0 pt-0 pa-lg-4"
          cols="12"
          lg="6"
        >
          <v-text-field
            dark
            dense
            v-model="searchString"
            label="Search"
            append-icon="mdi-magnify"
            :loading="searchingEmails"
            :disabled="filteringEmails || searchingEmails"
            @keyup.enter="search(true)"
          />
        </v-col>
        <v-col
          class="mt-1 pb-0 pt-0 pa-lg-4"
          cols="12"
          lg="6"
        >
          <v-autocomplete
            v-model="selectedLabels"
            dark
            dense
            chips
            deletable-chips
            multiple
            height="20px"
            append-icon="mdi-filter"
            label="Labels"
            :items="labels"
            :loading="filteringEmails"
            :disabled="filteringEmails || searchingEmails"
            @change="search(false)"
          />
        </v-col>
      </v-row>
      <v-expansion-panels dark>
        <v-expansion-panel
          v-for="emailThread in emailThreads"
          class="expansionPanel"
          color="accent"
          :key="emailThread.id"
          @change="getFullEmailThread(emailThread)"
        >
          <emailHeader
            :ref="`${emailThread.id}-header`"
            :emailThread="emailThread"
          />
          <employeeBodyContent
            :emailThread="emailThread"
            :own-user-name="ownUsername"
            :loading="loadingEmailThread"
          />
        </v-expansion-panel>
      </v-expansion-panels>
    </v-col>
    <SendActionButton/>
  </v-row>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import loadingMixin from '@/mixins/loadingMixin';
import EmployeeBodyContent from '@/views/EmailBodyContent.vue';
import SendActionButton from '@/components/SendActionButton.vue';
import EmailHeader from '@/views/EmailHeader.vue';
import ComposeEmailDialog from '@/components/ComposeEmailDialog.vue';
// eslint-disable-next-line import/no-cycle
import { EventBus } from '@/main';

export default {
  name: 'Home',
  components: {
    EmailHeader, EmployeeBodyContent, SendActionButton, ComposeEmailDialog,
  },
  mixins: [loadingMixin],

  data() {
    return {
      ownUsername: null,
      labels: [],
      emailThreads: [],
      searchString: '',
      searchingEmails: false,
      filteringEmails: false,
      reply: '',
      loadingEmailThread: false,
      selectedLabels: [],
    };
  },

  computed: {
    ...mapGetters(['getProfile', 'getEmailThread', 'getEmailThreads', 'getLabelNames']),
  },

  methods: {
    ...mapActions(['fetchUserInfo', 'fetchFullEmail', 'fetchEmails', 'fetchLabels', 'searchEmails']),

    updateEmails() {
      this.emailThreads = this.getEmailThreads().sort((a, b) => b.instantSent - a.instantSent);
    },

    getFullEmailThread(emailThread) {
      if (emailThread.emails.every((email) => email.body != null)) {
        return;
      }
      this.loadingEmailThread = true;
      this.fetchFullEmail(emailThread.id)
        .then(() => {
          const fullEmailThread = this.getEmailThread(emailThread.id);
          const outdatedEmailThread = this.emailThreads
            .find((searchingEmailThread) => searchingEmailThread.id === emailThread.id);
          outdatedEmailThread.emails = fullEmailThread.emails;
          this.updateEmailLabels(emailThread);
        })
        .finally(() => {
          this.loadingEmailThread = false;
        });
    },

    updateEmailLabels(emailThread) {
      try {
        const refKey = `${emailThread.id}-header`;
        this.$refs[refKey][0].updateLabels();
      } catch (e) {
        console.error(e);
      }
    },

    search(isSearchLoading) {
      this.searchingEmails = isSearchLoading;
      this.filteringEmails = !isSearchLoading;
      const payload = {
        searchString: this.searchString,
        labels: this.selectedLabels.join(','),
      };
      this.searchEmails(payload)
        .then(() => {
          this.emailThreads = this.getEmailThreads();
        })
        .finally(() => {
          this.searchingEmails = false;
          this.filteringEmails = false;
        });
    },
  },

  created() {
    this.setLoading(true);
    this.fetchLabels().then(() => {
      this.labels = this.getLabelNames();
    });
    this.fetchEmails()
      .finally(() => {
        this.emailThreads = this.getEmailThreads();
        this.setLoading(false);
      });

    this.ownUsername = this.getProfile().username;
    if (this.ownUsername === '') {
      this.fetchUserInfo().then(() => {
        this.ownUsername = this.getProfile().username;
      });
    }

    EventBus.$on('newEmail', () => { this.updateEmails(); });
  },

  beforeDestroy() {
    EventBus.$off('newEmail');
  },
};
</script>

<style scoped>

.expansionPanel {
  background-color: var(--v-accent-base) !important;
}

.expansionPanel  .v-expansion-panel-header {
  padding-top: 2px!important;
  padding-bottom: 2px!important;
}

</style>
