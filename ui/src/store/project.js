const projectUrl = process.env.VUE_APP_API_ENDPOINT + "/project";
const formConfig = {
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
    }
};

export default {
    state: {
        project: {
            id: 0,
            name: '',
            startDate: null,
            endDate: null,
            repository: '',
            description: '',
            active: false
        },
        userProjects: []
    },
    mutations: {
        setProject(state, project) {
            state.project = project;
        },
        setUserProjects(state, projects) {
            state.userProjects = projects;
        },
        cleanProject(state) {
            state.project = {
                id: 0,
                name: '',
                startDate: null,
                endDate: null,
                repository: '',
                description: '',
                active: false
            }
        }
    },
    getters: {
        getProject: state => {
            return state.project;
        },
        getProjectId: state => {
            return state.project.id;
        },
        getUserProjects: state => {
            return state.userProjects;
        }
    },
    actions: {
        createProject({commit, getters, dispatch}, projectData) {
            const form = new URLSearchParams();
            form.append('name', projectData.name);
            form.append('startDate', projectData.startDate);

            return getters.authenticatedAxios.post(projectUrl + '/create', form, formConfig).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code === 206) {
                        commit('setErrorMessage', response.data);
                        return;
                    }

                    return dispatch('createContributor', {
                        userId: getters.getUserId,
                        projectId: response.data.id,
                        owner: true
                    });
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updateProject({commit, getters}, projectData) {
            const form = new URLSearchParams();
            form.append('id', projectData.id);
            form.append('name', projectData.name);
            form.append('repo', projectData.repository);
            form.append('isActive', projectData.isActive);

            if (projectData.endDate!==null){
                form.append('endDate', projectData.endDate);
            }
            form.append('description', projectData.description);

            return getters.authenticatedAxios.post(projectUrl + '/update', form, formConfig).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('setProject', response.data);
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        deleteProject({commit, getters}, id) {
            return getters.authenticatedAxios.get(projectUrl + '/delete', {
                params: {
                    id: id
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('cleanProject','');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        findProject({commit, getters}, id) {
            return getters.authenticatedAxios.get(projectUrl + '/find', {
                params: {
                    id: id
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('setProject', response.data);
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        findUserProjects({commit, getters}, userId) {
            return getters.authenticatedAxios.get(projectUrl + '/find_user_projects', {
                params: {
                    id: userId
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('setUserProjects', response.data)
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },

    }
}
