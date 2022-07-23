<template>
  <v-col
    :class="{
          'firstPlace': isFirstPlace,
          'secondPlace': isSecondPlace,
          'thirdPlace': isThirdPlace
        }"
    cols="12"
    lg="4"
  >
    <v-card color="accent" :height="isFirstPlace? 300 : 250">
      <v-card-title
        :class="{
          'firstPlaceTitle': isFirstPlace,
          'secondPlaceTitle': isSecondPlace,
          'thirdPlaceTitle': isThirdPlace
        }"
      >
        <v-icon
          x-large
          :class="{
            'firstPlaceTitleIcon': isFirstPlace,
            'secondPlaceTitleIcon': isSecondPlace,
            'thirdPlaceTitleIcon': isThirdPlace
          }"
        >
          mdi-medal-outline
        </v-icon>
        <h2>{{ getTitleText() }}</h2>
        <LeaderBoardCardInfo
          :user-info="userInfo"
        />
      </v-card-title>
    </v-card>
  </v-col>
</template>
<script>
import LeaderBoardCardInfo from '@/components/Leaderboard/LeaderBoardCardInfo.vue';
import screenSizeMixin from '@/mixins/screenSizeMixin';

export default {
  name: 'LeaderBoardCard',
  mixins: [screenSizeMixin],

  components: { LeaderBoardCardInfo },
  props: {
    userInfo: Object,
    isFirstPlace: Boolean,
    isSecondPlace: Boolean,
    isThirdPlace: Boolean,
  },

  methods: {
    getTitleText() {
      let title = 'First place';

      if (this.isSecondPlace) {
        title = 'Second place';
      }

      if (this.isThirdPlace) {
        title = 'Third place';
      }

      return title;
    },
  },
};
</script>
<style scoped>
.firstPlace {
  color: gold;
}

.secondPlace {
  color: silver;
}

.thirdPlace {
  color: sandybrown;
}

.firstPlace.large {
  height: 30em;
}

.secondPlace.large {
  margin-top: 50px;
}

.thirdPlace.large {
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
