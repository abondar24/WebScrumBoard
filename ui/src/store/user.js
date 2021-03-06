import Axios from "axios";
import qs from "qs"

const userUrl = process.env.VUE_APP_API_ENDPOINT + "/user";


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
        viewUser: {
            id: 0,
            login: '',
            password: '',
            email: '',
            firstName: '',
            lastName: '',
            roles: '',
            avatar: null
        }
    },
    mutations: {
        setUser(state, user) {
            state.user = user;
        },
        setViewUser(state, viewUser) {
            state.viewUser = viewUser;
        },
        setAvatar(state, avatar) {
            state.user.avatar = avatar;
        },
        clearUser(state) {
            state.user = {};
        }
    },
    getters: {
        getUserId: state => {
            return state.user.id;
        },
        getUser: state => {
            return state.user;
        },
        getViewUser: state => {
            return state.viewUser;
        }
    },
    actions: {
        registerUser({commit,getters}, user) {
            const form = new URLSearchParams();
            form.append('login', user.login);
            form.append('password', user.password);
            form.append('email', user.email);
            form.append('firstName', user.firstName);
            form.append('lastName', user.lastName);
            form.append('roles', user.roles);

            return Axios.post(userUrl, form, getters.getFormConfig)
                .then(
                    (response) => {
                        commit('setUser', response.data);
                        commit('setErrorMessage', '');
                        if (response.status === 206 || response.status === 302) {
                            commit('setErrorMessage', response.data);
                        }
                    },
                    (error) => {
                        commit('setErrorMessage', error.response.data);
                    });

        },
        verifyCode({commit, getters}, code) {
            return Axios.get(userUrl +'/'+getters.getUserId +'/code', {
                params: {
                    code: code
                },
                headers: getters.getLangHeader
            })
                .then(
                    (response) => {
                        commit('setErrorMessage', '');
                    },
                    (error) => {
                        commit('setErrorMessage', error.response.data);
                    });
        },
        getUserByLogin({commit, getters}, params) {
            return Axios.get(userUrl + '/'+params.login, {
                params: {
                    headers: getters.getLangHeader
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                    if (params.isView) {
                        commit('setViewUser', response.data);
                    } else {
                        commit('setUser', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        getUsersByIds({commit, getters}, ids) {
            return getters.authenticatedAxios.get(userUrl , {
                params: {
                    id: ids
                },
                headers: getters.getLangHeader,
                paramsSerializer: function (params) {
                    return qs.stringify(params, {arrayFormat: 'repeat'})
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('setViewUser', response.data[0])
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        resetPassword({commit, getters}) {
            return Axios.put(userUrl +'/'+ getters.getUserId+'/reset_pwd', {
                headers: getters.getLangHeader,
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updatePassword({commit, getters}, passwords) {
            const form = new URLSearchParams();
            form.append('oldPassword', passwords.oldPassword);
            form.append('newPassword', passwords.newPassword);

            return Axios.put(userUrl +'/'+getters.getUserId+ '/password', form, getters.getFormConfig).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updateAvatar({commit, getters}, avatar) {
            const config = {
                headers: getters.getMultipartHeaders,
            };

            const body = new FormData();
            body.append('file', avatar);

            return getters.authenticatedAxios.put(userUrl +'/'+getters.getUserId +'/avatar', body, config).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('setAvatar', avatar);
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updateLogin({commit, getters}, login) {
            const form = new URLSearchParams();
            form.append('login', login);

            return getters.authenticatedAxios.put(userUrl +'/'+getters.getUserId +'/avatar', form, getters.getFormConfig).then(
                (response) => {
                    if (response.code === 302) {
                        commit('setErrorMessage', response.data);
                    } else {
                        commit('setErrorMessage', '');

                    }
                    commit('setUser', response.data);
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updateUser({commit, getters}, updUser) {
            const form = new URLSearchParams();
            form.append('firstName', updUser.firstName);
            form.append('lastName', updUser.lastName);
            form.append('email', updUser.email);
            form.append('roles', updUser.roles);


            return getters.authenticatedAxios.put(userUrl + '/'+getters.getUserId , form, getters.getFormConfig).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('setUser', response.data);
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        deleteUser({commit, dispatch, getters}) {
            return getters.authenticatedAxios.delete(userUrl + '/'+getters.getUserId, {
                headers: getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                    return dispatch('logOutUser');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        }
    }
}
