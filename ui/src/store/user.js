import Axios from "axios";

const userUrl = process.env.VUE_APP_API_ENDPOINT + "/user";

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
        getErrorMsg: state => {
            return state.errorMessage;
        },
        getUserId: state => {
            return state.user.id;
        }
    },
    actions: {
        registerUser({commit}, user) {
            const form = new URLSearchParams();
            form.append('login', user.login);
            form.append('password', user.password);
            form.append('email', user.email);
            form.append('firstName', user.firstName);
            form.append('lastName', user.lastName);
            form.append('roles', user.roles);

            return Axios.post(userUrl + '/create', form, config)
                .then(
                    (response) => {
                        commit('setUser', response.data);
                        commit('setErrorMessage', '');
                        if (response.status === 206) {
                            commit('setErrorMessage', response.data);
                        }
                    },
                    (error) => {
                        commit('setErrorMessage', error.response.data);
                    });

        },
        verifyCode({commit,getters}, code) {
            return Axios.get(userUrl + '/enter_code', {
                params: {
                    userId: getters.getUserId,
                    code: code
                }
            })
                .then(
                    (response) => {
                        commit('setErrorMessage', '');
                    },
                    (error) => {
                        commit('setErrorMessage', error.response.data);
                    });
        }
    }
}
