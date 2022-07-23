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
                v-model="labels"
                dark
                dense
                chips
                deletable-chips
                multiple
                label="Labels"
                prepend-inner-icon="mdi-label"
                append-icon=""
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
              />
            </v-col>
          </v-row>
        </v-col>
      </v-row>
    </v-card>
  </v-dialog>
</template>

<script>
import { mapMutations, mapState } from 'vuex';

export default {
  name: 'ComposeEmailDialog',
  data() {
    return {
      show: true,
      to: [],
      cc: [],
      labels: [],
      subject: '',
      message: '',
    };
  },

  computed: {
    ...mapState(['composingEmail']),
  },

  methods: {
    ...mapMutations(['setComposingEmail']),
  },

  created() {
    console.log(this.composingEmail);
  },
};
</script>

<style scoped>

</style>
