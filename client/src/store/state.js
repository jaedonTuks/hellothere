export function getDefaultState() {
  return {
    composingEmail: false,
    displaySnackBar: false,
    isLoggedIn: false,
    loading: false,
    areFeatureFlagsLoaded: false,
    isGamificationEnabled: false,
    nextPageToken: null,
    snackBarColor: 'error',
    challenges: [],
    userProfile: {
      leaderboardUsername: '',
      rank: '',
      currentWeekStats: null,
    },
    labels: [],
    labelAvailableColors: [],
    emailsById: {},
    threadsById: {},
    viewingEmail: {},
    currentThreadIds: [],
  };
}

const state = getDefaultState();

export default state;
