const mutations = {
  updateLoading: (state, isLoading) => {
    state.loading = isLoading;
  },

  setProfile: (state, info) => {
    state.userProfile = info;
  },

};

export default mutations;
