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
            <b-row id="topRow">
               <b-col>
                   <b-row>
                       <h1>{{project.name}}</h1>
                       <b-img id="activeImg" :src="image"  v-bind="imgProps" alt="Project status"></b-img>
                   </b-row>
                   <b-row>
                       <h2>Started on: {{project.startDate}}</h2>
                       <h2 v-if="!project.active">Finished on: {{project.endDate}}</h2>
                       <a v-if="project.repository!==null && project.repository.length" href="{{project.repository}}"></a>
                   </b-row>
               </b-col>
                <b-col>
                    <b-row>
                       <h2>Owner: {{ownerName}}</h2>
                    </b-row>
                    <b-row v-if="isEditable">
                        <b-button v-b-modal.editProject>Edit project</b-button>
                        <b-button id="deleteProject"  v-b-modal.delPrj variant="danger">Delete project</b-button>

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
                    </b-row>
                </b-col>
            </b-row>
            <b-row>
               <p>{{project.description}}</p>
            </b-row>

        </b-container>
    </div>
</template>

<script>
    import NavbarCommon from "./NavbarCommon";
    import EditProjectForm from "./EditProjectForm";

    export default {
        name: "Project",
        components: {EditProjectForm, NavbarCommon},
        data(){
            return {
                project:{
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
                image:null,
                ownerName:'',
                imgProps: {width: 20, height: 20, class: 'm1'},
            }
        },
        beforeMount() {
            this.findProject();
            this.findOwner();
            this.project=this.getProject;
            this.setDate();
            this.setImage();

        },
        created() {
            this.$store.watch(
                (state,getters) => getters.getProject,
                (newVal,oldVal) =>{
                    this.project = newVal;
                    this.setDate();
                    this.setImage();
                }
            );



        },
        methods: {
            findProject(){
                this.$store.dispatch('findProject',this.$route.params.id).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });
            },
            findOwner(){
                this.$store.dispatch('findProjectOwner',this.$route.params.id).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }

                });
                if (this.getUser.id===this.getOwner.id) {
                    this.isEditable = true;
                }

                this.ownerName=this.getOwner.firstName +' ' + this.getOwner.lastName;
            },
             setDate(){

                 this.project.startDate = this.formatDate(new Date(this.project.startDate));
                 if (this.project.endDate!==0){
                     this.project.endDate = this.formatDate(new Date(this.getProject.endDate));
                 }

             },
             setImage(){
                 if (this.project.active){
                     this.image = require('@/assets/active.png');
                 } else {
                     this.image = require('@/assets/inactive.png');
                 }
             },
            formatDate(rawDate){
                let date = new Date(rawDate);

                let dd = date.getDate();
                let mm = date.getMonth()+1;
                let yyyy = date.getFullYear();

                if(dd<10) {
                    dd='0'+dd;
                }

                if(mm<10) {
                    mm='0'+mm;
                }

                return dd+'/'+mm+'/'+yyyy;
            },
            delProject(){
                this.$store.dispatch('deleteProject',this.project.id).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.$router.push('/projects')
                    }
                });
            },
            hideEdit(){
                this.$refs['prjEdit'].hide();
            }
        },
        computed: {
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getProject(){
                return this.$store.getters.getProject;
            },
            getUser() {
                return this.$store.getters.getUser;
            },
            getOwner(){
                return this.$store.getters.getProjectOwner;
            },
            getViewUser() {
                return this.$store.getters.getUser;
            },
        }
    }
</script>

<style scoped>
 #topRow {
     margin-top: 30px;
 }
    #activeImg,#deleteProject {
        margin-left: 10px;
    }
</style>
