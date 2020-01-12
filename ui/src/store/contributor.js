const contributorUrl = process.env.VUE_APP_API_ENDPOINT + "/contributor";

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
        foundContributor:{
            id:0,
            userId:0,
            projectId:0
        },
        projectContributors: [],
        ctrCount: 0,
        selectedCtrId: 0,
    },
    mutations: {
        setProjectOwner(state, owner) {
            state.projectOwner = owner;
        },
        setContributors(state, contrs) {
            state.projectContributors = contrs;
        },
        setContributorCount(state, count) {
            state.ctrCount = count;
        },
        setSelectedContributorId(state, id) {
            state.selectedCtrId = id;
        },
        setFoundContributor(state,ctr){
            state.foundContributor = ctr;
        }
    },
    getters: {
        getProjectOwner: state => {
            return state.projectOwner;
        },
        getProjectContributors: state => {
            return state.projectContributors;
        },
        getContributorCount: state => {
            return state.ctrCount;
        },
        getSelectedContributorId: state => {
            return state.selectedCtrId;
        },
        getFoundContributor: state => {
            return state.foundContributor;
        }
    },
    actions: {
        createContributor({commit, getters}, ctrData) {
            const form = new URLSearchParams();
            form.append('userId', ctrData.userId);
            form.append('projectId', ctrData.projectId);
            form.append('isOwner', ctrData.owner);

            return getters.authenticatedAxios.post(contributorUrl + '/create', form, getters.getFormConfig).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code === 301 || response.code === 302) {
                        commit('setErrorMessage', response.data);
                    }
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updateContributor({commit, getters}, ctrData) {
            const form = new URLSearchParams();
            form.append('usrId', ctrData.userId);
            form.append('prjId', ctrData.projectId);
            form.append('isOwner', ctrData.owner);
            form.append('isActive', ctrData.active);

            return getters.authenticatedAxios.post(contributorUrl + '/update', form, getters.getFormConfig).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code === 302) {
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
        findProjectContributors({commit, getters}, queryParams) {
            return getters.authenticatedAxios.get(contributorUrl + '/find_project_contributors', {
                params: {
                    projectId: queryParams.projectId,
                    offset: queryParams.offset,
                    limit: queryParams.limit
                },
                headers: getters.getLangHeader
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
        },
        countProjectContributors({commit, getters}, prjId) {
            return getters.authenticatedAxios.get(contributorUrl + '/count_project_contributors', {
                params: {
                    projectId: prjId
                },
                headers: getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('setContributorCount',response.data);
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        findContributor({commit, getters}, queryParams) {
            return getters.authenticatedAxios.get(contributorUrl + '/find_contributor', {
                params: {
                    userId: queryParams.userId,
                    projectId: queryParams.projectId
                },
                headers: getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('setSelectedContributorId', response.data);
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        findContributorByLogin({commit, getters},login) {
            return getters.authenticatedAxios.get(contributorUrl + '/find_contributor_by_login', {
                params: {
                    projectId: getters.getProjectId,
                    login: login
                },
                headers: getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('setFoundContributor', response.data);
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
    }
}
