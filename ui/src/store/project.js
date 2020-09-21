const projectUrl = process.env.VUE_APP_API_ENDPOINT + "/project";



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
        userProjects: [],
        projectEditable:false
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

            }
        },
        setProjectEditable(state, editable) {
            state.projectEditable = editable;
        },
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
        },
        getProjectEditable: state =>{
            return state.projectEditable;
        }
    },
    actions: {
        createProject({commit, getters, dispatch}, projectData) {
            const form = new URLSearchParams();
            form.append('name', projectData.name);
            form.append('startDate', projectData.startDate);

            return getters.authenticatedAxios.post(projectUrl , form, getters.getFormConfig).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.status === 206 || response.status === 302) {
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
            form.append('name', projectData.name);
            form.append('repo', projectData.repository);
            form.append('isActive', projectData.isActive);

            if (projectData.endDate!==null){
                form.append('endDate', projectData.endDate);
            }
            form.append('description', projectData.description);

            return getters.authenticatedAxios.put(projectUrl + '/' + projectData.id, form, getters.getFormConfig).then(
                (response) => {
                    commit('setErrorMessage', '');
                    commit('setProject', response.data);
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        deleteProject({commit, getters}, id) {
            return getters.authenticatedAxios.delete(projectUrl + '/'+id, {

                headers: getters.getLangHeader
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
            return getters.authenticatedAxios.get(projectUrl + '/' + id, {
                headers: getters.getLangHeader
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
                },
                headers: getters.getLangHeader
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
