import Vue from 'vue';
import Vuex from 'vuex';
import actions from './actions';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    test: null,
  },
  mutations: {
    testCommit: (state, data) => {
      state.test = data;
    },
  },
  actions,
  modules: {},
});
