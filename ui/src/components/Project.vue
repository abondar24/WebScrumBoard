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
                       <b-img :src="image" alt="Project status"></b-img>
                   </b-row>
                   <b-row>
                       <h2>{{project.startDate}}</h2>
                       <h3 v-if="project.repository.length">{{project.repository}}</h3>
                   </b-row>
               </b-col>
                <b-col>

                </b-col>
            </b-row>
            <b-row>

            </b-row>

        </b-container>
    </div>
</template>

<script>
    import NavbarCommon from "./NavbarCommon";

    export default {
        name: "Project",
        components: {NavbarCommon},
        data(){
            return {
                project:{
                    id: 0,
                    name: '',
                    startDate: '',
                    endDate: '',
                    repository: '',
                    description: '',
                    active: false
                },
                isEditable: false,
                errorMessage: '',
                errorOccurred: false,
                image:null,
                ownerName:''
            }
        },
        beforeMount() {
                this.isEditable = false;
                this.$store.dispatch('findProject',this.$route.params.id).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.project=this.getProject;
                    }
                });

            this.$store.dispatch('findProjectOwner',this.$route.params.id).then(() => {
                this.errorMessage = this.getError;
                if (this.errorMessage.length) {
                    this.errorOccurred = true;
                } else if (this.getUser===this.getContributor.userId &&this.getContributor.owner) {
                   this.isEditable = true;
                   this.ownerName=this.getUser.firstName +' ' + this.getUser.lastName;
                }

            });

            if (this.project.active){
                this.image = require('@/assets/active.png');
            } else {
                this.image = require('@/assets/inactive.png');
            }

        },
        created() {
            this.$store.watch(
                (state,getters) => getters.getProject,
                (newVal,oldVal) =>{
                    this.project = newVal;
                }
            );
        },
        methods: {

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
            getContributor(){
                return this.$store.getters.getContributor;
            }
        }
    }
</script>

<style scoped>
 #topRow {
     margin-top: 10px;
 }
</style>
