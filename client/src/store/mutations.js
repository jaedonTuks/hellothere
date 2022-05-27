const mutations = {
  setLoading: (state, isLoading) => {
    state.loading = isLoading;
  },

  setProfile: (state, info) => {
    state.userProfile = info;
  },

  setEmailsById: (state, emails) => {
    const emailsObj = state.emailsById;

    emails.forEach((email) => {
      emailsObj[email.id] = email;
    });

    state.emailsById = emailsObj;
  },

  resetAndSetCurrentEmailIds: (state, emails) => {
    state.currentEmailIds = [];
    const emailIds = emails.map((email) => email.id);
    state.currentEmailIds = emailIds;
  },

};

export default mutations;
