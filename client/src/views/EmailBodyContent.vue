<template>
  <v-expansion-panel-content>
    <v-container class="emailBody">
      <v-row
        v-for="email in emailThread.emails"
        :class="{
          'mt-5': true
        }"
        :justify="getJustify(email)"
        :key="`${emailThread.id} - ${email.id}`"
      >
        <v-col cols="5" v-html="email.body"/>
      </v-row>
    </v-container>
    <v-textarea
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
