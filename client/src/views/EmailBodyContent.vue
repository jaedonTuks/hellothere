<template>
  <v-expansion-panel-content>
    <template class="emailBody">
      <div  v-html="email.body" />
      <style></style>
    </template>
    <v-textarea
        outlined
        v-model="reply"
        class="mt-5"
        name="input-7-4"
        label="Reply"
        append-icon="mdi-send"
        @click:append="sendReply"
        @keyup.ctrl.enter="sendReply"
    ></v-textarea>
  </v-expansion-panel-content>
</template>
<script>
export default {
  name: 'employeeBodyContent',
  props: {
    email: {},
    reply: {},
  },

  methods: {
    sendReply(email) {
      console.log(`replying to email ${email.id}`);
    },
  },
};
</script>
