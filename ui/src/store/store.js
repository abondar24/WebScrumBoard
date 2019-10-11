import Vue from "vue";
import Vuex from "vuex";
import UserModule from "./user";
import AuthModule from "./auth";
import ProjectModule from "./project";
import ContributorModule from "./contributor"
import createPersistedState from 'vuex-persistedstate';
import Cookies from 'js-cookie';

Vue.config.devtools = true;
Vue.use(Vuex);

export default new Vuex.Store({
    strict: false,
    plugins: [createPersistedState({
        key: 'wsc',
        storage: {
            getItem: key => Cookies.get(key),
            setItem: (key, value) => Cookies.set(key, value, {expires: 6000, secure: false}),
            removeItem: key => Cookies.remove(key)
        }
    })],
    modules: {user: UserModule, auth: AuthModule,
        project: ProjectModule, contributor:ContributorModule},
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

