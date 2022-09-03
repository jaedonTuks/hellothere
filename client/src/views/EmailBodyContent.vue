<template>
  <v-expansion-panel-content>
    <v-progress-linear
      v-show="loading"
      indeterminate
      color="purpleAccent"
    />
    <v-container v-show="!loading" class="emailBody">
      <v-row
        v-for="email in emailThread.emails"
        justify="center"
        class="mb-2"
        :key="`${emailThread.id} - ${email.id}`"
      >
        <v-col
          cols="12"
          :class="{
            'pa-3': true,
            'emailMessage': true,
            'ownMessage': isOwnEmail(email),
            'noReply': isNoReplyEmail(email)
          }"
        >
          <v-row class="borderBottom">
            <v-col cols="12" md="6"><h4>From: {{email.from}}</h4></v-col>
            <v-col cols="12" md="6">
              <span :class="{
                  'float-right': !isMobile
                }"
              >
                {{email.fullDateTime}}
              </span>
            </v-col>
            <v-col cols="12"> To: {{ email.to.join(", ") }} </v-col>
            <v-col v-if="email.cc.length > 0" cols="12"> CC: {{ email.cc.join(", ") }} </v-col>
          </v-row>
          <v-row class="pa-3 emailHtml" v-html="email.body"/>
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
        :loading="sendingReply"
        :disabled = "sendingReply || isNoReplyEmail(emailThread.emails[0])"
        @click:append="sendReply"
        @keyup.ctrl.enter="sendReply"
    />
  </v-expansion-panel-content>
</template>
<script>
import { mapActions, mapGetters } from 'vuex';
import screenSizeMixin from '@/mixins/screenSizeMixin';

export default {
  name: 'employeeBodyContent',
  mixins: [screenSizeMixin],

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
          this.reply = '';
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
  border-radius: 5px;
}

.ownMessage {
  border-radius: 10px 10px 0 10px!important;
}

.noReply {
  background-color: var(--v-accent-base);
}

@media only screen and (max-width: 1264px) {
  .emailHtml {
      overflow: auto;
  }
}

</style>
