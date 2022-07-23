<template>
  <div v-show="!loading" class="leaderboards mt-10">
    <div v-if="error" class="disabled">
      Feature not currently available
    </div>
    <div class="topThree">
      <h1>Top Three</h1>
      <v-row align="center" class="mt-5">
        <LeaderBoardCard
          v-if="isMobile"
          is-first-place
          :user-info="topThree.first"
        />
        <LeaderBoardCard
          is-second-place
          :user-info="topThree.second"
        />
        <LeaderBoardCard
          v-if="isNotMobile"
          is-first-place
          :user-info="topThree.first"
        />
        <LeaderBoardCard
          is-third-place
          :user-info="topThree.third"
        />
      </v-row>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import LeaderBoardCard from '@/views/LeaderBoardCard.vue';
import loadingMixin from '@/mixins/loadingMixin';
import screenSizeMixin from '@/mixins/screenSizeMixin';

export default {
  name: 'Leaderboards',
  components: { LeaderBoardCard },
  mixins: [loadingMixin, screenSizeMixin],

  data() {
    return {
      topThree: {
        first: {},
        second: {},
        third: {},
      },
      error: false,
    };
  },

  methods: {
    ...mapActions(['fetchLeaderboardTopThree']),
  },

  created() {
    console.log('created why is it being called so much');
    this.setLoading(true);
    this.fetchLeaderboardTopThree().then((response) => {
      this.topThree = response.data;
    }).catch((error) => {
      console.log(error);
      this.error = true;
    }).finally(() => {
      this.setLoading(false);
    });
  }
  ,

};
</script>
