import Axios from "axios";

const loginUrl = process.env.VUE_APP_API_ENDPOINT + "/user/login";
const logoutUrl = process.env.VUE_APP_API_ENDPOINT + "/user/logout";
const authHeaderKey = 'authorization';
export default {
    state: {
        authenticated: false,
        authorization: ''
    },
    getters: {
        authenticatedAxios: state => {
            return Axios.create({
                headers: {
                    authHeaderKey: state.authorization
                }
            });
        }
    },
    mutations: {
        setAuthenticated(state, header) {
            state.jwt = header;
            state.authenticated = true;
        },

        clearAuthenticated(state) {
            state.authenticated = false;
            state.jwt = null;
        },
        setErrorMessage(state, msg) {
            state.errorMessage = msg;
        }
    },
    actions: {
        loginUser({commit}, credentials) {
            const form = new URLSearchParams();
            form.append("login", credentials.login);
            form.append("password", credentials.password);

            const formConfig = {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            };

            return Axios.post(loginUrl, form, formConfig).then(
                (response) => {
                    commit('setErrorMessage', '', {root: true});
                    commit("setAuthenticated", response.headers[authHeaderKey]);
                },
                (error) => {
                    if (error.response.status===500){
                        commit('setErrorMessage', error.response.data.message, {root: true});
                    } else {
                        commit('setErrorMessage', error.response.data, {root: true});
                    }

                });

        },
        logOutUser({commit,getters}){
            return getters.authenticatedAxios.get(logoutUrl,{
                params: {
                    id: getters.getUserId
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '', {root: true});
                    commit("clearAuthenticated");
                },
                (error) => {
                        commit('setErrorMessage', error.response.data, {root: true});

                });

        }
    }
}
