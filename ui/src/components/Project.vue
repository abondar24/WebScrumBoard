<template>
    <div id="root">
        <NavbarCommon>
            <b-button variant="success"> Vue sprints</b-button>
        </NavbarCommon>
        <b-container>
            <b-alert
                    :show="errorOccurred"
                    dismissible
                    variant="danger">
                {{errorMessage}}
            </b-alert>
            <b-alert
                    :show="ownerChanged"
                    dismissible
                    variant="warning">
                Project Owner has been changed
            </b-alert>
            <b-row id="topRow">
                <b-col>
                    <b-row>
                        <h1>{{project.name}}</h1>
                        <b-img id="activeImg" :src="image" v-bind="imgProps" alt="Project status"></b-img>
                    </b-row>
                    <b-row>
                        <p>Started on: {{project.startDate}}</p>
                    </b-row>
                    <b-row>
                        <p v-if="!project.active">Finished on: {{project.endDate}}</p>
                    </b-row>
                    <b-row>
                        <b-link v-bind:href="project.repository"
                                v-if="project.repository!==null && project.repository.length">
                            Project repository
                        </b-link>
                    </b-row>
                </b-col>
                <b-col>
                    <b-row>
                        <h2>Owner: {{ownerName}}</h2>
                    </b-row>
                    <b-row>
                        <div v-if="isEditable">
                            <b-button v-if="project.active" v-b-modal.editProject>Edit project</b-button>
                            <b-button id="addCtr" v-b-modal.addContributor variant="success" v-if="project.active">Add
                                Contributor
                            </b-button>

                            <b-button id="deleteProject" v-b-modal.delPrj variant="danger">Delete project</b-button>
                            <b-modal
                                    id="delPrj"
                                    title="Delete project"
                                    ok-variant="danger"
                                    ok-title="yes"
                                    @ok="delProject"
                                    cancel-title="no">
                                Are you sure you want to delete project?
                            </b-modal>
                            <b-modal id="editProject"
                                     ref="prjEdit"
                                     hide-footer
                                     title="Edit project">
                                <EditProjectForm @exit="hideEdit"></EditProjectForm>
                            </b-modal>
                            <b-modal
                                    id="addContributor"
                                    title="Add contributor"
                                    ref="ctrAdd"
                                    hide-footer>
                                <AddContributorForm @exit="hideCtr"></AddContributorForm>
                            </b-modal>
                        </div>

                    </b-row>
                </b-col>
            </b-row>
            <b-row>
                <p>{{project.description}}</p>
            </b-row>
            <b-row>
                <b-table
                        id="ctrTable"
                        hover
                        responsive
                        :items="ctrItems"
                        :fields="fields"
                        :per-page="perPage"
                        caption-top
                        :current-page="currentPage">
                    <template v-slot:table-caption>Project contributors</template>
                    <template v-slot:cell(ctr_name)="data">
                        <router-link :to="{ name: 'User', params: { id: data.item.id }}">
                            {{data.item.ctr_name}}
                        </router-link>
                    </template>
                    <template v-slot:cell(ctr_actions)="data">
                        <b-button-group>
                            <div v-if="isEditable">
                                <b-button variant="warning" @click="makeAsOwner(data.item)">Make as owner</b-button>
                                <b-button variant="danger" @click="deleteContributor(data.item)">Delete</b-button>
                            </div>
                            <b-button variant="success" @click="showTasks(data.item)">View tasks</b-button>
                        </b-button-group>
                    </template>

                </b-table>
                <b-pagination
                        v-model="currentPage"
                        :total-rows="totalRows"
                        @input="loadNext(currentPage-1)"
                        :per-page="perPage"
                        aria-controls="ctrTable"/>
            </b-row>
            <b-row v-if="tasksToShow">
                <ContributorTasks @exit="hideTasks" :user="selectedCtr"></ContributorTasks>

            </b-row>

        </b-container>
    </div>
</template>

<script>
    import NavbarCommon from "./NavbarCommon";
    import EditProjectForm from "./EditProjectForm";
    import AddContributorForm from "./AddContributorForm";
    import ContributorTasks from "./ContributorTasks";

    export default {
        name: "Project",
        components: {ContributorTasks, AddContributorForm, EditProjectForm, NavbarCommon},
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
                isEditable: false,
                errorMessage: '',
                errorOccurred: false,
                image: null,
                ownerName: '',
                imgProps: {width: 20, height: 20, class: 'm1'},
                ctrItems: [],
                currentPage: 1,
                perPage: 10,
                totalRows: 0,
                fields: [
                    {key: 'ctr_name', label: ''},
                    {key: 'ctr_actions', label: ''},
                ],
                ownerChanged: false,
                tasksToShow:false,
                selectedCtr:{}
            }
        },
        beforeMount() {
            this.findProject();
            this.findOwner();
            this.countContributors();
            this.findContributors(0);

            this.setDate();
            this.setImage();
        },
        created() {
            this.$store.watch(
                (state, getters) => getters.getProject,
                (newVal, oldVal) => {
                    this.project = newVal;
                    this.setDate();
                    this.setImage();
                }
            );

        },
        methods: {
            findProject() {
                this.$store.dispatch('findProject', this.$route.params.id).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });

                this.project = this.getProject;
            },
            findOwner() {
                this.$store.dispatch('findProjectOwner', this.$route.params.id).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        if (this.getUser.id === this.getOwner.id) {
                            this.isEditable = true;
                        }
                        this.ownerName = this.getOwner.firstName + ' ' + this.getOwner.lastName;
                    }

                });

            },
            findContributors(offset) {
                this.$store.dispatch('findProjectContributors', {
                    projectId: this.$route.params.id,
                    offset: offset,
                    limit: this.perPage
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {

                        for (let i = 0; i < this.getContributors.length; i++) {
                            this.ctrItems.push({
                                id: this.getContributors[i].id,
                                ctr_name: this.getContributors[i].firstName +
                                    ' ' + this.getContributors[i].lastName
                            });
                        }

                    }
                });
            },
            countContributors(){
                this.$store.dispatch('countProjectContributors', this.$route.params.id).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });

                this.totalRows=this.getCount;
            },
            setDate() {

                this.project.startDate = this.formatDate(new Date(this.project.startDate));
                if (this.project.endDate !== 0) {
                    this.project.endDate = this.formatDate(new Date(this.getProject.endDate));
                }

            },
            setImage() {
                if (this.project.active) {
                    this.image = require('@/assets/active.png');
                } else {
                    this.image = require('@/assets/inactive.png');
                }
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
            delProject() {
                this.$store.dispatch('deleteProject', this.project.id).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.$router.push('/projects')
                    }
                });
            },
            hideEdit() {
                this.$refs['prjEdit'].hide();
            },
            hideCtr() {
                this.$refs['ctrAdd'].hide();
                this.ctrItems.push({
                    id: this.getViewUser.id,
                    ctr_name: this.getViewUser.firstName +
                        ' ' + this.getViewUser.lastName
                })
            },
            showTasks(user) {
               this.tasksToShow=true;
               this.selectedCtr=user;
            },
            hideTasks() {
                this.tasksToShow=false;
                this.selectedCtr={};
            },
            loadNext(index) {
                this.findContributors(index);
            },
            makeAsOwner(ctrData){
                this.$store.dispatch('updateContributor', {
                    userId: ctrData.id,
                    projectId: this.getProject.id,
                    owner:true,
                    active:true
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.ownerChanged = true;
                    }
                });
            },

            deleteContributor(ctrData){
                this.$store.dispatch('updateContributor', {
                    userId: ctrData.id,
                    projectId: this.getProject.id,
                    active:false
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                       let filteredContributors = this.getContributors.filter(function(value, index, arr){
                           return value.userId !==ctrData.id;
                       });

                       this.$store.commit('setContributors',filteredContributors);

                       this.ctrItems = this.ctrItems.filter(function(value, index, arr){
                           return value.id !==ctrData.id;
                       });

                    }
                });
            },
        },
        computed: {
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getProject() {
                return this.$store.getters.getProject;
            },
            getUser() {
                return this.$store.getters.getUser;
            },
            getOwner() {
                return this.$store.getters.getProjectOwner;
            },
            getViewUser() {
                return this.$store.getters.getViewUser;
            },
            getContributors() {
                return this.$store.getters.getProjectContributors;
            },
            getCount(){
                return this.$store.getters.getContributorCount;
            }
        }
    }
</script>

<style scoped>
    #topRow {
        margin-top: 30px;
    }

    #activeImg, #deleteProject, #addCtr {
        margin-left: 10px;
    }
</style>
