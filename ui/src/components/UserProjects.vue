<template>
    <div id="root">
        <NavbarCommon/>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>
        <b-button v-b-toggle.projects id="projectButton">
            User projects
        </b-button>
        <b-button id="createProject" variant="success" v-b-modal.createProject>
            Create project
        </b-button>
        <b-modal
                id="createProject"
                ref="projectCreate"
                title="Create project"
                hide-footer>
            <CreateProjectForm @exit="hideCreate"></CreateProjectForm>
        </b-modal>

        <b-collapse id="projects" class="mt-2">
            <b-card>
                <div v-for="(project, index) in projects">
                    <router-link :to="{ name: 'Project', params: { id: project.id}} ">
                        <b-button variant="link">{{ project.name }}</b-button>
                    </router-link>

                </div>
            </b-card>
        </b-collapse>

    </div>
</template>

<script>
    import NavbarCommon from "./NavbarCommon";
    import CreateProjectForm from "./CreateProjectForm";

    export default {
        name: "UserProjects",
        components: {CreateProjectForm, NavbarCommon},
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                projects: [],

            }
        },
        beforeMount() {
            this.getUserProjects();
        },
        methods: {
            hideCreate() {
                this.$refs['projectCreate'].hide();
                this.getUserProjects();
            },
            getUserProjects() {
                this.$store.dispatch('findUserProjects', this.getId).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.projects = this.getProjects;
                    }
                });
            }
        },
        computed: {
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getId() {
                return this.$store.getters.getUserId;
            },
            getProjects() {
                return this.$store.getters.getUserProjects;
            }
        }
    }
</script>

<style scoped>
    #projectButton {
        margin-top: 30px;
    }

    #createProject {
        margin-left: 10px;
        margin-top: 30px;
    }
</style>
