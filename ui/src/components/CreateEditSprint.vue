<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>

        <b-form @submit.prevent="submitData">
            <b-form-group id="name" label="Sprint name" label-for="nameInput">
               <div v-if="!isEdit">
                   <b-form-input
                           id="name"
                           v-model="sprintData.name"
                           required
                           :state="nameValidation"
                           placeholder="Enter name">
                   </b-form-input>
               </div>
                <div v-if="isEdit">
                    <b-form-input
                            id="name"
                            v-model="sprintData.name"
                            placeholder="Enter name">
                    </b-form-input>
                </div>

            </b-form-group >
            <b-form-group id="startDate" label="Start date" label-for="dateInput">
                <datepicker v-model="sprintData.startDate" :format="dateFormat" placeholder="Select date"></datepicker>
            </b-form-group>
            <b-form-group id="endDate" label="End date" label-for="dateInput">
                <datepicker v-model="sprintData.endDate" :format="dateFormat" placeholder="Select date"></datepicker>

            </b-form-group>


            <b-button type="submit" variant="primary" id="submitButton">
                {{btnName}}
            </b-button>


            <b-button variant="danger" id="cancelButton" @click="cancel">Cancel</b-button>

        </b-form>
    </b-container>
</template>

<script>
    import Datepicker from "vuejs-datepicker/dist/vuejs-datepicker.esm.js";

    export default {
        props: ['id','isEdit'],
        name: "CreateEditSprint",
        components: {
            Datepicker
        },
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                sprintData: {
                    id: 0,
                    name: "",
                    startDate: null,
                    endDate: null,
                    projectId: 0,
                    current: false
                },
                dateFormat:'dd/MM/yyyy',
                btnName:''
            }
        },
        created(){

            if (this.isEdit){
               this.btnName = "Edit";
                this.sprintData.id = this.id;
            } else {
                this.btnName = "Create";
                this.sprintData.projectId = this.id;
            }
        },
        methods:{
            submitData(){

                if (this.sprintData.startDate!==null){
                    this.sprintData.startDate = this.convertDate(this.sprintData.startDate);
                }

                if (this.sprintData.endDate!==null){
                    this.sprintData.endDate = this.convertDate(this.sprintData.endDate);
                }

                if (!this.isEdit){
                    this.$store.dispatch('createSprint', this.sprintData).then(() => {
                        this.errorMessage = this.getError;
                        if (this.errorMessage.length) {
                            this.errorOccurred = true;
                        } else {
                            this.$emit('exit');
                        }
                    });
                } else {

                    this.$store.dispatch('updateSprint', this.sprintData).then(() => {
                        this.errorMessage = this.getError;
                        if (this.errorMessage.length) {
                            this.errorOccurred = true;
                        } else {
                            this.$emit('exit');
                        }
                    });
                }



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
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            nameValidation() {
                return this.sprintData.name.length > 0;
            },
        }
    }
</script>

<style scoped>
 #submitButton {
     margin-right: 10px;
 }
</style>
