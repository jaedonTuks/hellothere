import Vue from 'vue';
import VueRouter from 'vue-router';
import Login from '@/views/Login.vue';
import store from '@/store';
import Home from '../views/Home.vue';
import Profile from '../views/Profile.vue';
import Leaderboards from '../views/Leaderboards.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login,
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
  },
  {
    path: '/leaderboards',
    name: 'Leaderboards',
    component: Leaderboards,
  },
  {
    path: '/inbox',
    name: 'Inbox',
    component: Home,
  },
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
});

function handleRouting(to, from, next) {
  if (to.name === 'Login' && store.state.isLoggedIn) {
    next({ name: 'Home' });
  } else if (to.name !== 'Login' && !store.state.isLoggedIn) {
    next({ name: 'Login' });
  } else {
    next();
  }
}

router.beforeEach((to, from, next) => {
  if (!store.state.isLoggedIn) {
    store.dispatch('isLoggedIn').then(() => {
      handleRouting(to, from, next);
    });
  } else {
    handleRouting(to, from, next);
  }
});

export default router;
