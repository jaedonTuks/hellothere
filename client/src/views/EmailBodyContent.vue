<template>
  <v-container>
    <v-row align="center" class="mb-2">
      <v-col cols="auto">
        <v-btn
          color="background"
          elevation="0"
          :to="{name: 'Inbox'}"
        >
          <v-icon>mdi-arrow-left</v-icon>
        </v-btn>
      </v-col>
      <v-col cols="9" md="8">
        <h1 class="subject">{{ emailThread.subject }}</h1>
      </v-col>
      <LabelsList
        larger-labels
        cols="12"
        md="3"
        :filter-labels="filterLabels"
      />
    </v-row>
    <v-progress-linear
      v-show="loading"
      indeterminate
      color="purpleAccent"
    />
    <v-expand-transition>
      <div v-show="!loading" class="mb-3 gradiantBorderBottom gradiantBorderBottomFullWidth"/>
    </v-expand-transition>
    <v-container v-show="!loading" class="emailBody">
      <div
        v-for="email in fullEmail.emails"
        class="mb-7 pl-5 pr-5"
        :key="`${emailThread.id} - ${email.id}`"
      >
        <v-row
          justify="center"
          class="mb-2 pa-3 emailMessage"
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
              <v-col cols="12" md="6"><h2 class="from">From: {{ email.from }}</h2></v-col>
              <v-col cols="12" md="6">
                <h3 :class="{
                    'float-right': !isMobile
                  }"
                >
                  {{ email.fullDateTime }}
                </h3>
              </v-col>
              <v-col cols="12"><h3> To: {{ email.to.join(", ") }}</h3></v-col>
              <v-col v-if="email.cc.length > 0" cols="12"> CC: {{ email.cc.join(", ") }}</v-col>
            </v-row>
          </v-col>
          <EmailHtml :email="email"/>
          <v-col v-if="email.attachments.length > 0" cols="12">
            <h3 style="font-weight: normal">Attachments</h3>
            <v-chip-group>
              <v-chip
                v-for="attachment in email.attachments"
                :key="attachment.fileName"
                color="secondary"
                @click="downloadAttachment(attachment, email.id)"
              >
                <v-icon class="mr-2" small>mdi-download</v-icon>
                <h3 style="font-weight: normal">{{ attachment.fileName }}</h3>
              </v-chip>
            </v-chip-group>
          </v-col>
        </v-row>
      </div>
    </v-container>
    <v-col cols="9">
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
    <v-form v-model="valid" ref="form" lazy-validation>
      <v-textarea
        v-if="!isNoReplyEmail(emailThread.emails[0])"
        outlined
        persistent-placeholder
        v-model="reply"
        class="mt-5"
        name="input-7-4"
        label="Reply"
        append-icon="mdi-send"
        :loading="sendingReply"
        :disabled="sendingReply || isNoReplyEmail(emailThread.emails[0])"
        :rules="getEmailBodyRules()"
        @click:append="sendReply"
        @keyup.ctrl.enter="sendReply"
      />
    </v-form>
  </v-container>
</template>
<script>
import {
  mapActions, mapGetters, mapMutations, mapState,
} from 'vuex';
import LabelsList from '@/views/LabelsList.vue';
import screenSizeMixin from '@/mixins/screenSizeMixin';
import EmailHtml from '@/views/EmailHtml.vue';
import validationMixin from '@/mixins/validationMixin';

export default {
  name: 'employeeBodyContent',
  components: { EmailHtml, LabelsList },
  mixins: [screenSizeMixin, validationMixin],

  data() {
    return {
      loading: true,
      sendingReply: false,
      ownUserName: '',
      reply: '',
      valid: true,
      attachments: [],
      fullEmail: {
        emails: [],
      },
    };
  },

  computed: {
    ...mapState({ emailThread: 'viewingEmail' }),
    ...mapGetters(['getProfile', 'getEmailThread', 'getThreadLabels']),

    filterLabels() {
      if (!this.emailThread.id) {
        return [];
      }
      const labels = this.getThreadLabels(this.emailThread.id);
      const removedCategory = labels.filter((label) => !label.includes('CATEGORY'));
      const removedInbox = removedCategory.filter((label) => label !== 'INBOX');
      return removedInbox
        .map((label) => label.toLowerCase());
    },
  },

  methods: {
    ...mapMutations(['setViewingEmail']),
    ...mapActions(['replyToEmail', 'fetchFullEmail', 'fetchAndDownloadAttachment']),

    downloadAttachment(attachment, emailId) {
      this.fetchAndDownloadAttachment({ id: attachment.id, name: attachment.fileName, emailId });
    },

    sendReply() {
      if (this.$refs.form.validate()) {
        this.sendingReply = true;

        const formDataPayload = new FormData();
        formDataPayload.append('threadId', this.emailThread.id);
        formDataPayload.append('reply', this.reply);

        this.attachments.forEach((attachment) => {
          formDataPayload.append('attachments', attachment, attachment.name);
        });

        this.replyToEmail(formDataPayload)
          .then(() => {
            this.fullEmail = this.getEmailThread(this.emailThread.id);
            this.reply = '';
            this.attachments = [];
            this.$refs.form.resetValidation();
          })
          .finally(() => {
            this.sendingReply = false;
          });
      }
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

    getFullEmailThread() {
      if (this.emailThread.emails.every((email) => email.body != null)) {
        return;
      }

      this.loading = true;
      this.fetchFullEmail(this.emailThread.id)
        .then(() => {
          this.fullEmail = this.getEmailThread(this.emailThread.id);
        })
        .finally(() => {
          this.loading = false;
        });
    },
  },

  created() {
    if (!this.emailThread.id) {
      this.$router.push({ name: 'Inbox' });
    } else {
      this.getFullEmailThread();
    }
  },

  beforeDestroy() {
    this.setViewingEmail({});
  },
};
</script>

<style scoped>
html {
  margin: 0 !important;
}

.emailMessage {
  background-color: var(--v-accent-darken1);
  border-radius: 5px;
}

.ownMessage {
  border-radius: 10px 10px 0 10px !important;
}

.noReply {
  background-color: var(--v-accent-base);
}

@media only screen and (max-width: 1264px) {

  h1 {
    font-size: 1.3em;
  }

  h2 {
    font-size: 1.2em;
  }
}

.testIframeClass {
  color: green !important;
}
</style>
