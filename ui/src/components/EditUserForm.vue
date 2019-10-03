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
                        v-model="firstName"
                        required
                        :state="firstNameValidation"
                        placeholder="Enter first name">
                </b-form-input>
            </b-form-group>

            <b-form-group
                    id="lname"
                    label="Last Name"
                    label-for="lnameInput">
                <b-form-input
                        id="lnameInput"
                        v-model="lastName"
                        required
                        :state="lastNameValidation"
                        placeholder="Enter first name">
                </b-form-input>
            </b-form-group>


            <b-form-group
                    id="email"
                    label="Email address"
                    label-for="emailInput"
                    description="We'll never share your email with anyone else.">
                <b-form-input
                        id="emailInput"
                        v-model="email"
                        type="email"
                        required
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
                        :select-size="3"
                        :state="roleValidation"></b-form-select>
                <b-form-text id="password-help-block">
                    Select at least one role
                </b-form-text>
            </b-form-group>

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
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                firstName:'',
                lastName:'',
                email:'',
                roles: [
                    {value: 'DEVELOPER', text: 'Developer'},
                    {value: 'QA', text: 'QA'},
                    {value: 'DEV_OPS', text: 'DevOps'}
                ],
                selectedRoles: [],
            }
        },
        methods:{
            submit(){

            },
            cancel(){
                this.$emit('close');
            },
            deleteUser(){

            }
        },
        computed:{
            roleValidation() {
                return this.selectedRoles.length > 0
            },
            firstNameValidation() {
                return this.firstName.length > 0
            },
            lastNameValidation() {
                return this.firstName.length > 0
            },
        }
    }
</script>

<style scoped>
    #cancelButton {
        margin-left: 10px;
    }
</style>
