import Vue from 'vue';
import Vuex from 'vuex';
import actions from './actions';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    loading: false,
  },
  mutations: {
    setLoading: (state, isLoading) => {
      state.loading = isLoading;
    },
  },
  actions,
  modules: {},
});
