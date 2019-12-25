<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>

        <b-alert
                :show="ctrFound"
                dismissible
                variant="success">
            Contributor is assigned to task
        </b-alert>

        <b-form @submit.prevent="submitData">

            <b-input-group id="assignGrp" class="mb-3">
                <b-input-group-text slot="prepend" size="sm">Assignee</b-input-group-text>
                <b-form-input v-model="ctrLogin" :state="ctrValidation" placeholder="login"></b-form-input>

                <b-input-group-append>
                    <b-button variant="outline-primary" v-on:click="findContributor">Find</b-button>
                </b-input-group-append>
            </b-input-group>

            <b-form-group id="tsName" label="Task name" label-for="nameInput">
                <div v-if="!isEdit">
                    <b-form-input
                            id="tsName"
                            v-model="taskData.taskName"
                            required
                            :state="nameValidation"
                            placeholder="Enter name">
                    </b-form-input>
                </div>
                <div v-if="isEdit">
                    <b-form-input
                            id="name"
                            v-model="taskData.taskName"
                            placeholder="Enter name">
                    </b-form-input>
                </div>
            </b-form-group>
            <b-form-textarea
                    id="name"
                    v-model="taskData.taskDescription"
                    placeholder="Description">
            </b-form-textarea>
            <b-form-group id="startDate" label="Start date" label-for="dateInput">
                <datepicker v-model="taskData.startDate" :format="dateFormat" placeholder="Select date"></datepicker>
            </b-form-group>

            <b-form-checkbox
                    id="devOps"
                    v-model="taskData.devOps"
                    name="devOps">
                DevOps required
            </b-form-checkbox>

            <b-form-select v-model="taskData.storyPoints" :options="storyPoints" size="sm" class="mt-3"></b-form-select>

            <div v-if="isEdit">
                <b-button v-b-modal.deleteTask variant="danger" id="deleteTask">
                    Delete
                </b-button>

                <b-modal id="deleteTask"
                         ref="tsDelete"
                         @ok="deleteTask()"
                         variant="danger"
                         title="Delete Task" >
                    Are you sure ,you want to delete this task?
                </b-modal>

            </div>

            <div id="saveDiv">
                <b-button type="submit" variant="primary" id="submitButton">
                    Save
                </b-button>

                <b-button id="cancelButton" @click="cancel">Cancel</b-button>

            </div>
        </b-form>

    </b-container>
</template>

<script>
    import Datepicker from "vuejs-datepicker";

    export default {
        props: ['isEdit'],
        name: "CreateEditTask",
        components: {
            Datepicker
        },
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                ctrLogin: '',
                ctrFound:false,
                taskData: {
                    id: 0,
                    ctrId: 0,
                    taskName: "",
                    startDate: null,
                    devOps: null,
                    storyPoints: 0,
                    taskDescription: "",
                    sprintId: 0
                },
                dateFormat: 'dd/MM/yyyy',
                btnName: '',
                storyPoints: [
                    {value: 0, text: '0'},
                    {value: 1, text: '1'},
                    {value: 2, text: '2'},
                    {value: 3, test: '3'},
                    {value: 5, text: '5'},
                    {value: 8, text: '8'},
                    {value: 13, text: '13'},
                    {value: 20, text: '20'},
                    {value: 40, text: '40'},
                    {value: 100, text: '100'}
                ]
            }
        },
        created() {

            if (this.isEdit) {
                this.btnName = "Edit";
                this.taskData.id = this.id;

            } else {
                this.btnName = "Create";
            }
        },
        methods: {
            submitData() {
                if (this.taskData.startDate !== null) {
                    this.taskData.startDate = this.convertDate(this.taskData.startDate);
                }

                if (this.ctrFound){
                    this.taskData.ctrId = this.getContributor.id;
                }

                if (!this.isEdit) {
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

            convertDate(rawDate) {
                let date = new Date(rawDate);

                let dd = date.getDate();
                let mm = date.getMonth() + 1;
                let yyyy = date.getFullYear();

                if (dd < 10) {
                    dd = '0' + dd;
                }

                if (mm < 10) {
                    mm = '0' + mm;
                }

                return dd + '/' + mm + '/' + yyyy;
            },
            cancel() {
                this.$emit('exit');
            },
            deleteTask(){
                this.$store.dispatch('deleteTask', this.taskData.id).then(() => {
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
            findContributor(){
                this.ctrLogin;
                this.$store.dispatch('findContributorByLogin', this.ctrLogin).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.ctrFound = true;
                    }
                });


                this.errorOccurred = false;
                this.errorMessage = '';
            }
        },
        computed: {
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getContributor(){
               return this.$store.getters.getFoundContributor;
            },
            nameValidation() {
                return this.taskData.taskName.length > 0;
            },
            ctrValidation() {
                return this.ctrLogin.length > 0;
            },
        }
    }
</script>

<style scoped>
    #submitButton {
        margin-right: 10px;
    }

    #saveDiv, #deleteTask {
        margin-top: 20px;
    }
</style>
