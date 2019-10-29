const taskUrl = process.env.VUE_APP_API_ENDPOINT + "/task";
const formConfig = {
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
    }
};

export default {
    state: {
        userTasks: [],
        contributorTasks: [],
        sprintTasks: [],
        userTasksNum: 0,
        contributorTasksNum: 0,

    },
    mutations: {
        setUserTasks(state,uTasks){
            state.userTasks = uTasks;
        },
        setContributorTasks(state,cTasks){
            state.contributorTasks = cTasks;
        },
        setSprintTasks(state,sTasks){
            state.sprintTasks = sTasks;
        },
        setUserTasksNum(state, num) {
            state.userTasksNum=num;
        },
        setContributorTasksNum(state, num) {
            state.contributorTasksNum=num;
        }

    },
    getters: {
        getUserTasks: state =>{
            return state.userTasks;
        },
        getContributorTasks: state =>{
            return state.contributorTasks;
        },
        getSprintTasks: state =>{
            return state.sprintTasks;
        },
        getUserTasksNum: state =>{
            return state.userTasksNum;
        },
        getContributorTasksNum: state =>{
            return state.contributorTasksNum;
        }

    },
    actions: {
       findSprintTasks({commit,getters}, queryParams){
           return getters.authenticatedAxios.get(taskUrl + '/find_sprint_tasks', {
               params: {
                   spId: queryParams.sprintId,
                   offset: queryParams.offset,
                   limit: queryParams.limit
               }
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
        findContributorTasks({commit,getters}, queryParams){
            return getters.authenticatedAxios.get(taskUrl + '/find_contributor_tasks', {
                params: {
                    ctrId: queryParams.contributorId,
                    offset: queryParams.offset,
                    limit: queryParams.limit
                }
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
        findUserTasks({commit,getters}, queryParams){
            return getters.authenticatedAxios.get(taskUrl + '/find_user_tasks', {
                params: {
                    ctrId: queryParams.userId,
                    offset: queryParams.offset,
                    limit: queryParams.limit
                }
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
        countContributorTasks({commit,getters}, contributorId){
            return getters.authenticatedAxios.get(taskUrl + '/count_contributor_tasks', {
                params: {
                    contributorId: contributorId,
                }
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
        countUserTasks({commit,getters}, userId){
            return getters.authenticatedAxios.get(taskUrl + '/count_user_tasks', {
                params: {
                    userId: userId,
                }
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
    }
}
