function getDefaultState() {
  return {
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
