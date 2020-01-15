<template>
    <div id="root">
        <div id="heading">
            <b-navbar type="dark" variant="dark">
                <b-navbar-brand href="#">{{ $t('pwd_reset') }}</b-navbar-brand>
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
            {{$t('pwd_reset_warn')}}
        </b-alert>
        <b-alert
                :show="passwordUpdated"
                dismissible
                variant="success">
            {{$t('pwd_updated')}}
        </b-alert>

        <b-col md="3" offset-md="4" id="resetDiv">
            <b-card class="text-left" bg-variant="light">

                <b-input-group id="loginGrp" class="mb-3">
                    <b-input-group-text slot="prepend" size="sm">{{$t('login')}}</b-input-group-text>
                    <b-form-input v-model="login" :state="loginValidation"></b-form-input>

                    <b-input-group-append>
                        <b-button variant="outline-primary" v-on:click="findLogin">{{$t('find')}}</b-button>
                    </b-input-group-append>
                </b-input-group>

                <b-input-group id="codeGrp" class="mb-3">
                    <b-input-group-text slot="prepend">{{$t('sec_code')}}</b-input-group-text>
                    <b-form-input v-model="code" :state="codeValidation">

                    </b-form-input>
                    <b-input-group-append>
                        <b-button variant="outline-warning" v-on:click="enterCode">{{$t('enter')}}</b-button>
                    </b-input-group-append>
                </b-input-group>

                <b-input-group id="passGrp" class="mb-3">
                    <b-input-group-text slot="prepend">{{$t('pwd_new')}}</b-input-group-text>
                    <b-form-input  type="password" v-model="password" :state="passwordValidation"
                                   :disabled="!newPwdEnabled">

                    </b-form-input>
                    <b-input-group-append>
                        <b-button variant="outline-success" v-on:click="updatePass" :disabled="!newPwdEnabled">
                            {{$t('upd')}}
                        </b-button>
                    </b-input-group-append>
                </b-input-group>
                <p> {{$t('pwd_size')}}</p>


                <b-button id="signInBtn" v-on:click="signIn" variant="primary" size="md">
                    {{ $t('sign_in') }}
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
                newPwdEnabled: false,
                passwordUpdated: false
            }
        },
        methods:{
            findLogin(){
                  this.$store.dispatch('getUserByLogin',{
                      login:this.login,
                      isView:false
                  }).then(()=>{
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

                this.errorOccurred = false;
                this.newPwdEnabled = false;
            },
            updatePass(){
                this.$store.dispatch('updatePassword',{
                    oldPassword:'reset',
                    newPassword: this.password
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;

                    } else {
                        this.passwordUpdated = true;
                    }
                });

                this.errorOccurred = false;
                this.passwordUpdated = false;
            },
            signIn(){
                this.$router.push('/login');
            }
        },
        computed: {
            codeValidation() {
                return this.code.length > 0 && this.code.match(/^[0-9]+$/) != null;
            },
            loginValidation() {
                return this.login.length > 0 && this.login!=='deleted'
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
