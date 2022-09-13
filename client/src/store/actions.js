import axios from 'axios';
import ErrorResponseUtil from '@/utils/ErrorResponseUtil';
import fileDownload from 'js-file-download';

const actions = {
  verifyLoggedIn: ({ commit }) => axios.get('/api/user/isLoggedIn').then(
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

  fetchFullLeaderboard: () => axios.get('/api/leaderboard/full-leaderboard'),

  fetchEmails: ({ commit }, pageToken) => axios.get(`/api/gmail/emails?pageToken=${pageToken || ''}`)
    .then((response) => {
      commit('setThreadsById', response.data);
      commit('resetAndSetCurrentThreadIds', response.data);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  searchEmails: ({ commit }, payload) => axios.get(`/api/gmail/emails?searchString=${payload.searchString}&labels=${payload.labels}&pageToken=${payload.pageToken || ''}`)
    .then((response) => {
      commit('setThreadsById', response.data);
      commit('resetAndSetCurrentThreadIds', response.data);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  fetchLabels: ({ commit }) => axios.get('/api/label')
    .then((response) => {
      commit('setLabels', response.data.labels);
      commit('setLabelAvailableColors', response.data.colors);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  updateLabelVisibility: ({ commit }, payload) => axios.post('/api/label/is-viewable/update', payload)
    .then((response) => {
      commit('updateLabel', response.data);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  updateLabelColor: ({ commit }, payload) => axios.post('/api/label/color/update', payload)
    .then((response) => {
      commit('updateLabel', response.data);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  updateLabels: ({ commit }, payload) => axios.post('/api/label/update', payload)
    .then((response) => {
      commit('setLabels', response.data.allLabels);
      commit('updateEmailThreadLabels', response.data.threadLabelMap);
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

  sendEmail: ({ commit }, payload) => axios.post('/api/gmail/send', payload, { headers: { 'Content-Type': 'multipart/form-data' } })
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
      commit('updateEmailThreadsByIdAddEmail', response.data);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  sendLogoutRequest: ({ commit }) => axios.get('/api/gmail/logout')
    .then(() => {
      commit('setIsLoggedIn', false);
      commit('resetToDefault', false);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  fetchUserChallenges: ({ commit }) => axios.get('/api/challenge')
    .then((response) => {
      commit('setUserChallenges', response.data);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  claimUserChallengeReward: ({ commit }, challengeId) => axios.get(`/api/challenge/claim-reward/${challengeId}`)
    .then((response) => {
      commit('setUserChallengesById', response.data);
      commit('updateUserProfileXP', response.data);
      commit('addColorToAvailableColors', response.data.color);
      commit('addTitleToAvailableTitles', response.data.title);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  fetchNotificationKey: ({ commit }) => axios.get('/api/user/get-notification-token')
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  sendUpdateTitleRequest: ({ commit }, payload) => axios.post('/api/user/update-title', payload)
    .then((response) => {
      commit('setProfileTitle', response.data);
    }).catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
      throw e;
    }),

  sendUpdateNotificationToken: ({ commit }, payload) => axios.post('/api/user/update-notification-token', payload)
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
      throw e;
    }),

  getIsGamificationViewsEnabled: ({ commit }) => axios.get('/api/feature/gamification-view-enabled')
    .then((response) => {
      commit('setFeaturesLoaded', true);
      commit('setIsGamificationEnabled', response.data);
    })
    .catch((e) => {
      const newLoggedIn = ErrorResponseUtil.loggedInNewState(e);
      commit('setIsLoggedIn', newLoggedIn);
      console.error(e);
    }),

  // eslint-disable-next-line no-empty-pattern
  fetchAndDownloadAttachment: ({}, params) => axios.request({
    url: `/api/gmail/attachment?attachmentId=${params.id}&emailId=${params.emailId}`,
    method: 'GET',
    responseType: 'blob',
  })
    .then((response) => {
      fileDownload(response.data, params.name);
    }),
};

export default actions;
