import Axios from "axios";

const loginUrl = process.env.VUE_APP_API_ENDPOINT+"/user/login";
const logoutUrl = process.env.VUE_APP_API_ENDPOINT+"/user/logout";
const authHeaderKey = 'authorization';
export default {
  state: {
    authenticated: false,
    authorization: '',
    errorMessage:'',
  },
  getters: {
    authenticatedAxios: state=> {
      return Axios.create({
        headers: {
          authHeaderKey: state.authorization
        }
      });
    },
    getErrorMessage: state => {
      return state.errorMessage;
    },
  },
  mutations: {
    setAuthenticated(state,header){
      state.jwt = header;
      state.authenticated = true;
    },

    clearAuthenticated(state){
      state.authenticated = false;
      state.jwt = null;
    },
    setErrorMessage(state, msg) {
      state.errorMessage = msg;
    }
  },
  actions: {
    loginUser({commit}, credentials){
      const form = new URLSearchParams();
      form.append("login",credentials.login);
      form.append("password",credentials.password);

      const formConfig = {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      };

      return  Axios.post(loginUrl,form,formConfig).then(
          (response) => {
            commit('setErrorMessage', '');
            commit("setAuthenticated",response.headers[authHeaderKey]);
          },
          (error) => {
            commit('setErrorMessage', error.response.data);
          });

    }
  }
}
