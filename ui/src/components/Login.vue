<template>
    <div id="root">
        <div id="heading">
            <b-navbar type="dark" variant="dark">
                <b-navbar-brand href="#">{{ $t('sign_in') }}</b-navbar-brand>
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

                <b-form @submit.prevent="submit">
                    <b-form-group
                            v-bind:label="$t('login')"
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

                    <b-form-group v-bind:label="$t('password')" label-for="pwdInp">
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
                            <b-button variant="link">{{ $t('pwd_forgot') }}</b-button>
                        </router-link>
                    </b-form-group>


                    <b-button type="submit" variant="primary" size="md">
                        {{ $t('sign_in') }}
                    </b-button>
                </b-form>
            </b-card>
            <br/>
            <p>{{ $t('or') }}</p>
            <br/>
            <b-button variant="success">{{ $t('github') }}</b-button>

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
                remember: false,
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

                this.errorMessage = '';
                this.errorOccurred = false;
            },
        },
        computed: {
            loginValidation() {
                return this.credentials.login.length > 0 && this.credentials.login!=='deleted'
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
