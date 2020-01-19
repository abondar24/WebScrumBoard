<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="warning">
            {{errorMessage}}
        </b-alert>
        <b-alert
                :show="dataUpdated"
                dismissible
                variant="success">
            {{ $t('creds_updated') }}
        </b-alert>
        <b-form>

            <b-input-group id="loginGrp" class="mb-3">
                <b-input-group-text slot="prepend" size="sm">{{ $t('login_new') }}</b-input-group-text>
                <b-form-input v-model="login" :state="loginValidation"></b-form-input>

                <b-input-group-append>
                    <b-button variant="outline-primary" v-on:click="updateUserLogin">{{ $t('upd') }}</b-button>
                </b-input-group-append>
            </b-input-group>

            <b-input-group id="passGrp" class="mb-3">
                <b-input-group-text slot="prepend">{{ $t('pwd_old') }}</b-input-group-text>
                <b-form-input type="password" v-model="oldPassword" >
                </b-form-input>
            </b-input-group>

            <b-input-group id="passGrp" class="mb-3">
                <b-input-group-text slot="prepend">{{ $t('pwd_new') }}</b-input-group-text>
                <b-form-input type="password" v-model="newPassword" :state="passwordValidation">

                </b-form-input>
                <b-input-group-append>
                    <b-button variant="outline-success" v-on:click="updateUserPassword">{{ $t('upd') }}</b-button>
                </b-input-group-append>
                <b-form-text id="password-help-block">
                    {{ $t('pwd_size') }}
                </b-form-text>
                <b-form-invalid-feedback :state="passwordValidation">
                    {{ $t('pwd_val') }}
                </b-form-invalid-feedback>
            </b-input-group>

            <router-link to="/reset">
                <b-button variant="link">{{ $t('pwd_forgot') }}</b-button>
            </router-link>


            <b-button variant="danger" @click="cancel">{{ $t('cancel') }}</b-button>

        </b-form>
    </b-container>
</template>

<script>
    export default {
        name: "EditCredentialsForm",
        props: ['exit'],
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                login: '',
                newPassword: '',
                oldPassword: '',
                dataUpdated: false
            }
        },
        methods: {
            cancel() {
                this.$emit('exit');
            },
            updateUserLogin() {
                this.$store.dispatch('updateLogin', this.login).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.dataUpdated = true;
                    }
                });

                this.errorMessage = '';
                this.errorOccurred = false;
            },
            updateUserPassword() {
                this.$store.dispatch('updatePassword', {
                    oldPassword: this.oldPassword,
                    newPassword: this.newPassword
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.dataUpdated = true;
                    }
                });

                this.errorMessage = '';
                this.errorOccurred = false;
            }
        },
        computed: {
            loginValidation() {
                return this.login.length > 4
            },
            passwordValidation() {
                return this.newPassword.length > 4 && this.newPassword.match(/^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$/) != null
            },
            getError() {
                return this.$store.getters.getErrorMsg;
            }
        }
    }
</script>

<style scoped>

</style>
