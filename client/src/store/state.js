function getDefaultState() {
  return {
    isLoggedIn: true,
    loading: false,
    userProfile: {
      username: '',
      rank: '',
      currentWeekStats: null,
    },
    emailsById: {},
    threadsById: {},
    currentThreadIds: [],
  };
}

const state = getDefaultState();

export default state;
