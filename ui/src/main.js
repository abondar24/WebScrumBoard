// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router/router'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'font-awesome/css/font-awesome.min.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import BootstrapVue from 'bootstrap-vue'
import store from './store/store';
import ImageUploader from "vue-image-upload-resize";
import vuexI18n from 'vuex-i18n';
import trEnglish from './lang/en';
import trGerman from './lang/de';
import trRussian from './lang/ru';

Vue.config.productionTip = false;

Vue.use(BootstrapVue);
Vue.use(ImageUploader);
Vue.use(vuexI18n.plugin,store);

Vue.i18n.add('en', trEnglish);
Vue.i18n.add('de', trGerman);
Vue.i18n.add('ru', trRussian);
Vue.i18n.set('en');

/* eslint-disable no-new */
new Vue({
  render: h => h(App),
  store,
  router,
  components: { App }
}).$mount('#app')

