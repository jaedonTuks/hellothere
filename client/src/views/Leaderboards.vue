<template>
  <div v-show="!loading" class="leaderboards mt-10">
    <div v-if="error" class="disabled">
      Feature not currently available
    </div>
    <div class="topThree">
      <h1>Top Three</h1>
      <v-row class="mt-5">
        <v-col class="secondPlace" cols="4">
          <v-card color="accent" height="300">
            <v-card-title class="secondPlaceTitle">
              <v-icon class="secondPlaceTitleIcon" x-large>mdi-medal-outline</v-icon>
              <h2>Second place</h2>
              <LeaderBoardCardInfo
                :user-info="topThree.second"
              />
            </v-card-title>
          </v-card>
        </v-col>
        <v-col class="firstPlace" cols="4">
          <v-card color="accent" height="350">
            <v-card-title class="firstPlaceTitle">
              <v-icon class="firstPlaceTitleIcon" x-large>mdi-medal-outline</v-icon>
              <h2>First place</h2>
              <LeaderBoardCardInfo
                :user-info="topThree.first"
              />
            </v-card-title>
          </v-card>
        </v-col>
        <v-col class="thirdPlace" cols="4">
          <v-card color="accent" height="250">
            <v-card-title class="thirdPlaceTitle">
              <v-icon class="thirdPlaceTitleIcon" x-large>mdi-medal-outline</v-icon>
              <h2>Third place</h2>
              <LeaderBoardCardInfo
                :user-info="topThree.third"
              />
            </v-card-title>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import LeaderBoardCardInfo from '@/components/LeaderBoardCardInfo.vue';
import loadingMixin from '@/loadingMixin';

export default {
  name: 'Leaderboards',
  components: { LeaderBoardCardInfo },
  mixins: [loadingMixin],

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

<style scoped>
.firstPlace {
  color: gold;
  height: 30em;
}

.secondPlace {
  color: silver;
  margin-top: 50px;
}

.thirdPlace {
  color: sandybrown;
  margin-top: 100px;
}

.firstPlaceTitle, .firstPlaceTitleIcon {
  color: gold !important;
}

.secondPlaceTitle, .secondPlaceTitleIcon {
  color: silver !important;
}

.thirdPlaceTitle, .thirdPlaceTitleIcon {
  color: sandybrown !important;
}

</style>
