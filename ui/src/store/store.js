import Vue from "vue";
import Vuex from "vuex";
import UserModule from "./user";
import AuthModule from "./auth"

Vue.use(Vuex);

export default new Vuex.Store({
  strict: false,
  modules: {user: UserModule, auth: AuthModule},
  state: {
    errorMessage: ''
  },
  mutations: {
    setErrorMessage(state, msg) {
      state.errorMessage = msg;
    },
  },
  getters: {
    getErrorMsg: state => {
      return state.errorMessage;
    }
  },
  actions: {}

})

