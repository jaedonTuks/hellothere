const mutations = {
  setIsLoggedIn: (state, isLoggedIn) => {
    state.isLoggedIn = isLoggedIn;
  },

  setLoading: (state, isLoading) => {
    state.loading = isLoading;
  },

  setProfile: (state, info) => {
    state.userProfile = info;
  },

  setThreadsById: (state, emailThreads) => {
    const threadsObj = state.threadsById;

    emailThreads.forEach((emailThread) => {
      threadsObj[emailThread.id] = emailThread;
    });

    state.threadsById = threadsObj;
  },

  setEmailsById: (state, emails) => {
    const emailsObj = state.emailsById;

    emails.forEach((email) => {
      emailsObj[email.id] = email;
    });

    state.emailsById = emailsObj;
  },

  updateEmailById: (state, email) => {
    state.emailsById[email.id] = email;
  },

  updateEmailThreadsById: (state, emailThread) => {
    state.threadsById[emailThread.id] = emailThread;
  },

  updateEmailThreadsByIdAddEmail: (state, payload) => {
    const emailThread = state.threadsById[payload.threadId];
    emailThread.emails.push(payload.newEmail);
  },

  resetAndSetCurrentThreadIds: (state, emailThreads) => {
    state.currentThreadIds = [];
    state.currentThreadIds = emailThreads.map((email) => email.id);
  },

};

export default mutations;
