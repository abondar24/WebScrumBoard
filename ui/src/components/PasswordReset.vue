<template>
    <div id="root">
        <div id="heading">
            <b-navbar type="dark" variant="dark">
                <b-navbar-brand href="#">Password Reset</b-navbar-brand>
            </b-navbar>
        </div>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>
        <b-alert
                :show="passwordReset"
                dismissible
                variant="warning">
            Your password has been reset!
            Please enter a code from email. After this you can enter a new password
        </b-alert>
        <b-col md="3" offset-md="4" id="resetDiv">
            <b-card class="text-left" bg-variant="light">

                <b-input-group id="loginGrp" class="mb-3">
                    <b-input-group-text slot="prepend" size="sm">Login</b-input-group-text>
                    <b-form-input v-model="login" :state="loginValidation"></b-form-input>

                    <b-input-group-append>
                        <b-button variant="outline-primary" v-on:click="findLogin">Find</b-button>
                    </b-input-group-append>
                </b-input-group>

                <b-input-group id="codeGrp" class="mb-3">
                    <b-input-group-text slot="prepend">Security Code</b-input-group-text>
                    <b-form-input v-model="code" :state="codeValidation">

                    </b-form-input>
                    <b-input-group-append>
                        <b-button variant="outline-warning" v-on:click="enterCode">Enter</b-button>
                    </b-input-group-append>
                </b-input-group>

                <b-input-group id="passGrp" class="mb-3">
                    <b-input-group-text slot="prepend">New Password</b-input-group-text>
                    <b-form-input  type="password" v-model="password" :state="passwordValidation"
                                   :disabled="!newPwdEnabled">

                    </b-form-input>
                    <b-input-group-append>
                        <b-button variant="outline-success" v-on:click="updatePass" :disabled="!newPwdEnabled">Update</b-button>
                    </b-input-group-append>
                </b-input-group>
                <p>Your password must be 5-20 characters long, contain letters and numbers.</p>


                <b-button id="signInBtn" v-on:click="signIn" variant="primary" size="md">
                    Sign in
                </b-button>
            </b-card>
        </b-col>

    </div>

</template>

<script>
    export default {
        name: "PasswordReset",
        data(){
            return {
                errorOccurred: false,
                errorMessage: '',
                login: '',
                code:'',
                password:'',
                passwordReset: false,
                newPwdEnabled: false
            }
        },
        methods:{
            findLogin(){
                  this.$store.dispatch('getUserByLogin',this.login).then(()=>{
                      this.errorMessage = this.getError;
                      if (this.errorMessage.length) {
                          this.errorOccurred = true;

                      } else {
                          this.$store.dispatch('resetPassword').then(()=>{
                              this.passwordReset = true;
                          });
                      }
                  });

                  this.errorOccurred = false;
                  this.passwordReset = false;
            },
            enterCode() {
                this.$store.dispatch('verifyCode', this.code).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.newPwdEnabled = true;
                    }
                });
            },
            updatePass(){
                this.$store.dispatch('updatePassword','reset' ,this.password).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;

                    }
                });
            },
            signIn(){
                this.$router.push("/login");
            }
        },
        computed: {
            codeValidation() {
                return this.code.length > 0 && this.code.match(/^[0-9]+$/) != null;
            },
            loginValidation() {
                return this.login.length > 0
            },
            passwordValidation() {
                return this.password.length > 4 && this.password.match(/^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$/) != null
            },
            getError(){
                return this.$store.getters.getErrorMsg;
            }
        }
    }
</script>

<style scoped>
 #resetDiv {
     margin-top: 30px;
 }

 #signInBtn {
     margin-top: 20px;
 }

</style>
