<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="warning">
            {{errorMessage}}
        </b-alert>
        <b-form @submit.prevent="submit">

            <b-form-group
                    id="fname"
                    label="First Name"
                    label-for="fnameInput">
                <b-form-input
                        id="fnameInput"
                        v-model="updUser.firstName"
                        placeholder="Enter first name">
                </b-form-input>
            </b-form-group>

            <b-form-group
                    id="lname"
                    label="Last Name"
                    label-for="lnameInput">
                <b-form-input
                        id="lnameInput"
                        v-model="updUser.lastName"
                        placeholder="Enter first name">
                </b-form-input>
            </b-form-group>


            <b-form-group
                    id="email"
                    label="Email address"
                    label-for="emailInput">
                <b-form-input
                        id="emailInput"
                        v-model="updUser.email"
                        type="email"
                        placeholder="Enter email">
                </b-form-input>
            </b-form-group>

            <b-form-group
                    id="roles"
                    label="User roles"
                    label-for="roleInput">
                <b-form-select
                        id="roleInput"
                        v-model="selectedRoles"
                        aria-describedby="password-help-block"
                        :options="roles" multiple
                        :select-size="3"></b-form-select>
            </b-form-group>

<!--            TODO:user delete modal and rest call-->
            <b-alert :show="true" variant="danger">
                <h2>DANGER ZONE !</h2>
                <b-button v-on:click="deleteUser">Delete User</b-button>
            </b-alert>

            <b-button type="submit" variant="primary" id="editButton">Edit</b-button>
            <b-button variant="danger" id="cancelButton" @click="cancel">Cancel</b-button>

        </b-form>
    </b-container>
</template>

<script>
    export default {
        name: "EditUserForm",
        props: ['exit'],
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                updUser: {
                    firstName: '',
                    lastName: '',
                    email: '',
                    roles: ''
                },
                roles: [
                    {value: 'DEVELOPER', text: 'Developer'},
                    {value: 'QA', text: 'QA'},
                    {value: 'DEV_OPS', text: 'DevOps'}
                ],
                selectedRoles: [],
                dataUpdated: false
            }
        },
        methods:{
            submit(){
                if (this.selectedRoles.length!==0){
                    this.updUser.roles = this.selectedRoles.join(";");
                }

                this.$store.dispatch('updateUser', this.updUser).then(() => {
                    this.errorMessage = this.getError;

                    if (this.errorMessage.length) {
                        this.errorOccurred = true;

                    } else {
                        this.updUser = {
                            email: '',
                            firstName: '',
                            lastName: '',
                            roles: ''
                        };
                        this.$emit('exit');
                    }
                });
                this.errorOccurred = false;
            },
            cancel(){
                this.$emit('exit');
            },
            deleteUser(){

            }
        },
        computed:{
            getError() {
                return this.$store.getters.getErrorMsg;
            },
        }
    }
</script>

<style scoped>
    #cancelButton {
        margin-left: 10px;
    }
</style>
