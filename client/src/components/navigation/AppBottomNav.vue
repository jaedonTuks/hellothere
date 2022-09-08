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
      v-if="isGamificationEnabled"
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
import featureFlags from '@/mixins/featureFlags';

export default {
  name: 'AppHeader',
  components: { MenuOption },

  mixins: [featureFlags],

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
