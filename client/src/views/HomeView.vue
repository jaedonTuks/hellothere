<template>
  <div>
    <div>
      <h1>{{ test }}</h1>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex';
import HelloWorld from '../components/HelloWorld.vue';

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
