<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>

        <b-row>
            <div v-if="noSprints">
                Current Project has no Sprints
            </div>
            <div v-if="!noSprints">
                <b-table
                        id="sprintTable"
                        hover
                        responsive
                        :items="sprints"
                        :fields="spFields"
                        :per-page="perPage"
                        :current-page="spCurrentPage">
                    <!--TODO: add button for deleteion -->
                    <!--TODO: add button for editing -->
                    <template v-slot:cell(sprintName)="data">
                        <b-button variant="link" @click="viewTasks(data.item.id)">{{data.item.sprintName}}</b-button>

                    </template>

                </b-table>
                <b-pagination
                        v-model="spCurrentPage"
                        :total-rows="totalSprints"
                        @input="loadNextSprints(spCurrentPage-1)"
                        :per-page="perPage"
                        aria-controls="sprintTable"/>
            </div>

        </b-row>
        <b-row v-if="showTasks">
            <div v-if="noTasks">
                Current Sprint has no tasks
            </div>

            <div v-if="!noTasks">
                <b-table
                        id="taskTable"
                        hover
                        responsive
                        :items="tasks"
                        :fields="tsFields"
                        :per-page="perPage"
                        :current-page="tsCurrentPage">
                </b-table>
                <b-pagination
                        v-model="tsCurrentPage"
                        :total-rows="totalTasks"
                        @input="loadNextTasks(tsCurrentPage-1)"
                        :per-page="perPage"
                        aria-controls="taskTable"/>
            </div>

        </b-row>
    </b-container>
</template>

<script>
    //TODO: check after task creation
    //TODO: reactive sprints list update after create
    //TODO: fix pagination offset everywhere
    export default {
        name: "ViewSprints",
        props: ['prId'],
        data() {
            return {
                sprints: [],
                tasks: [],
                errorMessage: '',
                errorOccurred: false,
                spFields: [
                    {key: 'sprintName', label: 'Sprint Name'},
                    {key: 'sprintStartDate', label: 'Start Date'},
                    {key: 'sprintEndDate', label: 'End Date'},
                ],
                tsFields: [
                    {key: 'taskName', label: 'Task Name'},
                    {key: 'taskStartDate', label: 'Start Date'},
                    {key: 'taskEndDate', label: 'End Date'},
                    {key: 'taskState', label: 'Status'},

                ],
                spCurrentPage: 1,
                tsCurrentPage: 1,
                perPage: 5,
                totalSprints: 0,
                totalTasks: 0,
                spId: 0,
                noSprints: false,
                noTasks: false,
                showTasks:false

            }
        },
        beforeMount() {
            this.countSprints();

            if (this.totalSprints > 0) {
                this.findSprints(0);
            } else {
                this.noSprints = true;
            }
        },
        methods: {
            loadNextSprints(index) {
                this.$store.commit('setSprints',[]);
                this.findSprints(this.calcOffset(index));
            },
            loadNextTasks(index) {
                    this.findTasks(index);

            },
            calcOffset(index){
                return index * this.perPage;
            },
            viewTasks(spId) {
                this.spId = spId;
                this.showTasks =true;

                this.countTasks();
                if (this.totalTasks > 0) {
                    this.findTasks(0);
                } else {
                    this.noTasks = true;
                }

            },
            findTasks(offset) {
                this.$store.dispatch('findSprintTasks', {
                    sprintId: this.spId,
                    offset: offset,
                    limit: this.perPage
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        for (let i = 0; i < this.getTasks.length; i++) {
                            this.tasks.push({
                                taskName: this.getTasks[i].name,
                                taskStartDate:
                                    this.formatDate(new Date(this.getTasks[i].startDate)),
                                taskEndDate:
                                    this.formatDate(new Date(this.getTasks[i].endDate)),
                                taskState: this.getTasks[i].taskState
                            });
                        }

                    }
                });
            },
            countTasks() {
                this.$store.dispatch('countSprintTasks', this.spId).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });

                this.totalTasks = this.getTasksCount;
            },


            findSprints(offset) {
                this.$store.dispatch('findSprints', {
                    projectId: this.prId,
                    offset: offset,
                    limit: this.perPage
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        for (let i = 0; i < this.getSprints.length; i++) {
                            this.sprints.push({
                                id: this.getSprints[i].id,
                                sprintName: this.getSprints[i].name,
                                sprintStartDate:
                                    this.formatDate(new Date(this.getSprints[i].startDate)),
                                sprintEndDate:
                                    this.formatDate(new Date(this.getSprints[i].endDate)),
                            });
                        }

                    }
                });
            },
            formatDate(rawDate) {
                let date = new Date(rawDate);

                let dd = date.getDate();
                let mm = date.getMonth() + 1;
                let yyyy = date.getFullYear();

                if (dd < 10) {
                    dd = '0' + dd;
                }

                if (mm < 10) {
                    mm = '0' + mm;
                }

                return dd + '/' + mm + '/' + yyyy;
            },

            countSprints() {
                this.$store.dispatch('countSprints', this.prId).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });

                this.totalSprints = this.getSprintsCount;
            },
        },
        computed: {
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getTasks() {
                return this.$store.getters.getSprintTasks;
            },
            getTasksCount() {
                return this.$store.getters.getSprintTasksNum;
            },
            getSprints() {
                return this.$store.getters.getSprints;
            },
            getSprintsCount() {
                return this.$store.getters.getSprintsCount;
            }
        }
    }
</script>

<style scoped>

</style>
