import Vue from 'vue'
import App from './login.vue'
import i18n from '../../assets/i18n2'
//
new Vue({
  el: '#app',
  render: h => h(App),
  i18n
})
