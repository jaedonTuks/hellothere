/* eslint-disable */
const getters = {
  getProfile: (state) => () => state.userProfile,

  getLabelNames: (state) => () => state.labels.map ((label) => label.name[0].toUpperCase() + label.name.substring(1).toLowerCase()),

  getEmailThreads: (state) => () => Object.entries(state.threadsById).filter((threadById) => {
    const [key] = threadById;
    return state.currentThreadIds.includes(key);
  }).map((threadEntry) => {
    const [key, value] = threadEntry;
    return value;
  }),

  getEmails: (state) => () => Object.entries(state.emailsById).filter((emailById) => {
    const [key] = emailById;
    return state.currentEmailIds.includes(key);
  }).map((emailEntry) => {
    const [key, value] = emailEntry;
    return value;
  }),

  getEmailThread: (state) => (id) => {
    return state.threadsById[id]
  },

  getThreadLabels: (state) => (id) => {
    return state.threadsById[id].labelIds
  },

  getCompletedChallenges: (state) => () => {
    return Object.values(state.challenges).filter((challenge)=> challenge.goal < challenge.progress)
  },

  getUserChallenges: (state) => () => {
    return Object.values(state.challenges);
  },
};

export default getters;
