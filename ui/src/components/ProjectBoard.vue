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

                <!--                <b-form-select v-model="selectedSprintId" :options="sprints" @change="loadSprint" size="sm"-->
                <!--                               class="mt-3"></b-form-select>-->
                <b-button id="viewSprints" v-b-modal.sprintsView>View Sprints</b-button>
                <b-button id="createSprint" v-b-modal.sprintCreate variant="info">Create Sprint</b-button>
                <b-button id="addTask" v-b-modal.taskCreate variant="primary">Add Task</b-button>
                <b-button id="backlog" v-b-modal.taskBacklog variant="warning">Tasks Backlog</b-button>

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


            </b-row>
            <b-row id="sprintRow">

                <b-col md="4">
                    <h3>CurrentSprint: {{this.getCurrentSprint.name}}</h3>

                </b-col>
                <b-col >
                    <h3>Period: {{this.currentSprintStartDate}} - {{this.currentSprintEndDate}}</h3>

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

    export default {
        name: "ProjectBoard",
        components: {TasksBacklog, CreateEditTask, CreateEditSprint, ViewSprints, NavbarRight},
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
                selectedSprintId: 0,
                currentSprintStartDate: '',
                currentSprintEndDate: '',
                sprints: [],
            }
        },
        beforeMount() {
            this.project = this.getProject;

            this.$store.dispatch('findSprints',
                {
                    projectId: this.getProjectId,
                    offset: 0
                }).then(() => {
                this.errorMessage = this.getError;
                if (this.errorMessage.length) {
                    this.errorOccurred = true;
                } else {

                    for (let i = 0; i < this.getSprints.length; i++) {
                        this.sprints.push({
                            value: this.getSprints[i].id,
                            text: this.getSprints[i].name
                        })
                    }
                }
            });

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
            loadSprint() {

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
            getSprints() {
                return this.$store.getters.getSprints;
            }
        }
    }
</script>

<style scoped>
    #viewSprints, #createSprint, #addTask, #backlog {
        margin-left: 10px;
    }

    #buttonRow {
        margin-top: 10px;
    }
</style>
