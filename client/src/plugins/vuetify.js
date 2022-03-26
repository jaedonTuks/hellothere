import Vue from 'vue';
import Vuetify from 'vuetify/lib/framework';

Vue.use(Vuetify);

export default new Vuetify({
  icons: {
    iconfont: 'mdi',
  },
  theme: {
    options: {
      customProperties: true,
    },
    dark: false,
    themes: {
      light: {
        primary: '#80DED9',
        secondary: '#C4676A',
        info: '#FFFBFE',
        accent: '#465362',
        error: '#ef5350',
      },
    },
  },
});
