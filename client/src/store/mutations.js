import { getDefaultState } from '@/store/state';

const mutations = {
  setIsLoggedIn: (state, isLoggedIn) => {
    state.isLoggedIn = isLoggedIn;
    localStorage.setItem('loggedIn', `${isLoggedIn}`);
  },

  setLoading: (state, isLoading) => {
    state.loading = isLoading;
  },

  setComposingEmail: (state, isComposing) => {
    state.composingEmail = isComposing;
  },

  setProfile: (state, info) => {
    state.userProfile = info;
  },

  updateUserProfileXP: (state, claimedChallenge) => {
    state.userProfile.totalExperience += claimedChallenge.reward;
    state.userProfile.currentWeekStats.experience += claimedChallenge.reward;
  },

  setLabels: (state, labels) => {
    state.labels = labels;
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

  updateEmailThreadLabels: (state, threadLabelMap) => {
    Object.entries(threadLabelMap).forEach(([key, value]) => {
      state.threadsById[key].labelIds = value;
    });
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

  prependToCurrentThreadIds: (state, newId) => {
    state.currentThreadIds.unshift(newId);
  },

  setProfileLeaderboardUsername: (state, newName) => {
    state.profile.leaderboardUsername = newName;
  },

  resetToDefault: (state) => {
    Object.assign(state, getDefaultState());
  },

  setUserChallenges: (state, challenges) => {
    state.challenges = challenges;
  },

  setUserChallengesById: (state, challenge) => {
    const index = state.challenges.findIndex(
      (storedChallenge) => storedChallenge.challengeId === challenge.challengeId,
    );
    console.log(index);
    console.log(state.challenges[index]);
    state.challenges.splice(index, 1, challenge);
  },
};

export default mutations;
