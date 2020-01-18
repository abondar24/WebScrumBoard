<template>
    <div id="root">
        <NavbarCommon/>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>
        <b-container>
            <b-row>
                <b-button id="createProject" variant="success" v-b-modal.createProject>
                    {{ $t('project_create') }}
                </b-button>
                <b-modal
                        id="createProject"
                        ref="projectCreate"
                        v-bind:title="$t('project_create')"
                        hide-footer>
                    <CreateProjectForm @exit="hideCreate"></CreateProjectForm>
                </b-modal>

            </b-row>
            <b-row>
                <b-table
                         hover
                         responsive
                         :items="projects"
                         :fields="fields"
                         caption-top
                         @row-clicked="routeToProject($event)">
                    <template v-slot:table-caption> {{ $t('user_projects') }}</template>
                </b-table>
            </b-row>


        </b-container>

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
                fields: [
                    {key:'name',label:''}
                ],
            }
        },
        beforeMount() {
            this.projects = [];
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

                this.errorMessage = '';
                this.errorOccurred = false;
            },
            routeToProject(project){
              this.$router.push({path: '/project/' + project.id});
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
    #tableRow {
        margin-top: 10px;
        margin-left: 20px;
    }

    #createProject {
        margin-left: 10px;
        margin-top: 30px;
    }
</style>
