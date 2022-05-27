/* eslint-disable */
const getters = {
  getProfile: (state) => state.userProfile,

  getEmails: (state) => () => Object.entries(state.emailsById).filter((emailById) => {
    const [key] = emailById;
    return state.currentEmailIds.includes(key);
  }).map((emailEntry) => {
    const [key, value] = emailEntry;
    return value;
  }),

  getOneEmail: (state) => (id) => {
    return state.emailsById[id]
  }
};

export default getters;
