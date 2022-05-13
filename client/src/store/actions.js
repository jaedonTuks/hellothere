import axios from 'axios';

const actions = {
  fetchLeaderboardTopThree: () => axios.get('/api/leaderboard/top-three'),
};

export default actions;
