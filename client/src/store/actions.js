import axios from 'axios';

const actions = {
  fetchUserInfo: ({ commit }) => axios.get('/api/user').then(
    (response) => {
      commit('setProfile', response.data);
    },
  ).catch((e) => {
    console.error(e);
  }),

  fetchLeaderboardTopThree: () => axios.get('/api/leaderboard/top-three'),

  fetchEmails: ({ commit }) => axios.get('/api/gmail/emails')
    .then((response) => {
      commit('setEmailsById', response.data);
      commit('resetAndSetCurrentEmailIds', response.data);
    })
    .catch((e) => {
      console.error(e);
    }),

  searchEmails: ({ commit }, payload) => axios.get(`/api/gmail/search?searchString=${payload.searchString}&labels=${payload.labels}`)
    .then((response) => {
      commit('setEmailsById', response.data);
      commit('resetAndSetCurrentEmailIds', response.data);
    })
    .catch((e) => {
      console.error(e);
    }),

  fetchFullEmail: ({ commit }, id) => axios.get(`/api/gmail/email/${id}`)
    .then((response) => {
      commit('updateEmailById', response.data);
    })
    .catch((e) => {
      console.error(e);
    }),
};

export default actions;
