<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>
        <b-input-group size="md" prepend="Code">
            <b-form-input v-model="code" :state="codeValidation">
            </b-form-input>
            <b-form-invalid-feedback :state="codeValidation">
                Code is empty
            </b-form-invalid-feedback>
        </b-input-group>
        <b-button class="mt-3" variant="success" block @click="verifyCode">Verify</b-button>
    </b-container>

</template>

<script>
    export default {
        name: "SecurityCodeComponent",
        data(){
            return {
                errorOccurred: false,
                errorMessage: '',
                code:''
            }
        },
        methods:{
            verifyCode() {
                this.$store.dispatch('verifyCode', this.code).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.$router.push("/login");
                    }
                });

                this.code = '';
                this.errorMessage='';
                this.errorOccurred = false;
            }
        },
        computed:{
            getError(){
                return this.$store.getters.getErrorMsg;
            },
            codeValidation() {
                return this.code.length > 0 && this.code.match(/^[0-9]+$/) != null;
            },
        }
    }
</script>

<style scoped>

</style>
