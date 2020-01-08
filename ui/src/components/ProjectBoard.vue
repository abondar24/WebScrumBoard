<template>
    <div id="root">
        <b-navbar type="dark" variant="dark">
            <b-navbar-nav tabs>
                <b-nav-item v-on:click="routeBack">{{project.name}}</b-nav-item>
            </b-navbar-nav>
            <NavbarRight></NavbarRight>
        </b-navbar>
        <b-container>
            <b-alert
                    :show="errorOccurred"
                    dismissible
                    variant="danger">
                {{errorMessage}}
            </b-alert>

            <b-row id="buttonRow">


                <b-button id="viewSprints" v-b-modal.sprintsView>View Sprints</b-button>
                <b-button id="createSprint" v-b-modal.sprintCreate variant="info">Create Sprint</b-button>
                <b-button id="addTask" v-b-modal.taskCreate variant="primary">Add Task</b-button>
                <b-button id="backlog" v-b-modal.taskBacklog variant="warning">Tasks Backlog</b-button>
                <b-button id="endSprint" v-b-modal.sprintEnd variant="danger">End sprint</b-button>

                <b-modal id="sprintsView"
                         ok-only
                         size="lg"
                         title="Project Sprints">
                    <ViewSprints :prId="project.id"></ViewSprints>
                </b-modal>


                <b-modal id="sprintCreate"
                         ref="spCreate"
                         title="Create Sprint"
                         hide-footer>
                    <CreateEditSprint :id="project.id" :isEdit="false" @exit="hideCreate"></CreateEditSprint>
                </b-modal>

                <b-modal id="taskCreate"
                         ref="tsCreate"
                         title="Create Task"
                         hide-footer>
                    <CreateEditTask :isEdit="false" @exit="hideTaskCreate"></CreateEditTask>
                </b-modal>


                <b-modal id="taskBacklog" ref="tsBacklog"
                         title="Backlog">
                    <TasksBacklog></TasksBacklog>
                </b-modal>

                <b-modal id="sprintEnd" ref="spEnd" title="End Sprint" hide-footer>
                    <EndSprint @exit="endSprint"></EndSprint>
                </b-modal>

            </b-row>
            <b-row id="sprintRow">

                <b-col md="4">
                    <h3>CurrentSprint: {{this.getCurrentSprint.name}}</h3>

                </b-col>
                <b-col>
                    <h3>Period: {{this.currentSprintStartDate}} - {{this.currentSprintEndDate}}</h3>

                </b-col>
            </b-row>
            <b-row id="board">
                <b-col col-3 id="created">
                    <h3>Created</h3>
                    <draggable v-model="createdTasks" group="tasks" @start="drag=true" @end="drag=false">
                        <div class="list-group-item" v-for="ts in createdTasks" :key="ts.id">{{ts.taskName}}</div>
                    </draggable>
                </b-col>

                <b-col col-3 id="develop">
                    <h3>In Development</h3>
                    <draggable v-model="devTasks" group="tasks" @start="drag=true" @end="drag=false"
                               @change="changeToInDevelopment">
                        <div class="list-group-item" v-for="ts in devTasks" :key="ts.id">{{ts.taskName}}</div>
                    </draggable>
                </b-col>

                <b-col col-3 id="review">
                    <h3>In Code review</h3>
                    <draggable v-model="reviewTasks" group="tasks" @start="drag=true" @end="drag=false"
                               @change="changeToInCodeReview">
                        <div class="list-group-item" v-for="ts in reviewTasks" :key="ts.id">{{ts.taskName}}</div>
                    </draggable>
                </b-col>

                <b-col col-3 id="test">
                    <h3>In Test</h3>
                    <draggable v-model="testTasks" group="tasks" @start="drag=true" @end="drag=false"
                               @change="changeToInTest">
                        <div class="list-group-item" v-for="ts in testTasks" :key="ts.id">{{ts.taskName}}</div>
                    </draggable>
                </b-col>

                <b-col col-3 id="deploy">
                    <h3>In Deployment</h3>
                    <draggable v-model="deployTasks" group="tasks" @start="drag=true" @end="drag=false"
                               @change="changeToInDeployment">
                        <div class="list-group-item" v-for="ts in deployTasks" :key="ts.id">{{ts.taskName}}</div>
                    </draggable>
                </b-col>

                <b-col col-3 id="completed">
                    <h3>Completed</h3>
                    <draggable v-model="completedTasks" group="tasks" @start="drag=true" @end="drag=false"
                               @change="changeToCompleted">
                        <div class="list-group-item" v-for="ts in completedTasks" :key="ts.id">{{ts.taskName}}</div>
                    </draggable>
                </b-col>

                <b-col col-3 id="pause">
                    <h3>Paused</h3>
                    <draggable v-model="pausedTasks" group="tasks" @start="drag=true" @end="drag=false"
                               @change="changeToPaused">
                        <div class="list-group-item" v-for="ts in pausedTasks" :key="ts.id">{{ts.taskName}}</div>
                    </draggable>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script>
    import NavbarRight from "./NavbarRight";
    import ViewSprints from "./ViewSprints";
    import CreateEditSprint from "./CreateEditSprint";
    import CreateEditTask from "./CreateEditTask";
    import TasksBacklog from "./TasksBacklog";
    import EndSprint from "./EndSprint";
    import draggable from "vuedraggable";

    export default {
        name: "ProjectBoard",
        components: {EndSprint, TasksBacklog, CreateEditTask, CreateEditSprint, ViewSprints, NavbarRight, draggable},
        data() {
            return {
                project: {
                    id: 0,
                    name: '',
                    startDate: null,
                    endDate: null,
                    repository: '',
                    description: '',
                    active: false
                },
                errorMessage: '',
                errorOccurred: false,
                currentSprintStartDate: '',
                currentSprintEndDate: '',
                taskStates: ['CREATED', 'IN_DEVELOPMENT', 'PAUSED', 'IN_CODE_REVIEW',
                    'IN_TEST', 'IN_DEPLOYMENT', 'COMPLETED'],
                createdTasks: [],
                devTasks: [],
                pausedTasks: [],
                reviewTasks: [],
                testTasks: [],
                deployTasks: [],
                completedTasks: []
            }
        },
        beforeMount() {
            this.project = this.getProject;
            this.loadCurrentSprint();

            this.$store.dispatch('findSprintTasks', {
                sprintId: this.getCurrentSprint.id,
                offset: 0,
                limit: null
            }).then(() => {
                this.errorMessage = this.getError;
                if (this.errorMessage.length) {
                    this.errorOccurred = true;
                }
            });

            let tasks = this.getTasks;
            for (let i = 0; i < tasks.length; i++) {
                switch (tasks[i].taskState) {
                    case this.taskStates[0]:
                        this.createdTasks.push(tasks[i]);
                        break;

                    case this.taskStates[1]:
                        this.devTasks.push(tasks[i]);
                        break;

                    case this.taskStates[2]:
                        this.pausedTasks.push(tasks[i]);
                        break;

                    case this.taskStates[3]:
                        this.reviewTasks.push(tasks[i]);
                        break;

                    case this.taskStates[4]:
                        this.testTasks.push(tasks[i]);
                        break;

                    case this.taskStates[5]:
                        this.deployTasks.push(tasks[i]);
                        break;

                    case this.taskStates[6]:
                        this.completedTasks.push(tasks[i]);
                        break;
                }


            }
        },
        created() {
            this.$store.watch(
                (state, getters) => getters.getCurrentSprint,
                (newVal, oldVal) => {

                }
            );

        },
        methods: {
            routeBack() {
                this.$router.push({path: '/project/' + this.$route.params.id});
            },
            hideCreate() {
                this.$refs['spCreate'].hide();
            },
            hideTaskCreate() {
                this.$refs['tsCreate'].hide();
            },
            endSprint() {
                this.$refs['spEnd'].hide();
                this.loadCurrentSprint();
            },
            changeToInDevelopment(ev) {
                let oldArr = [];
                if (ev.added) {
                    if (ev.added.element.prevState === this.taskStates[2]) {
                        oldArr = this.pausedTasks;
                    } else {
                        oldArr = this.createdTasks;
                    }
                    this.updateState(ev.added.element, this.taskStates[1], oldArr, this.testTasks);
                }


            },
            changeToInCodeReview(ev) {
                let oldArr = [];
                if (ev.added) {
                    if (ev.added.element.prevState === this.taskStates[2]) {
                        oldArr = this.pausedTasks;
                    } else {
                        oldArr = this.devTasks;
                    }
                    this.updateState(ev.added.element, this.taskStates[3], oldArr, this.testTasks);
                }

            },
            changeToInTest(ev) {
                let oldArr = [];
                if (ev.added) {
                    if (ev.added.element.prevState === this.taskStates[2]) {
                        oldArr = this.pausedTasks;
                    } else {
                        oldArr = this.reviewTasks;
                    }
                    this.updateState(ev.added.element, this.taskStates[4], oldArr, this.testTasks);
                }

            },
            changeToInDeployment(ev) {
                let oldArr = [];
                if (ev.added) {
                    if (ev.added.element.prevState === this.taskStates[2]) {
                        oldArr = this.pausedTasks;
                    } else {
                        oldArr = this.testTasks;
                    }
                    this.updateState(ev.added.element, this.taskStates[5], oldArr, this.testTasks);
                }

            },
            changeToCompleted(ev) {
                let oldArr = [];
                if (ev.added) {
                    if (ev.added.element.prevState === this.taskStates[2]) {
                        oldArr = this.pausedTasks;
                    } else {
                        oldArr = this.deployTasks;
                    }
                    this.updateState(ev.added.element, this.taskStates[6], oldArr, this.testTasks);
                }

            },
            changeToPaused(ev) {
                let oldArr = [];

                if (ev.added) {
                    switch (ev.added.element.prevState) {
                        case this.taskStates[0]:
                            oldArr = this.createdTasks;
                            break;

                        case this.taskStates[1]:
                            oldArr = this.devTasks;
                            break;

                        case this.taskStates[3]:
                            oldArr = this.reviewTasks;
                            break;

                        case this.taskStates[4]:
                            oldArr = this.testTasks;
                            break;

                        case this.taskStates[5]:
                            oldArr = this.deployTasks;
                            break;

                    }

                    this.updateState(ev.added.element, this.taskStates[2], oldArr, this.pausedTasks);
                }

            },
            updateState(task, newState, oldArr, newArr) {
                this.$store.dispatch('updateTaskState', {
                    id: task.id,
                    state: newState
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        newArr.push(task);
                        oldArr.splice(oldArr.findIndex(function (i) {
                            return i.id === task.id;
                        }), 1);
                    }
                });

                this.errorMessage = '';
                this.errorOccurred = false;

            },

            loadCurrentSprint() {

                this.$store.dispatch('findCurrentSprint', this.getProjectId).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.setDate();
                    }
                });

                this.errorMessage = '';
                this.errorOccurred = false;

            },
            setDate() {

                this.currentSprintStartDate = this.formatDate(new Date(this.getCurrentSprint.startDate));
                this.currentSprintEndDate = this.formatDate(new Date(this.getCurrentSprint.endDate));

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
        },
        computed: {
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getProject() {
                return this.$store.getters.getProject;
            },
            getProjectId() {
                return this.$store.getters.getProjectId;
            },
            getCurrentSprint() {
                return this.$store.getters.getCurrentSprint;
            },
            getTasks() {
                return this.$store.getters.getSprintTasks;
            }
        }
    }
</script>

<style scoped>
    #viewSprints, #createSprint, #addTask, #backlog, #endSprint {
        margin-left: 10px;
    }

    #buttonRow {
        margin-top: 10px;
    }

    #created {
        background-color: #42b983;
    }

    #develop {
        background-color: #0074D9;
    }

    #pause {
        background-color: #cc0000;
    }

    #review {
        background-color: #ffc520;
    }

    #test {
        background-color: darkgray;
    }

    #deploy {
        background-color: azure;
    }

    #completed {
        background-color: #ffdddd;
    }

    #board {
        margin-top: 100px;
    }

</style>
