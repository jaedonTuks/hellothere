import Vue from 'vue';
import VueApexCharts from 'vue-apexcharts';
import App from './App.vue';
import './registerServiceWorker';
import router from './router';
import store from './store';
import vuetify from './plugins/vuetify';
import './assets/styles/border.css';
import './assets/styles/text.scss';
import 'vue-swatches/dist/vue-swatches.css';
import Messaging from './firebase';

Vue.use(VueApexCharts);

Vue.component('apexchart', VueApexCharts);

Vue.config.productionTip = false;
Vue.prototype.$messaging = Messaging;

new Vue({
  router,
  store,
  vuetify,
  render: (h) => h(App),
}).$mount('#app');
