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
      <v-toolbar-title>Compose Email</v-toolbar-title>
    </v-toolbar>

    <v-card
      color="background"
      class="overflow-x-hidden"
    >
      <v-row justify="center">
        <v-col cols="6">
          <v-row class="pa-5 pb-0" justify="center">
            <v-col class="mt-4" cols="12">
              <v-combobox
                v-model="to"
                dark
                dense
                chips
                deletable-chips
                multiple
                label="To"
                prepend-icon="mdi-account"
                append-icon=""
              />
            </v-col>
          </v-row>
          <v-row class="pa-5 pt-0 mt-0" justify="center">
            <v-col class="mt-0"  cols="12">
              <v-combobox
                v-model="cc"
                dark
                dense
                chips
                deletable-chips
                multiple
                label="CC"
                prepend-icon="mdi-account"
                append-icon=""
              />
            </v-col>
          </v-row>
          <v-row class="pa-5 pt-0 mt-0" justify="start">
            <v-col class="mt-0"  cols="10">
              <v-text-field
                dark
                dense
                v-model="subject"
                label="Subject"
              />
            </v-col>
          </v-row>
          <v-row class="pa-5 pt-0 mt-0" justify="start">
            <v-col class="mt-0"  cols="6">
              <v-combobox
                v-model="selectedLabels"
                dark
                dense
                chips
                deletable-chips
                multiple
                label="Labels"
                prepend-inner-icon="mdi-label"
                append-icon=""
                :items="labels"
              />
            </v-col>
          </v-row>
          <v-row  class="pa-5 pt-0 mt-4">
            <v-col cols="8">
              <v-file-input
                counter
                multiple
                show-size
                small-chips
                prepend-icon=""
                prepend-inner-icon="mdi-paperclip"
                truncate-length="18"
              ></v-file-input>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <v-textarea
                v-model="message"
                outlined
                class="mt-5"
                name="input-7-4"
                label="Message"
                append-icon="mdi-send"
                @click:append="sendComposedEmail"
                @keyup.ctrl.enter="sendComposedEmail"
              />
            </v-col>
          </v-row>
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

export default {
  name: 'ComposeEmailDialog',
  data() {
    return {
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
    ...mapGetters(['getLabelNames']),
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
      const payload = {
        to: this.to,
        cc: this.cc,
        labels: this.selectedLabels,
        subject: this.subject,
        body: this.message,
      };

      this.sendEmail(payload)
        .then(() => {
          this.resetFields();
          EventBus.$emit('newEmail');
          this.setComposingEmail(false);
        });
    },
  },

  created() {
    // todo verify if this works
    this.labels = this.getLabelNames();
    if (this.labels.length === 0) {
      this.fetchLabels().then(() => {
        this.labels = this.getLabelNames();
      });
    }
  },

};
</script>

<style scoped>

</style>
