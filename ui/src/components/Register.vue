<template>
    <div id="root">
        <div id="heading">
            <b-navbar type="dark" variant="dark">
                <b-navbar-brand href="#">Registration</b-navbar-brand>
            </b-navbar>
        </div>

        <b-col md="4" offset-md="4" id="regForm">
            <b-card class="text-left" bg-variant="light">
                <b-alert
                        :show="errorOccurred"
                        dismissible
                        variant="danger">
                    {{errorMessage}}
                </b-alert>
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

                    <b-button type="submit" variant="primary" id="regButton"
                              @click="showVerify">
                        Register
                    </b-button>
                    <b-button type="reset" variant="danger" id="cancelButton">Cancel</b-button>

                    <b-modal
                            ref="codeModal"
                            v-model="showVerify"
                            hide-header
                            hide-footer
                            no-close-on-backdrop
                            no-close-on-esc>
                        <b-alert
                                :show="codeErrorOccurred"
                                dismissible
                                variant="danger">
                            {{codeError}}
                        </b-alert>
                        <b-input-group size="md" prepend="Code">
                            <b-form-input v-model="code" :state="codeValidation">
                            </b-form-input>
                            <b-form-invalid-feedback :state="codeValidation">
                                Code is empty
                            </b-form-invalid-feedback>
                        </b-input-group>
                        <b-button class="mt-3" variant="success" block @click="verifyCode">Verify</b-button>
                    </b-modal>
                </b-form>
            </b-card>
        </b-col>
    </div>


</template>

<script>


    import {mapGetters} from "vuex";

    export default {
        name: "Register",
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
                codeErrorOccurred: false,
                showVerify: false,
                errorMessage: '',
                codeError: '',
                code: ''
            }

        },
        methods: {
            submit() {

                this.user.roles = this.selectedRoles.join(";");
                this.$store.dispatch('registerUser', this.user).then(() => {
                    this.errorMessage = this.getError;

                    if (this.errorMessage.length) {
                        this.errorOccurred = true;

                    } else {
                        this.showVerify = true;
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
                this.showVerify = false;

            },
            reset() {
                this.$router.push("/");
            },
            verifyCode() {
                this.$store.dispatch('verifyCode', this.code).then(() => {
                    this.codeError = this.getError;
                    if (this.codeError.length) {
                        this.codeErrorOccurred = true;
                    } else {
                        this.$refs['codeModal'].hide();
                        this.$router.push("/login");
                    }
                });

                this.code = '';
                this.codeErrorOccurred = false;
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
            codeValidation() {
                return this.code.length > 0 && this.code.match(/^[0-9]+$/) != null;
            },
            getError(){
                return this.$store.getters.getErrorMsg;
            }
        }
    }
</script>

<style scoped>
    #regForm {
        margin-top: 30px;
    }

    #cancelButton {
        margin-left: 10px;
    }
</style>
