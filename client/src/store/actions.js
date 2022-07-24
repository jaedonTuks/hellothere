import axios from 'axios';
import ErrorResponseUtil from '@/utils/ErrorResponseUtil';

const actions = {
  isLoggedIn: ({ commit }) => axios.get('/api/user/isLoggedIn').then(
    (response) => {
      commit('setIsLoggedIn', response.data);
    },
  ).catch((e) => {
    const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
    commit('setIsLoggedIn', newLoggedIn);
    console.error(e);
  }),

  fetchUserInfo: ({ commit }) => axios.get('/api/user').then(
    (response) => {
      commit('setProfile', response.data);
    },
  ).catch((e) => {
    const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
    commit('setIsLoggedIn', newLoggedIn);
    console.error(e);
  }),

  fetchLeaderboardTopThree: () => axios.get('/api/leaderboard/top-three'),

  fetchEmails: ({ commit }) => axios.get('/api/gmail/emails')
    .then((response) => {
      commit('setThreadsById', response.data);
      commit('resetAndSetCurrentThreadIds', response.data);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  searchEmails: ({ commit }, payload) => axios.get(`/api/gmail/search?searchString=${payload.searchString}&labels=${payload.labels}`)
    .then((response) => {
      commit('setThreadsById', response.data);
      commit('resetAndSetCurrentThreadIds', response.data);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  fetchFullEmail: ({ commit }, id) => axios.get(`/api/gmail/email/${id}`)
    .then((response) => {
      commit('updateEmailThreadsById', response.data);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  sendEmail: ({ commit }, payload) => axios.post('/api/gmail/send', payload)
    .then((response) => {
      commit('updateEmailThreadsById', response.data);
      commit('prependToCurrentThreadIds', response.data.id);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  replyToEmail: ({ commit }, payload) => axios.post('/api/gmail/reply', payload)
    .then((response) => {
      commit('updateEmailThreadsByIdAddEmail', { threadId: payload.threadId, newEmail: response.data });
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),
};

export default actions;
