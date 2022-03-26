<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png">
    <div>
      <h1>{{test}}</h1>
    </div>
    <HelloWorld msg="Welcome to Your Vue.js App"/>
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from '@/components/HelloWorld.vue';
import { mapActions, mapState } from 'vuex';

export default {
  name: 'Home',
  components: {
    HelloWorld,
  },

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
