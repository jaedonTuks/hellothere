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
            label="Filter by labels"
            :items="labels"
            :loading="filteringEmails"
            :disabled="filteringEmails || searchingEmails"
            @change="search(false)"
          />
        </v-col>
      </v-row>
      <v-toolbar
        short
        outlined
        class="mb-1 toolbar"
        color="background"
        elevation="0"
      >
        <v-row class="mt-4">
          <v-col cols="1">
            <v-checkbox
              v-model="allSelected"
              class="align-center"
              dense
              :label="allSelected? 'Deselect All' : 'Select all'"
              @change="updateSelectAll"
            />
          </v-col>
          <v-col cols="1" class="pt-4 selectedIndicator">
            {{selectedEmailIds.length}} selected
          </v-col>
          <v-slide-x-transition>
            <v-col v-show="selectedEmailIds.length > 0" :cols="isLabelMenuOpen ? 4 : 1">
              <v-menu
                v-model="isLabelMenuOpen"
                bottom
                style="overflow-y: hidden"
                transition="slide-y-transition"
                ref="labelMenu"
                :close-on-content-click="false"
                @click="isLabelMenuOpen=true"
              >
                <template v-slot:activator="{ on, attrs }">
                  <v-btn
                    dark
                    small
                    class="mt-0"
                    color="background"
                    elevation="0"
                    v-bind="attrs"
                    v-on="on"
                  >
                    <v-icon>mdi-label</v-icon>
                  </v-btn>
                </template>
                <v-list
                  style="max-height: 500px; padding: 10px"
                  class="overflow-y-auto backgroundDark"
                >
                  <v-list-item-title class="borderBottom">
                    <v-icon medium class="mr-2">mdi-label</v-icon>
                    Manage label
                  </v-list-item-title>
                  <v-list-item
                    v-for="(label, index) in labels"
                    :key="index"
                  >
                    <v-list-item-action>
                      <v-checkbox
                        v-model="labelCheckboxSelected[index]"
                        @change="newLabelSelected($event, label)"
                      />
                    </v-list-item-action>
                    <v-list-item-title>{{ label }}</v-list-item-title>
                  </v-list-item>
                </v-list>
                <v-row justify="end" class="backgroundDark pa-3">
                  <v-col class="borderTop" cols="12">
                    <v-btn
                      color="secondary"
                      class="float-end"
                      :disabled="addLabels.length === 0"
                      :loading="labelChangeLoading"
                      @click="sendAddLabelsRequest"
                    >
                      Add labels
                    </v-btn>
                  </v-col>
                </v-row>
              </v-menu>
            </v-col>
          </v-slide-x-transition>

          <v-slide-x-transition>
            <v-col v-show="selectedEmailIds.length > 0" :cols="isLabelMenuOpen ? 4 : 1">
              <v-tooltip bottom>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon
                    v-bind="attrs"
                    v-on="on"
                    @click="markAsSpam"
                  >
                    mdi-email-off-outline
                  </v-icon>
                </template>
                <span>Mark as spam</span>
              </v-tooltip>
            </v-col>
          </v-slide-x-transition>
          <v-slide-x-transition>
            <v-col v-show="selectedEmailIds.length > 0" :cols="isLabelMenuOpen ? 4 : 1">
              <v-tooltip bottom>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon
                    v-bind="attrs"
                    v-on="on"
                    @click="markAsTrash"
                  >
                    mdi-delete
                  </v-icon>
                </template>
                <span>Delete</span>
              </v-tooltip>
            </v-col>
          </v-slide-x-transition>
        </v-row>
      </v-toolbar>
      <div class="mb-4 gradiantBorderBottom gradiantBorderBottomFullWidth"/>
      <v-expansion-panels dark class="expansionPanels">
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
            @selected="addSelectedEmail"
            @deselected="removeSelectedEmail"
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
      isLabelMenuOpen: false,
      allSelected: false,
      ownUsername: null,
      labels: [],
      emailThreads: [],
      searchString: '',
      searchingEmails: false,
      filteringEmails: false,
      labelChangeLoading: false,
      reply: '',
      loadingEmailThread: false,
      selectedLabels: [],
      selectedEmailIds: [],
      addLabels: [],
      removeLabels: [],
      labelCheckboxSelected: [],
    };
  },

  computed: {
    ...mapGetters(['getProfile', 'getEmailThread', 'getEmailThreads', 'getLabelNames']),

    toolbarActionsDisabled() {
      return this.selectedEmailIds.length === 0;
    },
  },

  methods: {
    ...mapActions(['fetchUserInfo', 'fetchFullEmail', 'fetchEmails', 'fetchLabels', 'searchEmails', 'updateLabels']),

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

    markAsSpam() {
      this.addLabels = ['SPAM'];
      this.sendAddLabelsRequest();
    },

    markAsTrash() {
      this.addLabels = ['TRASH'];
      this.sendAddLabelsRequest();
    },

    sendAddLabelsRequest() {
      this.labelChangeLoading = true;
      const payload = {
        threadIds: this.selectedEmailIds,
        addLabels: this.addLabels,
        removeLabels: this.removeLabels,
      };

      this.updateLabels(payload)
        .then(() => {
          this.updateAllEmailHeaders();
        }).finally(() => {
          this.resetLabelSelections();
          this.labelChangeLoading = false;
        });
    },

    updateAllEmailHeaders() {
      this.emailThreads.forEach((emailThread) => {
        const refKey = `${emailThread.id}-header`;
        this.$refs[refKey][0].updateLabels();
        this.$refs[refKey][0].deselect();
      });
    },

    resetLabelSelections() {
      this.selectedEmailIds = [];
      this.addLabels = [];
      this.removeLabels = [];
      this.isLabelMenuOpen = false;
      this.labelCheckboxSelected = [];
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

    addSelectedEmail(newId) {
      this.selectedEmailIds.push(newId);
    },

    removeSelectedEmail(oldId) {
      this.selectedEmailIds = this.selectedEmailIds.filter(
        (selectedEmailId) => selectedEmailId !== oldId,
      );
    },

    updateSelectAll() {
      this.selectedEmailIds = [];
      this.emailThreads.forEach((emailThread) => {
        const refKey = `${emailThread.id}-header`;
        this.$refs[refKey][0].isChecked = this.allSelected;
        if (this.allSelected) {
          this.selectedEmailIds.push(emailThread.id);
        }
      });
    },

    newLabelSelected(selected, label) {
      if (selected) {
        this.addLabels.push(label);
      } else {
        this.addLabels = this.addLabels.filter(
          (selectedEmailId) => selectedEmailId !== label,
        );
      }
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

    EventBus.$on('newEmail', () => {
      this.updateEmails();
    });
  },

  beforeDestroy() {
    EventBus.$off('newEmail');
  },
};
</script>

<style scoped>
.selectedIndicator {

}

.leftBorder {
  border-left: 2px solid var(--v-secondary-base) !important;
}

.borderBottom {
  padding-bottom: 5px;
  border-bottom: 1px solid var(--v-secondary-base) !important;
}

.borderTop {
  border-top: 2px solid var(--v-info-darken4) !important;
}

.expansionPanel {
  background-color: var(--v-accent-base) !important;
}

.expansionPanel .v-expansion-panel-header {
  padding-top: 2px !important;
  padding-bottom: 2px !important;
}

.backgroundDark {
  background-color: var(--v-accent-darken2) !important;
}

.v-menu__content {
  overflow-y: hidden !important;
}

.toolbarBottomBorder {
  content: '' !important;
  width: 200px !important;
  height: 20px !important;
  color: red !important;
  display: block !important;
}
</style>
