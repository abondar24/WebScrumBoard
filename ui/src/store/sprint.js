const sprintUrl = process.env.VUE_APP_API_ENDPOINT + "/sprint";
const formConfig = {
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
    }
};

export default {
    state: {
        sprints: [],
        sprintsCount: 0
    },
    mutations: {
        setSprints(state, prSprints) {
            state.sprints = prSprints;
        },
        setSprintsCount(state, count) {
            state.sprintsCount = count;
        }
    },
    getters: {
        getSprints: state => {
            return state.sprints;
        },
        getSprintsCount: state => {
            return state.sprintsCount;
        }
    },
    actions: {
        findSprints({commit, getters}, queryParams) {
            return getters.authenticatedAxios.get(sprintUrl + '/find_all', {
                params: {
                    projectId: queryParams.projectId,
                    offset: queryParams.offset,
                    limit: queryParams.limit
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code !== 204) {
                        commit('setSprints', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        countSprints({commit, getters}, projectId) {
            return getters.authenticatedAxios.get(sprintUrl + '/count', {
                params: {
                    projectId: projectId,
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code !== 204) {
                        commit('setSprintsCount', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
    }
}
