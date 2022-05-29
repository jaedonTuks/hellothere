<template>
  <v-expansion-panel-content>
    <template class="emailBody">
      <div  v-html="email.body" />
    </template>
    <v-textarea
        outlined
        v-model="reply"
        class="mt-5"
        name="input-7-4"
        label="Reply"
        append-icon="mdi-send"
        :disabled = "sendingReply"
        @click:append="sendReply"
        @keyup.ctrl.enter="sendReply"
    />
  </v-expansion-panel-content>
</template>
<script>
import { mapActions } from 'vuex';

export default {
  name: 'employeeBodyContent',
  props: {
    email: {},
  },

  data() {
    return {
      sendingReply: false,
      reply: '',
    };
  },

  methods: {
    ...mapActions(['replyToEmail']),

    sendReply() {
      this.sendingReply = true;
      const payload = {
        threadId: this.email.threadId,
        messageId: this.email.id,
        reply: this.reply,
      };

      console.log(payload);

      this.replyToEmail(payload)
        .finally(() => {
          this.sendingReply = false;
        });
    },
  },
};
</script>
