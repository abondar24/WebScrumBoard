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
                {{$t('project_owner_chg')}}
            </b-alert>
            <b-row id="topRow">
                <b-col>
                    <b-row>
                        <h1>{{project.name}}</h1>
                        <b-img id="activeImg" :src="image" v-bind="imgProps" alt="Project status"></b-img>
                    </b-row>
                    <b-row>
                        <p>{{$t('started_on')}} {{project.startDate}}</p>
                    </b-row>
                    <b-row>
                        <p v-if="!project.active">{{$t('finished_on')}} {{project.endDate}}</p>
                    </b-row>
                    <b-row>
                        <b-link v-bind:href="project.repository"
                                v-if="project.repository!==null && project.repository.length">
                            {{$t('project_repo')}}
                        </b-link>
                    </b-row>
                </b-col>
                <b-col>
                    <b-row>
                        <h2>{{$t('project_own')}} {{ownerName}}</h2>
                    </b-row>
                    <b-row>
                        <router-link :to="{ name: 'ProjectBoard', params: { id: this.getProject.id }}">
                            <b-button variant="primary">{{$t('board_view')}}</b-button>
                        </router-link>

                        <div v-if="isEditable">
                            <b-button id="editPrj" v-if="project.active" v-b-modal.editProject>
                                {{$t('project_edit')}}
                            </b-button>
                            <b-button id="addCtr" v-b-modal.addContributor variant="success" v-if="project.active">
                                {{$t('ctr_add')}}
                            </b-button>

                            <b-button id="deleteProject" v-b-modal.delPrj variant="danger">
                                {{$t('project_delete')}}
                            </b-button>
                            <b-modal
                                    id="delPrj"
                                    v-bind:title="$t('project_delete')"
                                    ok-variant="danger"
                                    v-bind:ok-title="$t('yes')"
                                    @ok="delProject"
                                    v-bind:cancel-title="$t('no')">
                                {{$t('project_delete_conf')}}
                            </b-modal>
                            <b-modal id="editProject"
                                     ref="prjEdit"
                                     hide-footer
                                     v-bind:title="$t('project_edit')">
                                <EditProjectForm @exit="hideEdit"></EditProjectForm>
                            </b-modal>
                            <b-modal
                                    id="addContributor"
                                    v-bind:title="$t('ctr_add')"
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
                    <template v-slot:table-caption> {{$t('project_ctrs')}}</template>
                    <template v-slot:cell(ctr_name)="data">
                        <router-link :to="{ name: 'User', params: { id: data.item.id }}">
                            {{data.item.ctr_name}}
                        </router-link>
                    </template>
                    <template v-slot:cell(ctr_actions)="data">
                        <b-button-group>
                            <div v-if="isEditable">
                                <b-button variant="warning" @click="makeAsOwner(data.item)">
                                    {{$t('owner_make')}}
                                </b-button>
                                <b-button variant="danger" @click="deleteContributor(data.item)">
                                    {{$t('delete')}}
                                </b-button>
                            </div>
                            <b-button variant="success" @click="showTasks(data.item)">
                                {{$t('tasks_view')}}
                            </b-button>
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
                tasksToShow: false,
                selectedCtr: {},
                ctrOffsets:[],
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


            this.$store.watch(
                (state, getters) => getters.getCount,
                (newVal, oldVal) => {
                    this.totalRows = newVal;
                    this.cleanCtrOffsets();
                }
            );

        },
        methods: {
            cleanCtrOffsets(){
                if ((this.perPage * this.ctrOffsets.length)-this.totalRows>=5){
                    this.ctrOffsets.pop();
                }
            },
            findProject() {
                this.$store.dispatch('findProject', this.$route.params.id).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });

                this.project = this.getProject;
                this.errorMessage = '';
                this.errorOccurred = false;
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

                    this.$store.commit('setProjectEditable',this.isEditable);
                });
                this.errorMessage = '';
                this.errorOccurred = false;

            },
            findContributors(offset) {
                if (!this.ctrOffsets.includes(offset)){
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
                            this.ctrOffsets.push(offset);
                        }
                    });
                }

                this.errorMessage = '';
                this.errorOccurred = false;
            },
            countContributors() {
                this.$store.dispatch('countProjectContributors', this.$route.params.id).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });

                this.totalRows = this.getCount;
                this.errorMessage = '';
                this.errorOccurred = false;
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
                this.tasksToShow = true;
                this.selectedCtr = user;
            },
            hideTasks() {
                this.tasksToShow = false;
                this.selectedCtr = {};
            },
            loadNext(index) {
                this.findContributors(index * this.perPage);
            },
            makeAsOwner(ctrData) {
                this.$store.dispatch('updateContributor', {
                    userId: ctrData.id,
                    projectId: this.getProject.id,
                    owner: true,
                    active: true
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.ownerChanged = true;
                    }
                });
            },

            deleteContributor(ctrData) {
                this.$store.dispatch('updateContributor', {
                    userId: ctrData.id,
                    projectId: this.getProject.id,
                    active: false
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        let filteredContributors = this.getContributors.filter(function (value, index, arr) {
                            return value.userId !== ctrData.id;
                        });

                        this.$store.commit('setContributors', filteredContributors);

                        this.ctrItems = this.ctrItems.filter(function (value, index, arr) {
                            return value.id !== ctrData.id;
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
            getCount() {
                return this.$store.getters.getContributorCount;
            }
        }
    }
</script>

<style scoped>
    #topRow {
        margin-top: 30px;
    }

    #activeImg, #deleteProject, #addCtr, #editPrj {
        margin-left: 10px;
    }
</style>
