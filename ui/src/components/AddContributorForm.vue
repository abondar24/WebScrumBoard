<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="warning">
            {{errorMessage}}
        </b-alert>
        <b-alert
                :show="userFound"
                dismissible
                variant="success">
           User found by login!!
        </b-alert>
        <b-form @submit.prevent="submit">
            <b-input-group id="loginGrp" class="mb-3">
                <b-input-group-text slot="prepend" size="sm">User login</b-input-group-text>
                <b-form-input v-model="login" :state="loginValidation"></b-form-input>

                <b-input-group-append>
                    <b-button variant="outline-primary" v-on:click="findUser">Find</b-button>
                </b-input-group-append>
            </b-input-group>

            <b-button type="submit" variant="primary" id="addButton">
                Add
            </b-button>
            <b-button  variant="danger" id="cancelButton" @click="cancel">
                Cancel
            </b-button>
        </b-form>
    </b-container>
</template>

<script>
    export default {
        name: "AddContributorForm",
        props: ['exit'],
        data(){
            return {
                errorMessage: '',
                errorOccurred: false,
                userId:0,
                login:'',
                userFound:false
            }
        },

        methods:{
            submit(){
                this.$store.dispatch('createContributor', {
                    userId: this.getUser.id,
                    projectId:  this.getProject.id,
                    owner: false
                }).then(()=>{
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;

                    } else {
                        this.$emit('exit');
                    }
                });
                this.userId=0;
            },
            findUser(){
                this.$store.dispatch('getUserByLogin',{
                    login: this.login,
                    isView:true
                }).then(()=>{
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;

                    } else {
                        this.userId = this.getUser.id;
                        this.userFound = true;
                    }
                });

                this.errorOccurred = false;
                this.userFound = false;
            },
            cancel() {
                this.$emit('exit');
            }
        },
        computed:{
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getProject(){
                return this.$store.getters.getProject;
            },
            loginValidation() {
                return this.login.length > 0 && this.login!=='deleted'
            },
            getUser(){
                return this.$store.getters.getViewUser;
            }
        }
    }
</script>

<style scoped>
   #cancelButton{
       margin-left: 10px;
   }
</style>
