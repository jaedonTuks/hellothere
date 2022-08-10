export default {
  methods: {
    getPercentage(value, total) {
      let safeTotal = total;
      if (safeTotal === 0) {
        safeTotal = 1;
      }
      return Math.round((value / safeTotal) * 100);
    },
  },
};
