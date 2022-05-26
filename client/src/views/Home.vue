<template>
  <v-row align="center" justify="center">
    <v-col>
      <h1>Home page</h1>
      <p>{{emails}}</p>
    </v-col>
  </v-row>
</template>

<script>
import { mapActions } from 'vuex';

export default {
  name: 'Home',
  data() {
    return {
      emails: [],
    };
  },

  methods: {
    ...mapActions(['fetchEmails']),
  },

  created() {
    this.fetchEmails().then(
      (emails) => {
        console.log(emails);
        this.emails = emails.data;
      },
    );
  },
};
</script>
