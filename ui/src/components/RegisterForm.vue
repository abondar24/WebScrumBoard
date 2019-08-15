<template>
            <b-card bg-variant="light" >
                <div id="alert">
                    <b-alert
                            :show="errorOccurred"
                            dismissible
                            variant="danger">
                        {{errorMessage}}
                    </b-alert>
                    <b-alert
                            :show="showConfirm"
                            variant="success">
                        Registration Successfull. Enter security code from the email
                    </b-alert>
                </div>
                <b-form @submit="submit" @reset="reset">
                    <b-form-group id="login" label="Login" label-for="loginInput">
                        <b-form-input
                                id="login"
                                v-model="user.login"
                                required
                                :state="loginValidation"
                                placeholder="Enter login">
                        </b-form-input>
                        <b-form-invalid-feedback :state="loginValidation">
                            Your login must be have more than 5 characters.
                        </b-form-invalid-feedback>
                    </b-form-group>

                    <b-form-group id="pass" label="Password" label-for="passwordInput">
                        <b-form-input
                                id="pass"
                                v-model="user.password"
                                required
                                type="password"
                                :state="passwordValidation"
                                aria-describedby="password-help-block"
                                placeholder="Enter password">
                        </b-form-input>
                        <b-form-text id="password-help-block">
                            Your password must be 5-20 characters long, contain letters and numbers.
                        </b-form-text>
                        <b-form-invalid-feedback :state="passwordValidation">
                            Password is too short
                        </b-form-invalid-feedback>
                    </b-form-group>

                    <b-form-group
                            id="email"
                            label="Email address"
                            label-for="emailInput"
                            description="We'll never share your email with anyone else.">
                        <b-form-input
                                id="emailInput"
                                v-model="user.email"
                                type="email"
                                required
                                placeholder="Enter email">
                        </b-form-input>
                    </b-form-group>

                    <b-form-group
                            id="fname"
                            label="First Name"
                            label-for="fnameInput">
                        <b-form-input
                                id="fnameInput"
                                v-model="user.firstName"
                                required
                                placeholder="Enter first name">
                        </b-form-input>
                    </b-form-group>

                    <b-form-group
                            id="lname"
                            label="Last Name"
                            label-for="lnameInput">
                        <b-form-input
                                id="lnameInput"
                                v-model="user.lastName"
                                required
                                placeholder="Enter last name">
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

                    <b-button type="submit" variant="primary" id="regButton">Register</b-button>
                    <b-button type="reset" variant="danger" id="cancelButton">Cancel</b-button>
                </b-form>
            </b-card>


</template>

<script>
    import {mapGetters} from "vuex";

    export default {
        name: "RegisterForm",
        data() {
            return {
                user: {
                    login: '',
                    password: '',
                    email: '',
                    firstName: '',
                    lastName: '',
                    roles: ''
                },
                roles: [
                    {value: 'DEVELOPER', text: 'Developer'},
                    {value: 'QA', text: 'QA'},
                    {value: 'DEV_OPS', text: 'DevOps'}
                ],
                selectedRoles: [],
                errorOccurred: false,
                showConfirm: false,
                errorMessage: '',
            }

        },
        methods: {
            submit() {
                this.user.roles = this.selectedRoles.join(";");
                this.$store.dispatch('registerUser', this.user).then(()=>{
                    this.errorMessage = this.getError;

                    if (this.errorMessage.length){
                        this.errorOccurred = true;

                    } else {
                        this.showConfirm = true;
                        this.user = {
                            login: '',
                            password: '',
                            email: '',
                            firstName: '',
                            lastName: '',
                            roles: ''
                        }
                    }


                });

                this.errorOccurred = false;
                this.showConfirm = false;

            },
            reset() {
                this.$router.push("/");
            }
        },
        computed: {
            loginValidation() {
                return this.user.login.length > 4
            },
            passwordValidation() {
                return this.user.password.length > 4
            },
            roleValidation() {
                return this.selectedRoles.length > 0
            },
            ...mapGetters({getError:"getErrorMsg"}),

        }
    }
</script>

<style scoped>
    #cancelButton{
        margin-left: 10px;
    }
</style>
