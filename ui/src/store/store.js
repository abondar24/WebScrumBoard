import Vue from "vue";
import Vuex from "vuex";
import Axios from "axios";
import UserModule from "./user";
import AuthModule from "./auth"

Vue.use(Vuex);

export default new Vuex.Store({
  strict: false,
  modules: {user: UserModule, auth: AuthModule},
  state: {
  },
  getters: {

  },
  mutations: {

  },
  actions: {}

})

