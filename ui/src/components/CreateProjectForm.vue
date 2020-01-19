<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>
        <b-form @submit.prevent="create">
            <b-form-group id="name" v-bind:label="$t('project_name')" label-for="nameInput">
                <b-form-input
                        id="name"
                        v-model="project.name"
                        required
                        :state="nameValidation"
                        v-bind:placeholder="$t('project_name_pl')">
                </b-form-input>
            </b-form-group >
            <b-form-group id="date" v-bind:label="$t('start_date')" label-for="dateInput">
                <datepicker v-model="project.startDate" :format="dateFormat"></datepicker>

            </b-form-group>

                <b-button type="submit" variant="primary" id="regButton">
                    {{ $t('create') }}
                </b-button>
                <b-button variant="danger" id="cancelButton" @click="cancel">{{ $t('cancel') }}</b-button>

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
                },
                dateFormat:'dd/MM/yyyy'
            }
        },
        methods: {
            create() {
                this.project.startDate = this.convertDate(this.project.startDate);

                this.$store.dispatch('createProject', this.project).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.$emit('exit');
                    }
                });

                this.errorOccurred = false;
                this.errorMessage = '';
            },
            convertDate(rawDate){
                let date = new Date(rawDate);

                let dd = date.getDate();
                let mm = date.getMonth()+1;
                let yyyy = date.getFullYear();

                if(dd<10) {
                    dd='0'+dd;
                }

                if(mm<10) {
                    mm='0'+mm;
                }

                return dd+'/'+mm+'/'+yyyy;
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
