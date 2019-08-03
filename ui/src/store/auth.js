import Axios from "axios";

const loginUrl = process.env.API_ENDPOINT+"/login";


export default {
  state: {
    authenticated: false,
    jwt: null
  },
  getters: {
    authenticatedAxios(state){
      return Axios.create({
        headers: {
          "Authorization": `JWT <${state.jwt}>`
        }
      });
    }
  },
  mutations: {
    setAuthenticated(state,header){
      state.jwt = header;
      state.authenticated = true;
    },
    clearAuthenticated(state){
      state.authenticated = false;
      state.jwt = null;
    }
  },
  actions: {
    async authenticate(context, credentials){
      let response = await Axios.post(loginUrl,credentials);
      if (response.data.success === true){
        context.commit("setAuthenticated",response.data.token);
      }
    }
  }
}
