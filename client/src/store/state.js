function getDefaultState() {
  return {
    loading: false,
    userProfile: {
      username: '',
      rank: '',
      currentWeekStats: null,
    },
  };
}

const state = getDefaultState();

export default state;
