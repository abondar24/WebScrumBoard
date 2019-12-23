<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>

        <b-form @submit.prevent="submitData">

            <b-form-group id="startDate" label="Start date" label-for="dateInput">
                <datepicker v-model="taskData.startDate" :format="dateFormat" placeholder="Select date"></datepicker>
            </b-form-group>
        </b-form>
    </b-container>
</template>

<script>
    import Datepicker from "vuejs-datepicker";

    export default {
        props: ['id','isEdit'],
        name: "CreateEditTask",
        components: {
            Datepicker
        },
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                taskData: {
                    id: 0,
                    ctrId:0,
                    taskName: "",
                    startDate: null,
                    devOps: null,
                    storyPoints:0,
                    taskDescription: "",
                    sprintId:0
                },
                dateFormat:'dd/MM/yyyy',
                btnName:''
            }
        },
        created(){

            if (this.isEdit){
                this.btnName = "Edit";
                this.taskData.id = this.id;
            } else {
                this.btnName = "Create";
            }
        },
        methods:{
            submitData(){
                if (this.taskData.startDate!==null){
                    this.taskData.startDate = this.convertDate(this.taskData.startDate);
                }

                if (!this.isEdit){
                    this.$store.dispatch('createTask', this.taskData).then(() => {
                        this.errorMessage = this.getError;
                        if (this.errorMessage.length) {
                            this.errorOccurred = true;
                        } else {
                            this.$emit('exit');
                        }
                    });
                } else {

                    this.$store.dispatch('updateTask', this.taskData).then(() => {
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
                return this.taskData.name.length > 0;
            },
        }
    }
</script>

<style scoped>
    #submitButton {
        margin-right: 10px;
    }
</style>
