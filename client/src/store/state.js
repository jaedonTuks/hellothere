export function getDefaultState() {
  return {
    composingEmail: false,
    displaySnackBar: true,
    isLoggedIn: true,
    loading: false,
    nextPageToken: null,
    snackBarColor: 'error',
    challenges: [],
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
