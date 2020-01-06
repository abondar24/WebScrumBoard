const sprintUrl = process.env.VUE_APP_API_ENDPOINT + "/sprint";

const langHeader = {'Accept-Language': 'en'};

const formConfig = {
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Accept-Language': 'en'
    }
};


export default {
    state: {
        sprints: [],
        sprintsCount: 0,
        currentSprint: {
            id: 0,
            name: "",
            startDate: null,
            endDate: null,
            projectId: 0,
            current: false
        },
    },
    mutations: {
        setSprints(state, prSprints) {
            state.sprints = prSprints;
        },
        setSprintsCount(state, count) {
            state.sprintsCount = count;
        },
        setCurrentSprint(state,currSp){
            state.currentSprint = currSp;
        },
    },
    getters: {
        getSprints: state => {
            return state.sprints;
        },
        getSprintsCount: state => {
            return state.sprintsCount;
        },
        getCurrentSprint: state => {
            return state.currentSprint;
        },

    },
    actions: {
        createSprint({commit, getters}, sprintData) {
            const form = new URLSearchParams();
            form.append("name", sprintData.name);
            form.append("startDate", sprintData.startDate);
            form.append("endDate", sprintData.endDate);
            form.append("projectId", sprintData.projectId);

            return getters.authenticatedAxios.post(sprintUrl + '/create', form, formConfig).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.status === 205 ||
                        response.status === 206 ||
                        response.status === 302) {
                        commit('setErrorMessage', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updateSprint({commit, getters}, sprintData) {
            const form = new URLSearchParams();

            form.append("id", sprintData.id);

            if (sprintData.name.length){
                form.append("name", sprintData.name);
            }

            if (sprintData.startDate!=null){
                form.append("startDate", sprintData.startDate);
            }

            if (sprintData.endDate!=null){
                form.append("endDate", sprintData.endDate);
            }


            if (sprintData.isCurrent===undefined){
                form.append("isCurrent", "false");
            } else {
                form.append("isCurrent", sprintData.isCurrent);
            }


            return getters.authenticatedAxios.post(sprintUrl + '/update', form, formConfig).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.status === 205 ||
                        response.status === 206 ||
                        response.status === 302) {
                        commit('setErrorMessage', response.data);

                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        deleteSprint({commit, getters}, sprintId) {
            return getters.authenticatedAxios.delete(sprintUrl + '/delete', {
                params: {
                    id: sprintId
                },
                headers: langHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        findCurrentSprint({commit, getters}, projectId) {
            return getters.authenticatedAxios.get(sprintUrl + '/find_current', {
                params: {
                    prId: projectId
                },
                headers: langHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code === 204) {
                        commit('setErrorMessage',response.data);
                    } else {
                        commit('setCurrentSprint', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        findSprints({commit, getters}, queryParams) {

            return getters.authenticatedAxios.get(sprintUrl + '/find_all', {
               params:queryParams,
                headers: langHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code === 204) {
                        commit('setErrorMessage', error.response.data);
                    } else {
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
                },
                headers: langHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code === 204) {
                        commit('setErrorMessage', error.response.data);
                    } else {
                        commit('setSprintsCount', response.data);
                    }


                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
    }
}
