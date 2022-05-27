import Vue from 'vue';
import Vuetify from 'vuetify/lib/framework';
import userIcon from '@/assets/icons/user.png';
import '@mdi/font/css/materialdesignicons.css';

Vue.use(Vuetify);

export default new Vuetify({
  icons: {
    iconfont: 'mdi',
    values: {
      profile: userIcon,
    },
  },
  theme: {
    options: {
      customProperties: true,
    },
    dark: true,
    themes: {
      dark: {
        primary: '#80DED9',
        secondary: '#C4676A',
        info: '#FFFBFE',
        accent: '#465362',
        purpleAccent: '#bf78fb',
        pinkAccent: '#fa73c6',
        error: '#ef5350',
      },
    },
  },
});
