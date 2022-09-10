<template>
  <v-dialog
    v-model="composingEmail"
    fullscreen
    class="overflow-y-hidden"
    transition="dialog-top-transition"
  >
    <v-toolbar color="accent">
      <v-btn
        icon
        dark
        @click="setComposingEmail(false)"
      >
        <v-icon>mdi-close</v-icon>
      </v-btn>
      <v-toolbar-title>
        <div class="d-flex align-center">
          <h1
            v-if="isNotMobile"
            class="logo"
            @click="$router.push({name: 'Inbox'})"
          >
            Hello There!
          </h1>
          <h3 class="ml-lg-8 ml-0 pl-4 borderLeft">Compose Email</h3>
        </div>
      </v-toolbar-title>
    </v-toolbar>
    <v-card
      color="background"
      class="overflow-x-hidden"
    >
      <v-row justify="center">
        <v-col class="emailForm" cols="11" md="6">
          <v-form
            ref="form"
            v-model="valid"
            lazy-validation
          >
            <v-row class="pa-5 pb-0" justify="center">
              <v-col class="mt-4" cols="12">
                <v-combobox
                  v-model="to"
                  dark
                  dense
                  chips
                  deletable-chips
                  multiple
                  persistent-placeholder
                  label="To"
                  prepend-icon="mdi-account"
                  append-icon=""
                  :rules="[(v => !!v.length > 0 || 'To is required'), getEmailRule()]"
                />
              </v-col>
            </v-row>
            <v-row class="pa-5 pt-0 mt-0" justify="center">
              <v-col class="mt-0" cols="12">
                <v-combobox
                  v-model="cc"
                  dark
                  dense
                  chips
                  deletable-chips
                  multiple
                  persistent-placeholder
                  label="CC"
                  prepend-icon="mdi-account"
                  append-icon=""
                  :rules="[getEmailRule()]"
                />
              </v-col>
            </v-row>
            <div
              class="mt-0 mt-lg-5 mt-md-0
                gradiantBorderBottom gradiantBorderBottomFullWidth"
            />
            <v-row
              class="mb-0 pt-0 mt-0"
              align="center"
              align-content="center"
            >
              <v-col
                lg="1"
                cols="3"
                :class="{
                  'mt-7': attachments.length <1
              }"
              >
                <LabelMenu
                  hide-buttons
                  ref="labelMenu"
                  style="display: inline-block"
                  button-background="background darken-1"
                  :manageable-labels="labels"
                  :selected-labels="selectedLabels"
                  @change="newLabelSelected"
                />
              </v-col>
              <v-col :cols="(attachments.length < 1) ? 1 : 9">
                <v-file-input
                  v-model="attachments"
                  class="mb-0"
                  counter
                  multiple
                  show-size
                  small-chips
                  full-width
                  :hide-input="attachments.length < 1"
                  :rules="getFileValidationRules()"
                />
              </v-col>
              <v-expand-x-transition>
                <v-col v-if="selectedLabels.length > 0" cols="12">
                  <LabelsList
                    id-mode
                    cols="12"
                    md="12"
                    :filter-labels="selectedLabels"
                  />
                </v-col>
              </v-expand-x-transition>
            </v-row>
            <v-row class="pt-0 mt-0 pb-0" justify="start">
              <v-col class="mt-3" cols="10">
                <v-text-field
                  dark
                  dense
                  persistent-placeholder
                  v-model="subject"
                  label="Subject"
                  :rules="[(v) => !!v || 'Subject is required']"
                />
              </v-col>
            </v-row>
            <v-row class="mt-0 pt-0">
              <v-col cols="12">
                <v-textarea
                  v-model="message"
                  outlined
                  auto-grow
                  persistent-placeholder
                  class="mt-5"
                  name="input-7-4"
                  label="Message"
                  placeholder="Dear ..."
                  append-icon="mdi-send"
                  :disabled="loading"
                  :loading="loading"
                  :rules="getEmailBodyRules()"
                  @click:append="sendComposedEmail"
                  @keyup.ctrl.enter="sendComposedEmail"
                />
              </v-col>
            </v-row>
          </v-form>
        </v-col>
      </v-row>
    </v-card>
  </v-dialog>
</template>

<script>
import {
  mapActions, mapGetters, mapMutations, mapState,
} from 'vuex';
import { EventBus } from '@/main';

import LabelMenu from '@/views/LabelMenu.vue';
import LabelsList from '@/views/LabelsList.vue';
import ScreenSizeMixin from '@/mixins/screenSizeMixin';
import validationMixin from '@/mixins/validationMixin';

export default {
  name: 'ComposeEmailDialog',
  mixins: [ScreenSizeMixin, validationMixin],

  components: {
    LabelMenu,
    LabelsList,
  },

  data() {
    return {
      valid: true,
      attachments: [],
      loading: false,
      labels: [],
      to: [],
      cc: [],
      selectedLabels: [],
      subject: '',
      message: '',
    };
  },

  computed: {
    ...mapState(['composingEmail']),
    ...mapGetters(['getLabels']),
  },

  methods: {
    ...mapMutations(['setComposingEmail']),
    ...mapActions(['sendEmail', 'fetchLabels']),

    resetFields() {
      this.to = [];
      this.cc = [];
      this.selectedLabels = [];
      this.subject = '';
      this.message = '';
    },

    sendComposedEmail() {
      if (this.$refs.form.validate()) {
        this.loading = true;
        const formDataPayload = new FormData();

        formDataPayload.append('to', this.to);
        formDataPayload.append('cc', this.cc);
        formDataPayload.append('labels', this.selectedLabels);
        formDataPayload.append('subject', this.subject);
        formDataPayload.append('body', this.message);

        this.attachments.forEach((attachment) => {
          formDataPayload.append('attachments', attachment, attachment.name);
        });

        this.sendEmail(formDataPayload)
          .then(() => {
            this.resetFields();
            EventBus.$emit('newEmail');
            this.setComposingEmail(false);
          }).finally(() => {
            this.loading = false;
          });
      }
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
  },

  created() {
    this.labels = this.getLabels();
    if (this.labels.length === 0) {
      this.fetchLabels().then(() => {
        this.labels = this.getLabels();
      });
    }
  },

};
</script>

<style scoped>
.emailForm {
  background: var(--v-background-darken1);
  border-radius: 10px;
  margin: 8em auto;
}
</style>
