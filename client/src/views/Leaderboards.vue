<template>
  <div class="about mt-10">
    <h1>Leaderboards page</h1>
    <v-row class="mt-5">
      <v-col class="secondPlace" cols="4">
        <v-card color="accent" height="300">
          <v-card-title class="secondPlaceTitle">
            <v-icon class="secondPlaceTitleIcon" x-large>mdi-medal-outline</v-icon>
            <h2>Second place</h2>
          </v-card-title>
        </v-card>
      </v-col>
      <v-col class="firstPlace" cols="4">
        <v-card color="accent" height="350">
          <v-card-title class="firstPlaceTitle">
            <v-icon class="firstPlaceTitleIcon" x-large>mdi-medal-outline</v-icon>
            <h2>First place</h2>
          </v-card-title>
        </v-card>
      </v-col>
      <v-col class="thirdPlace" cols="4">
        <v-card color="accent" height="250">
          <v-card-title class="thirdPlaceTitle">
            <v-icon class="thirdPlaceTitleIcon" x-large>mdi-medal-outline</v-icon>
            <h2>Third place</h2>
          </v-card-title>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

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
