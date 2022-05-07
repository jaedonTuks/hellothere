<template>
  <v-row align="center" justify="center">
    <v-col>
      <h1>{{ test }}</h1>
    </v-col>
  </v-row>
</template>

<script>
// @ is an alias to /src
import { mapActions, mapState } from 'vuex';

export default {
  name: 'Home',
  components: {},

  computed: {
    ...mapState({
      test: (state) => state.test,
    }),
  },

  methods: {
    ...mapActions(['fetchTest']),
  },

  created() {
    this.fetchTest();
  },

};
</script>
