export default {
  computed: {
    isMobile() {
      return this.$vuetify.breakpoint.mobile;
    },

    isNotMobile() {
      return !this.$vuetify.breakpoint.mobile;
    },
  },
};
