<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="warning">
            {{errorMessage}}
        </b-alert>
        <b-form @submit.prevent="create">
            <b-form-group id="name" label="Project name" label-for="nameInput">
                <b-form-input
                        id="name"
                        v-model="project.name"
                        required
                        :state="nameValidation"
                        placeholder="Enter name">
                </b-form-input>
            </b-form-group >
            <b-form-group id="date" label="Start date" label-for="dateInput">
                <datepicker v-model="project.startDate"></datepicker>

            </b-form-group>

                <b-button type="submit" variant="primary" id="regButton"
                          @click="create">
                    Create
                </b-button>
                <b-button variant="danger" id="cancelButton" @click="cancel">Cancel</b-button>

        </b-form>
    </b-container>
</template>

<script>
    import Datepicker from "vuejs-datepicker/dist/vuejs-datepicker.esm.js";

    export default {

        name: "CreateProjectForm",
        components: {
            Datepicker
        },
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                project: {
                    name: '',
                    startDate: null
                }
            }
        },
        methods: {
            create() {
               console.log(this.project.startDate)
            },
            cancel() {
                this.$emit('exit');
            }
        },
        computed: {
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            nameValidation() {
                return this.project.name.length > 0
            },
        }
    }
</script>

<style scoped>
  #cancelButton{
      margin-left: 10px;
  }
</style>
