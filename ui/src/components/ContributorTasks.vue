<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                variant="warning">
            {{errorMessage}}
        </b-alert>

        <div v-if="noTasks">
            Contributor currently has no assigned tasks
        </div>

        <div v-if="!noTasks">
            <b-table
                    id="ctrTaskTable"
                    hover
                    responsive
                    :items="tasks"
                    :fields="fields"
                    :per-page="perPage"
                    caption-top
                    :current-page="currentPage">
                <template v-slot:table-caption>Tasks of {{this.user.ctr_name}}</template>
            </b-table>
            <b-pagination
                    v-model="currentPage"
                    :total-rows="totalRows"
                    @input="loadNext(currentPage-1)"
                    :per-page="perPage"
                    aria-controls="ctrTaskTable"/>
        </div>


        <b-button variant="danger" @click="close">Hide</b-button>
    </b-container>
</template>

<script>
    export default {
        name: "ContributorTasks",
        props: ['exit', 'user'],
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                tasks: [],
                fields: [
                    {key: 'taskName', label: 'Task Name'},
                    {key: 'taskState', label: 'Task State'},
                    {key: 'storyPoints', label: 'Story Points'},
                ],
                currentPage: 1,
                perPage: 5,
                totalRows: 0,
                noTasks: false
            }
        },
        beforeMount() {
            this.findContributorId();
            this.countTasks();
            if (this.totalRows > 0) {
                this.findTasks(0);
            } else {
                this.noTasks = true;
            }

        },
        methods: {
            findContributorId() {
                this.$store.dispatch('findContributor', {
                    userId: this.user.id,
                    projectId: this.getProject.id
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });

            },
            findTasks(offset) {
                this.$store.dispatch('findContributorTasks', {
                    contributorId: this.getContributorId,
                    offset: offset,
                    limit: this.perPage
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        for (let i = 0; i < this.getTasks.length; i++) {
                            this.tasks.push({
                                name: this.getTasks[i].name,
                                state: this.getTasks[i].taskState,
                                storyPoints: this.getTasks[i].storyPoints
                            });
                        }

                    }
                });
            },
            countTasks(){
                this.$store.dispatch('countContributorTasks', this.getContributorId).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });

                this.totalRows=this.getTasksCount;
            },
            loadNext(index) {
                this.findTasks(index);
            },
            close() {
                this.$emit('exit');
            }
        },
        computed: {
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getContributorId() {
                return this.$store.getters.getSelectedContributorId;
            },
            getProject() {
                return this.$store.getters.getProject;
            },
            getTasks() {
                return this.$store.getters.getContributorTasks;
            },
            getTasksCount(){
                return this.$store.getters.getUserTasksNum;
            }
        }
    }
</script>

<style scoped>

</style>
