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

Vue.config.productionTip = false;


Vue.use(BootstrapVue);
Vue.use(ImageUploader);

/* eslint-disable no-new */
new Vue({
  render: h => h(App),
  store,
  router,
  components: { App }
}).$mount('#app')
