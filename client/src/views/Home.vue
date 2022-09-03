<template>
  <v-row align="center" justify="center">
    <ComposeEmailDialog/>
    <v-col style="margin-bottom: 60px;" class="mt-5">
      <v-toolbar
        short
        outlined
        class="mt-1 mb-1 mt-md-0 toolbar"
        color="background"
        elevation="0"
      >
        <v-row class="mt-4" align-content="start">
          <v-col cols="6" md="1">
            <v-checkbox
              v-model="allSelected"
              class="align-center"
              dense
              :label="allSelected? 'Deselect All' : 'Select all'"
              @change="updateSelectAll"
            />
          </v-col>
          <v-col cols="6" md="1" class="pt-4 selectedIndicator">
            {{ selectedEmailIds.length }} selected
          </v-col>
          <v-col cols="12" md="4">
            <v-row style="min-height: 52px;">
              <v-slide-x-transition>
                <v-col v-show="selectedEmailIds.length > 0" cols="4" md="2">
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
                        v-for="(label, index) in manageableLabels"
                        :key="index"
                      >
                        <v-list-item-action>
                          <v-checkbox
                            v-model="labelCheckboxSelected[index]"
                            @change="newLabelSelected($event, label)"
                          />
                        </v-list-item-action>
                        <v-list-item-title :style="{ 'color': label.color }">
                          {{ label.name }}
                        </v-list-item-title>
                      </v-list-item>
                    </v-list>
                    <v-row justify="end" class="backgroundDark pa-3">
                      <v-col class="borderTop" cols="6">
                        <v-btn
                          color="secondary"
                          :disabled="selectedLabels.length === 0"
                          :loading="labelRemoveLoading"
                          @click="sendUpdateLabelsRequest(true)"
                        >
                          Remove labels
                        </v-btn>
                      </v-col>
                      <v-col class="borderTop" cols="6">
                        <v-btn
                          color="secondary"
                          class="float-end"
                          :disabled="selectedLabels.length === 0"
                          :loading="labelAddLoading"
                          @click="sendUpdateLabelsRequest(false)"
                        >
                          Add labels
                        </v-btn>
                      </v-col>
                    </v-row>
                  </v-menu>
                </v-col>
              </v-slide-x-transition>
              <v-slide-x-transition>
                <v-col v-show="selectedEmailIds.length > 0" cols="4" md="2">
                  <v-tooltip bottom>
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn
                        dark
                        small
                        class="mt-0"
                        color="background"
                        elevation="0"
                        v-bind="attrs"
                        v-on="on"
                        :loading="isMarkAsSpamLoading"
                        @click="markAsSpam"
                      >
                        <v-icon>mdi-email-off-outline</v-icon>
                      </v-btn>
                    </template>
                    <span>Mark as spam</span>
                  </v-tooltip>
                </v-col>
              </v-slide-x-transition>
              <v-slide-x-transition>
                <v-col v-show="selectedEmailIds.length > 0" cols="4" md="2">
                  <v-tooltip bottom>
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn
                        dark
                        small
                        class="mt-0"
                        color="background"
                        elevation="0"
                        v-bind="attrs"
                        v-on="on"
                        :loading="isMarkAsTrashLoading"
                        @click="markAsTrash"
                      >
                        <v-icon>mdi-delete</v-icon>
                      </v-btn>
                    </template>
                    <span>Delete</span>
                  </v-tooltip>
                </v-col>
              </v-slide-x-transition>
            </v-row>
          </v-col>
          <v-col
            class="pb-0 pt-0 pa-lg-4"
            cols="12"
            lg="6"
          >
            <v-text-field
              dark
              dense
              clearable
              v-model="searchString"
              label="Search"
              type="text"
              append-icon="mdi-magnify"
              :disabled="isFetchingMoreEmails || searchingEmails"
              @click:clear="clearSearch"
              @keyup.enter="search(true)"
            />
          </v-col>
        </v-row>
      </v-toolbar>
      <div
        class="mt-15 mt-md-0 mb-md-10 mb-md-4 gradiantBorderBottom gradiantBorderBottomFullWidth"
      />
      <v-tabs
        v-if="viewableLabels.length > 0"
        v-model="selectedLabelViewIndex"
        centered
        center-active
        background-color="background"
        class="mb-4 ml-0 pl-0"
        :slider-color="selectedLabelData.color"
        @change="changeLabelView()"
      >
        <v-tab>
          <v-icon class="mr-2">mdi-label</v-icon>
          All
        </v-tab>
        <v-tab
          v-for="label in viewableLabels"
          :key="label.name"
          class="mdi-format-text-wrapping-overflow"
        >
          <v-icon :color="label.color" class="mr-2">mdi-label</v-icon>
          {{ label.name }}
        </v-tab>
      </v-tabs>
      <NoEmailsCard :email-threads="emailThreads" :filtering-emails="isFetchingMoreEmails"/>
      <v-dialog-transition>
        <v-container
          v-show="emailThreads.length > 0"
          dark
        >
          <v-row>
            <v-progress-linear
              v-show="isFetchingMoreEmails || searchingEmails"
              indeterminate
              color="primary"
              class="mb-0"
            />
          </v-row>
          <v-row
            v-for="emailThread in emailThreads"
            class="expansionPanel"
            color="accent"
            :key="emailThread.id"
            @click="navigateToEmail(emailThread)"
          >
            <emailHeader
              :ref="`${emailThread.id}-header`"
              :emailThread="emailThread"
              @selected="addSelectedEmail"
              @deselected="removeSelectedEmail"
            />
          </v-row>
        </v-container>
      </v-dialog-transition>
      <v-row class="mt-5" justify="center">
        <v-btn
          v-show="hasNextPage"
          :loading="isFetchingMoreEmails"
          @click="getNextPage"
        >
          Load more
        </v-btn>
      </v-row>
    </v-col>
    <SendActionButton/>
  </v-row>
</template>

<script>
import {
  mapActions, mapGetters, mapMutations, mapState,
} from 'vuex';
import loadingMixin from '@/mixins/loadingMixin';
import SendActionButton from '@/components/SendActionButton.vue';
import EmailHeader from '@/views/EmailHeader.vue';
import ComposeEmailDialog from '@/components/ComposeEmailDialog.vue';
import NoEmailsCard from '@/components/NoEmailsCard.vue';
// eslint-disable-next-line import/no-cycle
import { EventBus } from '@/main';

export default {
  name: 'Home',
  components: {
    NoEmailsCard,
    EmailHeader,
    SendActionButton,
    ComposeEmailDialog,
  },
  mixins: [loadingMixin],

  data() {
    return {
      isLabelMenuOpen: false,
      allSelected: false,
      isMarkAsSpamLoading: false,
      isMarkAsTrashLoading: false,
      searchingEmails: false,
      isFetchingMoreEmails: false,
      labelAddLoading: false,
      labelRemoveLoading: false,
      loadingEmailThread: false,
      selectedLabelViewIndex: 0,
      ownUsername: null,
      searchString: '',
      reply: '',
      labels: [],
      emailThreads: [],
      selectedLabels: [],
      selectedEmailIds: [],
      labelCheckboxSelected: [],
    };
  },

  computed: {
    ...mapState(['nextPageToken']),
    ...mapGetters(['getProfile', 'getEmailThread', 'getEmailThreads', 'getLabels', 'getLabelNames']),

    hasNextPage() {
      return this.nextPageToken && this.nextPageToken.length > 1;
    },

    isSearchResult() {
      return (this.searchString && this.searchString.length > 1)
        || (this.selectedLabelData.name && this.selectedLabelData.name.length > 1);
    },

    toolbarActionsDisabled() {
      return this.selectedEmailIds.length === 0;
    },

    manageableLabels() {
      return this.labels.filter((label) => label.isManageable);
    },

    viewableLabels() {
      return this.labels.filter((label) => label.isViewable);
    },

    selectedLabelData() {
      if (this.selectedLabelViewIndex === 0) {
        return {
          name: '',
          color: '#FFF',
        };
      }
      const selectedLabelIndex = this.selectedLabelViewIndex - 1;
      return this.viewableLabels[selectedLabelIndex];
    },
  },

  methods: {
    ...mapActions(['fetchUserInfo', 'fetchEmails', 'fetchLabels', 'searchEmails', 'updateLabels']),
    ...mapMutations(['setViewingEmail']),

    // general methods
    updateEmails() {
      this.emailThreads = this.getEmailThreads().sort((a, b) => b.instantSent - a.instantSent);
    },

    navigateToEmail(emailThread) {
      this.setViewingEmail(emailThread);
      this.$router.push({ name: 'Email' });
    },

    // searching and view updates
    search(isSearchLoading) {
      this.searchingEmails = isSearchLoading;
      this.isFetchingMoreEmails = !isSearchLoading;

      const payload = {
        searchString: this.searchString,
        labels: this.selectedLabelData.name,
        pageToken: null,
      };

      this.searchWithPayload(payload);
    },

    searchWithPayload(payload) {
      this.searchEmails(payload)
        .then(() => {
          this.emailThreads = this.getEmailThreads();
        })
        .finally(() => {
          this.searchingEmails = false;
          this.isFetchingMoreEmails = false;
        });
    },

    clearSearch() {
      this.searchString = '';
      this.search(true);
    },

    changeLabelView() {
      this.search(false);
    },

    // email modifications
    updateEmailLabels(emailThread) {
      try {
        const refKey = `${emailThread.id}-header`;
        this.$refs[refKey][0].updateLabels();
      } catch (e) {
        console.error(e);
      }
    },

    markAsSpam() {
      this.isMarkAsSpamLoading = true;
      this.selectedLabels = ['SPAM'];
      this.sendUpdateLabelsRequest();
    },

    markAsTrash() {
      this.isMarkAsTrashLoading = true;
      this.selectedLabels = ['TRASH'];
      this.sendUpdateLabelsRequest();
    },

    sendUpdateLabelsRequest(isRemoving = false) {
      this.labelAddLoading = !isRemoving;
      this.labelRemoveLoading = isRemoving;

      let addLabels = this.selectedLabels;
      let removeLabels = [];
      if (isRemoving) {
        addLabels = [];
        removeLabels = this.selectedLabels;
      }

      const payload = {
        threadIds: this.selectedEmailIds,
        addLabels,
        removeLabels,
      };

      this.updateLabels(payload)
        .then(() => {
          this.updateAllEmailHeaders();
        }).finally(() => {
          this.resetLabelSelections();
          this.labelAddLoading = false;
          this.labelRemoveLoading = false;
          this.isMarkAsSpamLoading = false;
          this.isMarkAsTrashLoading = false;
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
      this.selectedLabels = [];
      this.isLabelMenuOpen = false;
      this.labelCheckboxSelected = [];
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
        this.selectedLabels.push(label.id);
      } else {
        this.selectedLabels = this.selectedLabels.filter(
          (selectedLabelId) => selectedLabelId !== label.id,
        );
      }
    },

    // styling
    getLabelColor(label) {
      return {
        color: `#${label.color}`,
      };
    },

    getNextPage() {
      this.isFetchingMoreEmails = true;
      if (this.isSearchResult) {
        const payload = {
          searchString: this.searchString,
          labels: this.selectedLabelData.name,
          pageToken: this.nextPageToken,
        };

        this.searchWithPayload(payload);
      } else {
        this.fetchEmails(this.nextPageToken)
          .finally(() => {
            this.emailThreads = this.getEmailThreads();
            this.isFetchingMoreEmails = false;
          });
      }
    },
  },

  created() {
    this.setLoading(true);
    this.fetchLabels().then(() => {
      this.labels = this.getLabels();
    });
    this.fetchEmails(null)
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
.v-slide-group__prev v-slide-group__prev--disabled {
  display: none!important;
}
</style>
