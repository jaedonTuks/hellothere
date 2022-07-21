<template>
  <v-expansion-panel-content>
    <v-progress-linear
      v-show="loading"
      indeterminate
      color="purpleAccent"
    ></v-progress-linear>
    <v-container v-show="!loading" class="emailBody">
      <v-row
        v-for="email in emailThread.emails"
        :class="{
          'mt-5': true
        }"
        :justify="getJustify(email)"
        :key="`${emailThread.id} - ${email.id}`"
      >
        <div style="overflow-x: scroll" v-if="$vuetify.breakpoint.mobile" v-html="email.body"/>
        <v-col
          v-else
          cols="5"
          :class="{
            'pa-6': true,
            'emailMessage': true,
            'ownMessage': isOwnEmail(email),
            'noReply': isNoReplyEmail(email)
          }"
        >
          <v-row style="margin-bottom: 1px;">
            <h4>{{email.from}}</h4>
          </v-row>
          <v-row v-html="email.body"/>
        </v-col>
      </v-row>
    </v-container>
    <v-textarea
        v-if="!isNoReplyEmail(emailThread.emails[0])"
        outlined
        v-model="reply"
        class="mt-5"
        name="input-7-4"
        label="Reply"
        append-icon="mdi-send"
        :disabled = "sendingReply || isNoReplyEmail(emailThread.emails[0])"
        @click:append="sendReply"
        @keyup.ctrl.enter="sendReply"
    />
  </v-expansion-panel-content>
</template>
<script>
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'employeeBodyContent',
  props: {
    emailThread: {},
    ownUserName: String,
    loading: Boolean,
  },

  data() {
    return {
      sendingReply: false,
      reply: '',
    };
  },

  computed: {
    ...mapGetters(['getProfile', 'getEmailThread']),
  },

  methods: {
    ...mapActions(['replyToEmail']),

    sendReply() {
      this.sendingReply = true;
      const payload = {
        threadId: this.emailThread.id,
        reply: this.reply,
      };

      this.replyToEmail(payload)
        .finally(() => {
          this.sendingReply = false;
        });
    },

    isOwnEmail(email) {
      if (this.ownUserName && this.ownUserName !== '') {
        const emailFrom = email.from;
        return emailFrom.includes(this.ownUserName);
      }
      return false;
    },

    isNoReplyEmail(email) {
      return email && email.from && email.from.includes('no') && email.from.includes('reply');
    },

    getJustify(email) {
      if (this.isNoReplyEmail(email)) {
        return 'center';
      }
      return this.isOwnEmail(email) ? 'end' : 'start';
    },
  },

};
</script>

<style scoped>

.emailMessage {
  background-color: var(--v-accent-darken1);
  border-radius: 10px 10px 10px 0;
}

.ownMessage {
  border-radius: 10px 10px 0 10px!important;
}

.noReply {
  background-color: var(--v-accent-base);
}

</style>
