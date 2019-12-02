<template>
    <div id="root">
        <b-navbar type="dark" variant="dark">
            <b-navbar-nav tabs>
                <b-nav-item v-on:click="routeBack">{{project.name}}</b-nav-item>
            </b-navbar-nav>
            <NavbarRight></NavbarRight>
        </b-navbar>
        <b-container>
            <b-row id="buttonRow">
                <b-button id="viewSprints" v-b-modal.sprintsView>View Sprints</b-button>
                <b-button id="createSprint" @click="createSprint" variant="info">Create Sprint</b-button>
                <b-button id="addTask" @click="addTask" variant="primary">Add Task</b-button>

                <b-modal id="sprintsView"
                         ok-only
                         title="Project Sprints">
                    <ViewSprints :prId="project.id"></ViewSprints>
                </b-modal>
            </b-row>
        </b-container>
    </div>
</template>

<script>
    import NavbarRight from "./NavbarRight";
    import ViewSprints from "./ViewSprints";

    export default {
        name: "ProjectBoard",
        components: {ViewSprints, NavbarRight},
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
                }
            }
        },
        beforeMount() {
            this.project = this.getProject;
        },
        methods: {
            routeBack() {
                this.$router.push({path: '/project/' + this.$route.params.id});
            },
            createSprint() {

            },
            addTask() {

            }
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
