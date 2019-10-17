<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="warning">
            {{errorMessage}}
        </b-alert>
        <b-form @submit.prevent="submit">
            <b-form-group
                    id="name"
                    label="Project name"
                    label-for="nameInput">
                <b-form-input
                        id="nameInput"
                        v-model="project.name"
                        placeholder="Enter project name">
                </b-form-input>
            </b-form-group>
            <b-form-group
                    id="name"
                    label="Project repository"
                    label-for="repoInput">
                <b-form-input
                        id="repoInput"
                        v-model="project.repository"
                        placeholder="Enter project repository">
                </b-form-input>
            </b-form-group>
            <b-form-textarea
                    id="description"
                    v-model="project.description"
                    placeholder="Enter description..."
                    rows="3"
                    max-rows="6"></b-form-textarea>
            <b-form-checkbox
                    v-model="project.isActive"
                    name="checkbox-1">
                Project active
            </b-form-checkbox>
            <b-form-group id="date" label="End date" label-for="dateInput" v-if="!project.isActive">
                <datepicker v-model="project.endDate" :format="dateFormat"></datepicker>

            </b-form-group>
            <b-button type="submit" variant="primary" id="regButton">
                Edit
            </b-button>
            <b-button variant="danger" id="cancelButton" @click="cancel">Cancel</b-button>


        </b-form>
    </b-container>

</template>

<script>
    import Datepicker from "vuejs-datepicker/dist/vuejs-datepicker.esm.js";

    export default {
        components: {
            Datepicker
        },
        name: "EditProjectForm",
        props: ['exit'],
        data(){
           return {
               errorMessage: '',
               errorOccurred: false,
               project:{
                   id:0,
                   name:'',
                   repository:'',
                   isActive:false,
                   endDate:null,
                   description: ''
               },
               dateFormat:'dd/MM/yyyy'
           }
        },
        beforeMount(){
            this.project.isActive = this.getProject.active;
            this.project.id = this.getProject.id;
        },
        methods: {
            submit(){
                if (this.project.endDate!==null){
                    this.project.endDate = this.convertDate(this.project.endDate);
                }

                this.$store.dispatch('updateProject', this.project).then(() => {
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
        computed:{
            getProject(){
                return this.$store.getters.getProject;
            },
        },
    }
</script>

<style scoped>
    #cancelButton{
        margin-left: 10px;
    }
</style>
