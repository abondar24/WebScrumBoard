<template>
    <div id="root">
        <div id="heading">
            <b-navbar type="dark" variant="dark">
                <b-navbar-brand href="#">{{ $t('registration') }}</b-navbar-brand>
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
                <b-form @submit.prevent="submit" @reset="reset">
                    <b-form-group id="login" v-bind:label="$t('login')" label-for="loginInput">
                        <b-form-input
                                id="login"
                                v-model="user.login"
                                required
                                :state="loginValidation"
                                v-bind:placeholder="$t('login_pl')">
                        </b-form-input>
                        <b-form-invalid-feedback :state="loginValidation">
                            {{ $t('login_val') }}
                        </b-form-invalid-feedback>
                    </b-form-group>

                    <b-form-group id="pass" v-bind:label="$t('password')" label-for="passwordInput">
                        <b-form-input
                                id="pass"
                                v-model="user.password"
                                required
                                type="password"
                                :state="passwordValidation"
                                aria-describedby="password-help-block"
                                v-bind:placeholder="$t('pwd_pl')">
                        </b-form-input>
                        <b-form-text id="password-help-block">
                            {{ $t('pwd_help') }}
                        </b-form-text>
                        <b-form-invalid-feedback :state="passwordValidation">
                            {{ $t('pwd_val') }}
                        </b-form-invalid-feedback>
                    </b-form-group>

                    <b-form-group
                            id="email"
                            v-bind:label="$t('email')"
                            label-for="emailInput"
                            v-bind:description="$t('email_descr')">
                        <b-form-input
                                id="emailInput"
                                v-model="user.email"
                                type="email"
                                required
                                v-bind:placeholder="$t('email_pl')">
                        </b-form-input>
                    </b-form-group>

                    <b-form-group
                            id="fname"
                            v-bind:label="$t('first_name')"
                            label-for="fnameInput">
                        <b-form-input
                                id="fnameInput"
                                v-model="user.firstName"
                                required
                                v-bind:placeholder="$t('fname_pl')"
                                :state="firstNameValidation">
                        </b-form-input>
                    </b-form-group>

                    <b-form-group
                            id="lname"
                            v-bind:label="$t('last_name')"
                            label-for="lnameInput">
                        <b-form-input
                                id="lnameInput"
                                v-model="user.lastName"
                                required
                                v-bind:placeholder="$t('lname_pl')"
                                :state="lastNameValidation">
                        </b-form-input>
                    </b-form-group>

                    <b-form-group
                            id="roles"
                            v-bind:label="$t('roles')"
                            label-for="roleInput">
                        <b-form-select
                                id="roleInput"
                                v-model="selectedRoles"
                                aria-describedby="password-help-block"
                                :options="roles" multiple
                                :select-size="3"
                                :state="roleValidation"></b-form-select>
                        <b-form-text id="password-help-block">
                            {{ $t('role_select') }}
                        </b-form-text>
                    </b-form-group>

                    <b-button type="submit" variant="primary" id="regButton"
                              @click="showVerify">
                        {{ $t('reg') }}
                    </b-button>
                    <b-button type="reset" variant="danger" id="cancelButton"> {{ $t('cancel') }}</b-button>

                    <b-modal
                            ref="codeModal"
                            v-model="showVerify"
                            v-bind:label="$t('sec_code_title')"
                            hide-footer
                            no-close-on-backdrop
                            no-close-on-esc>
                            <SecurityCodeComponent></SecurityCodeComponent>
                                           </b-modal>
                </b-form>
            </b-card>
        </b-col>
    </div>


</template>

<script>
    import SecurityCodeComponent from "./SecurityCodeComponent";
    export default {
        name: "Register",
        components: {SecurityCodeComponent},
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
                showVerify: false,
                errorMessage: '',
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
        },
        computed: {
            firstNameValidation(){
                return this.user.firstName.length>0
            },
            lastNameValidation(){
                return this.user.lastName.length>0
            },

            loginValidation() {
                return this.user.login.length > 4 && this.user.login.match(/^[a-zA-Z]+$/) != null
            },
            passwordValidation() {
                return this.user.password.length > 4 && this.user.password.match(/^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$/) != null
            },
            roleValidation() {
                return this.selectedRoles.length > 0
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
