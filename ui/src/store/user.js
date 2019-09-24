import Axios from "axios";

const userUrl = process.env.VUE_APP_API_ENDPOINT + "/user";

const config = {
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
    }
};

export default {
    state: {
        user: {
            id: 0,
            login: '',
            password: '',
            email: '',
            firstName: '',
            lastName: '',
            roles: '',
            avatar: null
        },
    },
    mutations: {
        setUser(state, user) {
            state.user = user;
        },
        setAvatar(state,avatar){
            state.user.avatar = avatar;
        },
        clearUser(state) {
            state.user = {
                id: 0,
                login: '',
                password: '',
                email: '',
                firstName: '',
                lastName: '',
                roles: '',
                avatar: null
            };
        }
    },
    getters: {
         getUserId: state => {
             return state.user.id;
         },
        getUserLogin: state => {
            return state.user.login;
        },
        getUserPassword: state => {
            return state.user.password;
        },
        getUser: state =>{
            return state.user;
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
        verifyCode({commit, getters}, code) {
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
        },
        getUserByLogin({commit, getters}, login) {
            return Axios.get(userUrl + '/find', {
                params: {
                    login: login
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('setUser', response.data);
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        resetPassword({commit,getters}) {
            return Axios.get(userUrl + '/reset_pwd', {
                params: {
                    id: getters.getUserId
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updatePassword({commit,getters},passwords){
            const form = new URLSearchParams();
            form.append('oldPassword', passwords.oldPassword);
            form.append('newPassword', passwords.newPassword);
            form.append('id', getters.getUserId);

            return Axios.post(userUrl + '/update_password', form,config).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updateAvatar({commit,getters},avatar){
             const config =  {
                 params: {
                     id: id
                 },
                 body: avatar
             };

             return getters.authenticatedAxios.post(userUrl + '/update_avatar', config).then(
                 (response) => {
                     commit('setErrorMessage', '');
                     commit('setAvatar',avatar);
                 },
                 (error) => {
                     commit('setErrorMessage', error.response.data);
                 });
        }
    }
}
