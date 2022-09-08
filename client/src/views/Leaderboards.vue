<template>
  <div v-show="!loading" class="leaderboards mt-10">
    <div v-if="error" class="disabled">
      Feature not currently available
    </div>
    <div class="topThree">
      <h1 class="gradiantBorderBottom" style="text-align: center">Top Three</h1>
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

    <v-row justify="center" class="mt-8">
      <v-col cols="12">
        <h1 class="gradiantBorderBottom" style="width:100%;text-align: center">
          Leaderboards
        </h1>
      </v-col>
      <v-col cols="12" lg="8">
        <v-data-table
          dark
          disable-filtering
          disable-sort
          hide-default-footer
          style="background-color: #465362"
          :multi-sort="false"
          :loading="fullLeadboardLoading"
          :headers="leaderboardHeaders"
          :items="fullLeaderBoard"
          :item-class="itemRowBackground"
        >
          <template v-slot:item.rank="{ item }">
            {{ item.rank }}
          </template>
          <template v-slot:item.username="{ item }">
            {{ item.username }}
          </template>
          <template v-slot:item.xp="{ item }">
            {{ item.totalXp }}
          </template>
        </v-data-table>
      </v-col>
    </v-row>

  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import LeaderBoardCard from '@/views/LeaderBoardCard.vue';
import loadingMixin from '@/mixins/loadingMixin';
import screenSizeMixin from '@/mixins/screenSizeMixin';

export default {
  name: 'Leaderboards',
  components: { LeaderBoardCard },
  mixins: [loadingMixin, screenSizeMixin],

  data() {
    return {
      ownUsername: '',
      fullLeaderBoard: [],
      topThree: {
        first: {},
        second: {},
        third: {},
      },
      fullLeadboardLoading: true,
      error: false,
      leaderboardHeaders: [
        {
          text: 'Rank',
          value: 'rank',
          align: 'start',
          sort: (a, b) => a.rank > b.rank,
        },
        {
          text: 'Username',
          value: 'username',
          filterable: false,
          sortable: false,
          align: 'start',
        },
        {
          text: 'Total XP',
          value: 'xp',
          align: 'start',
        },
      ],
    };
  },

  computed: {
    ...mapGetters(['getProfile']),
  },

  methods: {
    ...mapActions(['fetchLeaderboardTopThree', 'fetchFullLeaderboard', 'fetchUserInfo']),

    loadLeaderboard() {
      this.fullLeadboardLoading = true;
      this.fetchFullLeaderboard().then((response) => {
        this.fullLeaderBoard = response.data;
      }).finally(() => {
        this.fullLeadboardLoading = false;
      });
    },

    itemRowBackground(item) {
      return item.username === this.ownUsername ? 'tableRowHighlighted' : '';
    },
  },

  created() {
    this.setLoading(true);

    this.ownUsername = this.getProfile().username;
    if (this.ownUsername === '') {
      this.fetchUserInfo().then(() => {
        this.ownUsername = this.getProfile().username;
      });
    }

    this.fetchLeaderboardTopThree().then((response) => {
      this.topThree = response.data;
      this.loadLeaderboard();
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

<style>
.tableRowHighlighted {
  background-color: var(--v-accent-darken1) !important;
}
</style>
