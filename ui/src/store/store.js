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
    key: 'wsc',
    restoreState: (key, storage) => Cookies.getJSON(key),
    saveState: (key, state, storage) =>
        Cookies.set(key, state, {
            expires: 3
        }),
    modules: ['user'],
});

const vuexLocal = new VuexPersistence({
    key: 'wsc',
    storage: window.localStorage
});


export default new Vuex.Store({
    strict: false,
    plugins: [vuexCookie.plugin, vuexLocal.plugin],
    modules: {
        user: UserModule, auth: AuthModule,
        project: ProjectModule, contributor: ContributorModule, task: TaskModule,
        sprint: SprintModule
    },
    state: {
        errorMessage: '',
        langHeader: {'Accept-Language': 'en'},
        formConfig: {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Accept-Language': 'en'
            },
            multiPartHeaders: {
                    'Content-Type': `multipart/form-data;`,
                    'Accept-Language': 'en'
                },

        },
        mutations: {
            setErrorMessage(state, msg) {
                state.errorMessage = msg;
            },
            setLangHeaders(state, lang) {
                state.langHeader = {'Accept-Language': lang};

                state.formConfig.headers = {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Accept-Language': lang
                };

                state.multiPartHeaders = {
                    'Content-Type': `multipart/form-data;`,
                    'Accept-Language': lang
                };
            },
        },
        getters: {
            getErrorMsg: state => {
                return state.errorMessage;
            },
            getLangHeader: state => {
                return state.langHeader;
            },
            getFormConfig: state => {
                return state.formConfig;
            },
            getMultipartHeaders: state => {
                return state.multiPartHeaders;
            },
        }
    },
    actions: {}

})

