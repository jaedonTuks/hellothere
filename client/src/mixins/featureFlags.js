import { mapActions, mapState } from 'vuex';

export default {
  methods: {
    ...mapActions(['getIsGamificationViewsEnabled']),
  },

  computed: {
    ...mapState(['isGamificationEnabled', 'areFeatureFlagsLoaded']),
  },

  created() {
    if (!this.areFeatureFlagsLoaded) {
      this.getIsGamificationViewsEnabled();
    }
  },
};
