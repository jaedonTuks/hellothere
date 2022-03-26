<template>
  <v-app-bar
      app
      color="accent"
      dark
  >
    <div class="d-flex align-center">
     <h1 class="logo">Hello There!</h1>
    </div>

    <v-spacer></v-spacer>

    <v-btn
        text
    >
      <span class="mr-2">Home</span>
      <v-icon>mdi-open-in-new</v-icon>
    </v-btn>

    <v-btn
        text
    >
      <span class="mr-2">Profile</span>
      <v-icon>mdi-profile</v-icon>
    </v-btn>
    <v-btn
        text
    >
      <span class="mr-2">Leaderboards</span>
      <v-icon>mdi-profile</v-icon>
    </v-btn>
  </v-app-bar>
</template>
<script>
export default {
  name: 'AppHeader',
};
</script>

<style scoped>
.logo {
  color: var(--v-secondary-base);
}

span {
  color: var(--v-info-base);
}
</style>
