<template>
    <div id="root">
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>

        <div class="col-md-4 offset-md-4" id="loginDiv">
            <b-card id="loginForm"
                    bg-variant="light">

                <b-form @submit="submit" itle="Log in">
                    <b-form-group id="log" label="Login" label-for="loginInput">
                        <b-form-input
                                id="log"
                                v-model="user.login"
                                required
                                :state="loginValidation"
                                placeholder="Enter login">
                        </b-form-input>
                        <b-form-invalid-feedback :state="loginValidation">
                        </b-form-invalid-feedback>
                    </b-form-group>

                    <b-form-group id="pwd" label="Password" label-for="passwordInput">
                        <b-form-input
                                id="pwd"
                                v-model="user.password"
                                required
                                type="password"
                                :state="passwordValidation"
                                aria-describedby="password-help-block"
                                placeholder="Enter password">
                        </b-form-input>
                        <b-form-invalid-feedback :state="passwordValidation">
                        </b-form-invalid-feedback>
                    </b-form-group>
                    <b-form-group id="pass">
                        <b-form-checkbox
                                id="rememberCheck"
                                v-model="remember"
                                name="rememberMe"
                                value="true"
                                unchecked-value="false">
                            Remember me
                        </b-form-checkbox>
                        <b-button variant="link" v-b-modal.reset> Forgot password</b-button>

                        <b-modal id="reset"
                                 ref="reset"
                                 hide-header
                                 size="sm"
                                 centered>
                            Are you sure you want to reset your password?
                            <div slot="modal-footer" class="w-10">
                                <b-button
                                        variant="danger"
                                        size="md"
                                        v-on:click="resetPassword">
                                    Reset
                                </b-button>

                                <b-button
                                        id="cancelButton"
                                        v-on:click="cancel"
                                        size="md">
                                    Cancel
                                </b-button>

                            </div>
                        </b-modal>
                    </b-form-group>
                </b-form>
            </b-card>
            <br/>
            <p>OR</p>
            <br/>
            <b-button variant="success" id="githubButton">Sign in with github</b-button>

        </div>
    </div>
</template>

<script>
    export default {
        name: "Login",
        data() {
            return {
                errorOccurred: false,
                errorMessage: '',
                user: {
                    login: '',
                    password: ''
                },
                remember: false
            }
        },
        methods: {
            submit() {

            },
            resetPassword() {
                this.$refs['reset'].hide();
                this.$router.push("/reset");
            },
            cancel() {
                this.$refs['reset'].hide();
            }
        },
        computed: {
            loginValidation() {
                return this.user.login.length > 0
            },
            passwordValidation() {
                return this.user.password.length > 0
            },
        }
    }
</script>

<style scoped>

    #loginDiv {
        margin-top: 30px
    }

    #cancelButton {
        alignment: center;
        margin-left: 10px;
    }
</style>
