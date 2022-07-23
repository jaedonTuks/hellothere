<template>
  <v-bottom-navigation
    v-model="value"
    horizontal
    app
  >
    <MenuOption
      route-name="Inbox"
      icon-name="home"
      :notification-count="notificationCount"
    />
    <MenuOption
      route-name="Leaderboards"
      icon-name="first-place"
    />
    <MenuOption
      route-name="Profile"
      icon-name="user"
    />
  </v-bottom-navigation>
</template>

<script>
import MenuOption from '@/components/navigation/MenuOption.vue';

export default {
  name: 'AppHeader',
  components: { MenuOption },

  data() {
    return {
      value: 'inbox',
      notificationCount: 0,
    };
  },

};
</script>

<style scoped>

</style>
