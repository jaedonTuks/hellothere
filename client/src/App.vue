<template>
  <v-app class="app">

    <AppHeader
      v-if="shouldDisplayHeader"
      :is-mobile="isMobile"
    />
    <v-main
      v-touch="{
        left: () => swipe(false),
        right: () => swipe(true),
      }"
    >
        <Loader/>
        <v-container style="width:100%">
          <v-slide-y-transition mode="out-in">
            <router-view/>
          </v-slide-y-transition>
        </v-container>
    </v-main>
    <AppBottomNav
      v-if="shouldDisplayHeader && isMobile"
    />
  </v-app>
</template>

<script>

import AppHeader from '@/components/navigation/AppHeader.vue';
import AppBottomNav from '@/components/navigation/AppBottomNav.vue';
import Loader from '@/components/Loader.vue';
import { mapMutations, mapState } from 'vuex';
import screenSizeMixin from '@/mixins/screenSizeMixin';

export default {
  name: 'App',
  components: {
    AppHeader, AppBottomNav, Loader,
  },
  mixins: [screenSizeMixin],

  computed: {
    ...mapState(['isLoggedIn']),

    shouldDisplayHeader() {
      return this.$route.name !== 'Login';
    },

  },

  watch: {
    isLoggedIn() {
      if (!this.isLoggedIn && !this.isLoggedInFromPreviousSession()) {
        this.$router.push({ name: 'Login' });
      }
    },
  },

  methods: {
    ...mapMutations(['setIsLoggedIn']),

    isLoggedInFromPreviousSession() {
      if (localStorage.getItem('loggedIn') === 'true') {
        this.setIsLoggedIn(true);
      } else {
        this.setIsLoggedIn(false);
      }
    },

    swipe(isSwipeRight) {
      switch (this.$router.currentRoute.name) {
        case 'Inbox': {
          this.handleSwipeNav(isSwipeRight, null, 'Leaderboards');
          break;
        }
        case 'Leaderboards': {
          this.handleSwipeNav(isSwipeRight, 'Inbox', 'Profile');
          break;
        }
        case 'Profile': {
          this.handleSwipeNav(isSwipeRight, 'Leaderboards', null);
          break;
        }
        default: break;
      }
    },

    handleSwipeNav(isSwipeRight, leftName, rightName) {
      if (isSwipeRight) {
        if (leftName) {
          this.$router.push({ name: leftName });
        }
      } else if (rightName) this.$router.push({ name: rightName });
    },
  },

  created() {
    document.title = 'Hello There!';
  },
};
</script>
<style>
@import './assets/styles/loader.css';

div {
  color: #FFF !important;
}

.app {
  background-color: #343E59 !important;
  overflow-x: hidden;
}

html { overflow-y: auto }
</style>
