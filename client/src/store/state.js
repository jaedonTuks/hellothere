export function getDefaultState() {
  return {
    composingEmail: false,
    displaySnackBar: false,
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
    viewingEmail: {},
    currentThreadIds: [],
  };
}

const state = getDefaultState();

export default state;
