import Vue from 'vue'
import App from './sso.vue'
import i18n from '../../assets/i18n2'
import iView from 'iview';

import 'iview/dist/styles/iview.css';
Vue.use(iView);
//
new Vue({
  el: '#app',
  render: h => h(App),
  i18n
})
