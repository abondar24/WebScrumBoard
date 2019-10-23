const contributorUrl = process.env.VUE_APP_API_ENDPOINT + "/contributor";
const formConfig = {
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
    }
};

export default {
    state: {
        projectOwner: {
            id: 0,
            login: '',
            password: '',
            email: '',
            firstName: '',
            lastName: '',
            roles: '',
            avatar: null
        },
        projectContributors: []
    },
    mutations: {
        setProjectOwner(state, owner) {
            state.projectOwner = owner;
        },
        setContributors(state, contrs) {
            state.projectContributors = contrs;
        }
    },
    getters: {
        getProjectOwner: state => {
            return state.projectOwner;
        },
        getProjectContributors: state => {
            return state.projectContributors;
        }
    },
    actions: {
        createContributor({commit, getters}, ctrData) {
            const form = new URLSearchParams();
            form.append('userId', ctrData.userId);
            form.append('projectId', ctrData.projectId);
            form.append('isOwner', ctrData.owner);

            return getters.authenticatedAxios.post(contributorUrl + '/create', form, formConfig).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code === 301) {
                        commit('setErrorMessage', response.data);
                    }
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        findProjectOwner({commit, getters}, projectId) {

            return getters.authenticatedAxios.get(contributorUrl + '/find_project_owner', {
                params: {
                    projectId: projectId
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code === 204) {
                        commit('setErrorMessage', response.data);
                    } else {
                        commit('setProjectOwner', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        findProjectContributors({commit, getters}, queryParams){
            return getters.authenticatedAxios.get(contributorUrl + '/find_project_contributors', {
                params: {
                    projectId: queryParams.projectId,
                    offset: queryParams.offset,
                    limit: queryParams.limit
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code !== 204) {
                        commit('setContributors', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        }
    }
}
