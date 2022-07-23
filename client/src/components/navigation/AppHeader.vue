<template>
  <v-app-bar
    app
    color="accent"
    dark
  >
    <div class="d-flex align-center">
      <h1
        class="logo"
        @click="$router.push({name: 'Home'})"
      >
        Hello There!
      </h1>
    </div>

    <v-spacer></v-spacer>
    <template v-if="isNotMobile">
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
    </template>

  </v-app-bar>
</template>
<script>
import MenuOption from '@/components/navigation/MenuOption.vue';
import screenSizeMixin from '@/mixins/screenSizeMixin';

export default {
  name: 'AppHeader',
  components: { MenuOption },
  mixins: [screenSizeMixin],

  data() {
    return {
      notificationCount: 0,
    };
  },

};
</script>

<style scoped>
.logo:hover {
  cursor: pointer;
}

.logo {
  background: -webkit-linear-gradient(
    0.5turn,
    rgba(135, 240, 252, 1),
    rgba(191, 120, 251, 1),
    rgba(250, 115, 198, 1));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
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

.button.active {
  opacity: 1 !important;
}

.button:hover .icon {
  filter: grayscale(0);
}

.underlineEffect {
  display: inline-block;
}

.button:hover .underlineEffect::before {
  transform: scaleX(1);
}

.active .underlineEffect::before {
  transform: scaleX(1) !important;
}

.underlineEffect::before {
  content: '';
  position: absolute;
  width: 100%;
  height: 4px;
  border-radius: 2px;
  bottom: -10px;
  left: 0;
  background: -webkit-linear-gradient(
    1turn,
    rgba(135, 240, 252, 1),
    rgba(191, 120, 251, 1),
    rgba(250, 115, 198, 1));
  transform-origin: bottom left;
  transition: transform 0.4s ease-out;
  transform: scaleX(0);
}

.v-btn {
  opacity: 0.85 !important;
}

@media only screen and (max-width: 1264px) {
  .logo {
    font-size: 1.4em;
  }
}

</style>
