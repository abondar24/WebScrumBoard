const taskUrl = process.env.VUE_APP_API_ENDPOINT + "/task";
const formConfig = {
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Accept-Language': 'en'
    }
};

const langHeader = {'Accept-Language': 'en'};

export default {
    state: {
        userTasks: [],
        contributorTasks: [],
        sprintTasks: [],
        userTasksNum: 0,
        contributorTasksNum: 0,
        sprintTasksNum: 0

    },
    mutations: {
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


            return getters.authenticatedAxios.post(taskUrl + '/create', form, formConfig).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        updateTask({commit, getters}, taskData) {
            const form = new URLSearchParams();

            form.append("id", taskData.id);

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
                form.append("devOps", taskData.storyPoints);
            }

            if (taskData.taskName.length){
                form.append("taskName", taskData.taskName);
            }

            if (taskData.taskDescription.length){
                form.append("taskDescription", taskData.taskDescription);
            }

            return getters.authenticatedAxios.post(taskUrl + '/update', form, formConfig).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },
        deleteTask({commit, getters}, taskId) {
            return getters.authenticatedAxios.delete(taskId + '/delete', {
                params: {
                    id: taskId
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
        updateSprint({commit, getters}, taskData) {

            const form = new URLSearchParams();

            form.append("id",taskData.id);
            form.append("sprintId",taskData.sprintId);

            return getters.authenticatedAxios.post(taskUrl + '/update_sprint', form, formConfig).then(
                (response) => {
                    commit('setErrorMessage', '');
                },
                (error) => {
                    commit('setErrorMessage', error.response.data);
                });
        },

        findSprintTasks({commit, getters}, queryParams) {
            return getters.authenticatedAxios.get(taskUrl + '/find_sprint_tasks', {
                params: {
                    spId: queryParams.sprintId,
                    offset: queryParams.offset,
                    limit: queryParams.limit
                },
                headers: langHeader
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
            return getters.authenticatedAxios.get(taskUrl + '/find_contributor_tasks', {
                params: {
                    ctrId: queryParams.contributorId,
                    offset: queryParams.offset,
                    limit: queryParams.limit
                },
                headers: langHeader
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
            return getters.authenticatedAxios.get(taskUrl + '/find_user_tasks', {
                params: {
                    ctrId: queryParams.userId,
                    offset: queryParams.offset,
                    limit: queryParams.limit
                },
                headers: langHeader
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
        countContributorTasks({commit, getters}, contributorId) {
            return getters.authenticatedAxios.get(taskUrl + '/count_contributor_tasks', {
                params: {
                    contributorId: contributorId,
                },
                headers: langHeader
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
            return getters.authenticatedAxios.get(taskUrl + '/count_user_tasks', {
                params: {
                    userId: userId,
                },
                headers: langHeader
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
            return getters.authenticatedAxios.get(taskUrl + '/count_sprint_tasks', {
                params: {
                    sprintId: sprintId,
                },
                headers: langHeader
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
