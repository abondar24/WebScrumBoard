import Vue from "vue";
import Vuex from "vuex";
import UserModule from "./user";
import AuthModule from "./auth";
import ProjectModule from "./project";
import ContributorModule from "./contributor"
import TaskModule from "./task"
import SprintModule from "./sprint"

import Cookies from 'js-cookie'
import VuexPersistence from 'vuex-persist'

Vue.config.devtools = true;
Vue.use(Vuex);

const vuexCookie = new VuexPersistence({
    key:'wsc',
    restoreState: (key, storage) => Cookies.getJSON(key),
    saveState: (key, state, storage) =>
        Cookies.set(key, state, {
            expires: 3
        }),
    modules: ['user'],
});

const vuexLocal = new VuexPersistence({
    key:'wsc',
    storage: window.localStorage
});


export default new Vuex.Store({
    strict: false,
    plugins: [vuexCookie.plugin,vuexLocal.plugin],
    modules: {user: UserModule, auth: AuthModule,
        project: ProjectModule, contributor: ContributorModule,task: TaskModule,
    sprint:SprintModule},
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

