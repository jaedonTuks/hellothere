import axios from 'axios';

const actions = {
  // todo pass through info for test user to get
  fetchUserInfo: ({ commit }) => axios.get('/api/user').then(
    (response) => {
      commit('setProfile', response.data);
    },
  ).catch((e) => {
    console.log(e);
  }),

  fetchLeaderboardTopThree: () => axios.get('/api/leaderboard/top-three'),

  fetchEmails: () => axios.get('/api/gmail/emails'),
};

export default actions;
