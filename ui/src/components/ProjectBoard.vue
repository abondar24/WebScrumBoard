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


            </b-row>
        </b-container>
    </div>
</template>

<script>
    import NavbarRight from "./NavbarRight";
    import ViewSprints from "./ViewSprints";
    import CreateEditSprint from "./CreateEditSprint";
    import CreateEditTask from "./CreateEditTask";

    export default {
        name: "ProjectBoard",
        components: {CreateEditTask, CreateEditSprint, ViewSprints, NavbarRight},
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
            }
        },
        beforeMount() {
            this.project = this.getProject;
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
        },
        computed: {
            getProject() {
                return this.$store.getters.getProject;
            },
        }
    }
</script>

<style scoped>
    #viewSprints, #createSprint, #addTask {
        margin-left: 10px;
    }

    #buttonRow {
        margin-top: 10px;
    }
</style>
