import Vue from 'vue';
import VueApexCharts from 'vue-apexcharts';
import VueSafeIframe from 'vue-safe-iframe';
import App from './App.vue';
import './registerServiceWorker';
import router from './router';
import store from './store';
import vuetify from './plugins/vuetify';
import './assets/styles/border.css';
import Messaging from './firebase';

Vue.use(VueApexCharts);
Vue.use(VueSafeIframe);

Vue.component('apexchart', VueApexCharts);

Vue.config.productionTip = false;
Vue.prototype.$messaging = Messaging;

// eslint-disable-next-line import/prefer-default-export
export const EventBus = new Vue();

new Vue({
  router,
  store,
  vuetify,
  render: (h) => h(App),
}).$mount('#app');
