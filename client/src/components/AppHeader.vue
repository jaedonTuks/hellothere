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
      plain
      :class="{
        button: true,
        active: $route.name === 'Home'
      }"
      @click="$router.push({name: 'Home'})"
    >
      <v-img class="icon mr-2" src="../assets/icons/home.png"/>
      <span class="mr-2">Home</span>
    </v-btn>

    <v-btn
    text
    plain
    :class="{
        button: true,
        active: $route.name === 'Leaderboards'
      }"
    @click="$router.push({name: 'Leaderboards'})"
  >
    <v-img class="icon mr-2" src="../assets/icons/first-place.png"/>
    <span class="mr-2">Leaderboards</span>
  </v-btn>

    <v-btn
      text
      plain
      :class="{
        button: true,
        active: $route.name === 'Profile'
      }"
      @click="$router.push({name: 'Profile'})"
    >
      <v-img class="icon mr-2" src="../assets/icons/user.png"/>
      <span class="mr-2">Profile</span>
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

.icon {
  filter: grayscale(1);
  width: 2em;
  transition: 0.5s;
}

.button.active .icon {
  filter: grayscale(0);
}

.button.active{
  opacity: 1!important;
}

.button:hover .icon {
  filter: grayscale(0);
}

</style>
