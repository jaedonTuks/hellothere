function getDefaultState() {
  return {
    loading: false,
    userProfile: {
      username: '',
      rank: '',
      currentWeekStats: null,
    },
    emailsById: {},
    currentEmailIds: [],
  };
}

const state = getDefaultState();

export default state;
