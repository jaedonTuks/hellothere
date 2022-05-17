import axios from 'axios';

const actions = {
  // todo pass through info for test user to get
  fetchUserInfo: ({ commit }) => axios.get('/api/user/testUser').then(
    (response) => {
      commit('setProfile', response.data);
    },
  ).catch((e) => {
    console.log(e);
  }),
};

export default actions;
