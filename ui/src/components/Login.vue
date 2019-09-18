<template>
    <div id="root">
        <div id="heading">
            <b-navbar type="dark" variant="dark">
                <b-navbar-brand href="#">Sign in</b-navbar-brand>
            </b-navbar>
        </div>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>

        <b-col md="3" offset-md="4" id="loginDiv">
            <b-card class="text-left" bg-variant="light">

                <b-form @submit="submit">
                    <b-form-group
                            label="Login"
                            label-for="logInp">
                        <b-form-input
                                id="logInp"
                                v-model="credentials.login"
                                required
                                :state="loginValidation">
                        </b-form-input>
                        <b-form-invalid-feedback :state="loginValidation">
                        </b-form-invalid-feedback>
                    </b-form-group>

                    <b-form-group label="Password" label-for="pwdInp">
                        <b-form-input
                                id="pwdInp"
                                v-model="credentials.password"
                                required
                                type="password"
                                :state="passwordValidation"
                                aria-describedby="password-help-block">
                        </b-form-input>
                        <b-form-invalid-feedback :state="passwordValidation">
                        </b-form-invalid-feedback>
                    </b-form-group>
                    <b-form-group>
                        <router-link to="/reset">
                            <b-button variant="link" >Forgot password</b-button>
                        </router-link>

                    </b-form-group>


                    <b-button type="submit" variant="primary" size="md">
                        Sign in
                    </b-button>
                </b-form>
            </b-card>
            <br/>
            <p>OR</p>
            <br/>
            <b-button variant="success">Sign in with github</b-button>

        </b-col>
    </div>
</template>

<script>

    export default {
        name: "Login",
        data() {
            return {
                errorOccurred: false,
                errorMessage: '',
                credentials: {
                    login: '',
                    password: ''
                },
            }
        },
        methods: {
            submit() {
                this.$store.dispatch('loginUser', this.credentials).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;

                    } else {
                        this.$router.push({path: '/user/' + this.getId});
                    }
                });
            },
        },
        computed: {
            loginValidation() {
                return this.credentials.login.length > 0
            },
            passwordValidation() {
                return this.credentials.password.length > 0
            },
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getId() {
                return this.$store.getters.getUserId;
            }
        },
    }
</script>

<style scoped>

    #loginDiv {
        margin-top: 30px
    }


</style>
