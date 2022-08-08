export function getDefaultState() {
  return {
    composingEmail: false,
    isLoggedIn: true,
    loading: false,
    userProfile: {
      leaderboardUsername: '',
      rank: '',
      currentWeekStats: null,
    },
    labels: [],
    emailsById: {},
    threadsById: {},
    currentThreadIds: [],
  };
}

const state = getDefaultState();

export default state;
