import qs from "qs";

const taskUrl = process.env.VUE_APP_API_ENDPOINT + "/task";

export default {
    state: {
        projectTasks:[],
        userTasks: [],
        contributorTasks: [],
        sprintTasks: [],
        userTasksNum: 0,
        contributorTasksNum: 0,
        sprintTasksNum: 0

    },
    mutations: {
        setProjectTasks(state,pTasks){
            state.projectTasks = pTasks;
        },
        setUserTasks(state, uTasks) {
            state.userTasks = uTasks;
        },
        setContributorTasks(state, cTasks) {
            state.contributorTasks = cTasks;
        },
        setSprintTasks(state, sTasks) {
            state.sprintTasks = sTasks;
        },
        setUserTasksNum(state, num) {
            state.userTasksNum = num;
        },
        setContributorTasksNum(state, num) {
            state.contributorTasksNum = num;
        },
        setSprintTasksNum(state, num) {
            state.sprintTasksNum = num;
        }

    },
    getters: {
        getProjectTasks: state => {
          return state.projectTasks;
        },
        getUserTasks: state => {
            return state.userTasks;
        },
        getContributorTasks: state => {
            return state.contributorTasks;
        },
        getSprintTasks: state => {
            return state.sprintTasks;
        },
        getUserTasksNum: state => {
            return state.userTasksNum;
        },
        getContributorTasksNum: state => {
            return state.contributorTasksNum;
        },
        getSprintTasksNum: state => {
            return state.sprintTasksNum;
        }

    },
    actions: {
        createTask({commit, getters}, taskData) {
            const form = new URLSearchParams();

            form.append("ctrId", taskData.ctrId);
            form.append("startDate", taskData.startDate);
            form.append("devOps", taskData.devOps);
            form.append("taskName", taskData.taskName);
            form.append("taskDescription", taskData.taskDescription);


            return getters.authenticatedAxios.post(taskUrl, form, getters.getFormConfig).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updateTask({commit, getters}, taskData) {
            const form = new URLSearchParams();

            if (taskData.ctrId !== 0) {
                form.append("ctrId", taskData.ctrId);
            }

            if (taskData.startDate !== null) {
                form.append("startDate", taskData.startDate);

            }

            if (taskData.devOps!==null){
                form.append("devOps", taskData.devOps);
            }

            if (taskData.storyPoints!==0){
                form.append("storyPoints", taskData.storyPoints);
            }

            if (taskData.taskName.length){
                form.append("taskName", taskData.taskName);
            }

            if (taskData.taskDescription.length){
                form.append("taskDescription", taskData.taskDescription);
            }

            return getters.authenticatedAxios.put(taskUrl + '/'+taskData.id, form,  getters.getFormConfig).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        deleteTask({commit, getters}, taskId) {
            return getters.authenticatedAxios.delete(taskId + '/'+taskId, {
                headers: getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updateTaskSprint({commit, getters}, taskData) {

            return getters.authenticatedAxios.put(taskUrl +'/'+taskData.id +'/sprint/' + taskData.sprintId, getters.getFormConfig).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updateTaskState({commit, getters}, taskData) {

            const form = new URLSearchParams();

            form.append("state",taskData.state);

            return getters.authenticatedAxios.put(taskUrl +'/'+taskData.id  + '/state', form, getters.getFormConfig).then(
                (response) => {
                    commit('setErrorMessage', response.data);
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },

        updateTasksSprint({commit,getters},params){
            return getters.authenticatedAxios.put(taskUrl +'/sprint/' + params.sprintId, {
                params: {
                    id: params.ids
                },
                headers: getters.getLangHeader,
                paramsSerializer: function (params) {
                    return qs.stringify(params.id, {arrayFormat: 'repeat'})
                }
            }).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        findSprintTasks({commit, getters}, queryParams) {
            return getters.authenticatedAxios.get(taskUrl + '/sprint'+queryParams.sprintId, {
                params: {
                    offset: queryParams.offset,
                    limit: queryParams.limit
                },
                headers: getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code !== 204) {
                        commit('setSprintTasks', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        findContributorTasks({commit, getters}, queryParams) {
            return getters.authenticatedAxios.get(taskUrl + '/contributor/'+queryParams.contributorId, {
                params: {
                    offset: queryParams.offset,
                    limit: queryParams.limit
                },
                headers: getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code !== 204) {
                        commit('setContributorTasks', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        findUserTasks({commit, getters}, queryParams) {
            return getters.authenticatedAxios.get(taskUrl + '/user/'+queryParams.userId, {
                params: {
                    offset: queryParams.offset,
                    limit: queryParams.limit
                },
                headers: getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code !== 204) {
                        commit('setUserTasks', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        findProjectTasks({commit, getters}, queryParams) {
            return getters.authenticatedAxios.get(taskUrl + '/project/'+queryParams.prId, {
                params: {
                    offset: queryParams.offset,
                    limit: queryParams.limit,
                    all: queryParams.all,
                },
                headers: getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code !== 204) {
                        commit('setProjectTasks', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        countContributorTasks({commit, getters}, contributorId) {
            return getters.authenticatedAxios.get(taskUrl + '/contributor/'+contributorId+'/count', {
                headers: getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code !== 204) {
                        commit('setContributorTasksNum', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        countUserTasks({commit, getters}, userId) {
            return getters.authenticatedAxios.get(taskUrl + '/user/'+userId+'/count', {
                headers: getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code !== 204) {
                        commit('setUserTasksNum', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        countSprintTasks({commit, getters}, sprintId) {
            return getters.authenticatedAxios.get(taskUrl + '/sprint/'+sprintId+'/count', {

                headers: getters.getLangHeader
            }).then(
                (response) => {
                    commit('setErrorMessage', '');

                    if (response.code !== 204) {
                        commit('setSprintTasksNum', response.data);
                    }

                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
    }
}
