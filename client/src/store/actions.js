import axios from 'axios';

const actions = {
  fetchTest: ({ commit }) => axios.get('/api/test').then(
    (response) => {
      commit('testCommit', response.data);
    },
  ),
};

export default actions;
