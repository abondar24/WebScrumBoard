import Axios from "axios";

const userUrl = process.env.API_ENDPOINT + "/user";

const config = {
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded'
  }
};

export default {
  state: {
    user: null,
    errorMessage: ''
  },
  mutations: {
    setUser(state, user) {
      state.user = user;
    },
    setErrorMessage(state, msg) {
      state.errorMessage = msg;
    },
    clearUser(state) {
      state.user = null;
    }
  },
  getters: {
    getError(state) {
      return state.errorMessage;
    }
  },
  actions: {
    async registerUser({commit}, user) {
      const form = new URLSearchParams();
      form.append('login', user.login);
      form.append('password', user.password);
      form.append('email', user.email);
      form.append('firstName', user.firstName);
      form.append('lastName', user.lastName);
      form.append('roles', user.roles);

       Axios.post(userUrl + '/create', form, config)
        .then(
          (response) => {
            commit('setUser', response.data);
            if (response.status === 206) {
              commit('setErrorMessage', response.data);
            }
          },
          (error) => {
            commit('setErrorMessage', error.response.data);
          });

    }
  }
}
