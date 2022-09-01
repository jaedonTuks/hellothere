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
      <snackBar/>
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
import SnackBar from '@/SnackBar.vue';
import { mapActions, mapMutations, mapState } from 'vuex';
import screenSizeMixin from '@/mixins/screenSizeMixin';

// eslint-disable-next-line no-unused-vars
import { getAuth, signInAnonymously, signInWithEmailAndPassword } from 'firebase/auth';
import { getToken } from 'firebase/messaging';

export default {
  name: 'App',
  components: {
    SnackBar,
    AppHeader,
    AppBottomNav,
    Loader,
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
      // todo fix up this flow
      if (!this.isLoggedIn && !this.isLoggedInFromPreviousSession()) {
        this.$router.push({ name: 'Login' });
      }
    },
  },

  methods: {
    ...mapActions(['sendUpdateNotificationToken', 'fetchNotificationKey']),
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
        default:
          break;
      }
    },

    handleSwipeNav(isSwipeRight, leftName, rightName) {
      if (isSwipeRight) {
        if (leftName) {
          this.$router.push({ name: leftName });
        }
      } else if (rightName) this.$router.push({ name: rightName });
    },

    async signIntoFirebase() {
      const auth = getAuth();
      if (!auth.currentUser) {
        console.log(auth.currentUser);
        const keyResponse = await this.fetchNotificationKey();
        // eslint-disable-next-line max-len
        const result = await signInWithEmailAndPassword(auth, keyResponse.data.username, keyResponse.data.pass);
        console.log(result);
        await this.getAndPostToken(keyResponse.data.vapidKey);
      }
    },

    async getAndPostToken(key) {
      console.log(key);
      const token = await getToken(this.$messaging, { vapidKey: key });
      const payload = {
        token,
      };
      await this.sendUpdateNotificationToken(payload);
    },
  },

  created() {
    document.title = 'Hello There!';

    if (this.isLoggedIn) {
      this.signIntoFirebase();
    }
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

html {
  overflow-y: auto
}
</style>
