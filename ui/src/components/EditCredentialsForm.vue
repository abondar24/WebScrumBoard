<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="warning">
            {{errorMessage}}
        </b-alert>
        <b-form>

            <b-input-group id="loginGrp" class="mb-3">
                <b-input-group-text slot="prepend" size="sm">New Login</b-input-group-text>
                <b-form-input v-model="login" :state="loginValidation"></b-form-input>

                <b-input-group-append>
                    <b-button variant="outline-primary" v-on:click="updateLogin">Update</b-button>
                </b-input-group-append>
            </b-input-group>


            <b-input-group id="passGrp" class="mb-3">
                <b-input-group-text slot="prepend">New Password</b-input-group-text>
                <b-form-input  type="password" v-model="password" :state="passwordValidation">

                </b-form-input>
                <b-input-group-append>
                    <b-button variant="outline-success" v-on:click="updatePassword">Update</b-button>
                </b-input-group-append>
                <b-form-text id="password-help-block">
                    Your password must be 5-20 characters long, contain letters and numbers.
                </b-form-text>
                <b-form-invalid-feedback :state="passwordValidation">
                    Password is too short and contains only letters or numbers
                </b-form-invalid-feedback>
            </b-input-group>


            <b-button variant="danger" @click="cancel">Cancel</b-button>

        </b-form>
    </b-container>
</template>

<script>
    export default {
        name: "EditCredentialsForm",
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                login:'',
                password:''
            }
        },
        methods:{
            submit(){

            },
            cancel(){
                this.$emit('close');
            },
            updateLogin(){

            },
            updatePassword(){

            }
        },
        computed:{
            loginValidation() {
                return this.login.length > 4
            },
            passwordValidation() {
                return this.password.length > 4 && this.password.match(/^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$/) != null
            }
        }
    }
</script>

<style scoped>

</style>
