import Axios from "axios";

const loginUrl = process.env.VUE_APP_API_ENDPOINT + "/user/login";
const logoutUrl = process.env.VUE_APP_API_ENDPOINT + "/user";
const authorization = 'authorization';


export default {
    state: {
        authenticated: false,
        jwt: ''
    },
    mutations: {
        setAuthenticated(state, token) {
            state.authenticated = true;
            state.jwt = token;
        },

        clearAuthenticated(state) {
            state.authenticated = false;
            state.jwt = '';
        },
        setErrorMessage(state, msg) {
            state.errorMessage = msg;
        }
    },
    getters: {
        authenticatedAxios: state => {
            return Axios.create({
                headers: {
                    authorization: state.jwt
                }
            });
        },
        getAuth: state=>{

            return state.authenticated;
        }
    },
    actions: {
        loginUser({commit,dispatch,getters}, credentials) {
            const form = new URLSearchParams();
            form.append('login', credentials.login);
            form.append('password', credentials.password);

            console.log(getters.getFormConfig)

            return Axios.post(loginUrl, form, getters.getFormConfig).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('setAuthenticated', response.headers[authorization]);
                    return dispatch('getUserByLogin',{
                        login:credentials.login,
                        isView:false
                    }) ;
                },
                (error) => {
                    if (error.response.status === 500) {
                        commit('setErrorMessage', error.response.data.message);
                    } else {
                        commit('setErrorMessage', error.response.data);
                    }
                });
        },
        logOutUser({commit, getters}) {
            return getters.authenticatedAxios.get(logoutUrl +'/' +getters.getUserId + '/', {

                headers:getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('clearAuthenticated');
                    commit('clearUser');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                    commit('clearAuthenticated');
                    commit('clearUser');

                });

        }
    }
}
