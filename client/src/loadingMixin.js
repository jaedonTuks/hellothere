import { mapMutations, mapState } from 'vuex';

export default {
  computed: {
    ...mapState(['loading']),
  },

  methods: {
    ...mapMutations(['setLoading']),
  },
};
