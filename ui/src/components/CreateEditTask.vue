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
            {{$t('ctr_assigned')}}
        </b-alert>

        <b-form @submit.prevent="submitData">

            <b-input-group id="assignGrp" class="mb-3">
                <b-input-group-text slot="prepend" size="sm">{{$t('assignee')}}</b-input-group-text>
                <b-form-input v-model="ctrLogin" :state="ctrValidation" v-bind:placeholder="$t('login')"></b-form-input>

                <b-input-group-append>
                    <b-button variant="outline-primary" v-on:click="findContributor">{{$t('find')}}</b-button>
                </b-input-group-append>
            </b-input-group>

            <b-form-group id="tsName" label="Task name" label-for="nameInput">
                <div v-if="!isEdit">
                    <b-form-input
                            id="tsName"
                            v-model="taskData.taskName"
                            required
                            :state="nameValidation"
                            v-bind:placeholder="$t('enter_name')">
                    </b-form-input>
                </div>
                <div v-if="isEdit">
                    <b-form-input
                            id="name"
                            v-model="taskData.taskName"
                            v-bind:placeholder="$t('enter_name')">
                    </b-form-input>
                </div>
            </b-form-group>
            <b-form-textarea
                    id="name"
                    v-model="taskData.taskDescription"
                    v-bind:placeholder="$t('description')">
            </b-form-textarea>
            <b-form-group id="startDate" v-bind:label="$t('start_date')" label-for="dateInput">
                <datepicker v-model="taskData.startDate" :format="dateFormat" v-bind:placeholder="$t('select_date')"></datepicker>
            </b-form-group>

            <b-form-checkbox
                    id="devOps"
                    v-model="taskData.devOps"
                    name="devOps">
                {{$t('dev_ops')}}
            </b-form-checkbox>


            <div v-if="isEdit">
                <b-button v-b-modal.deleteTask variant="danger" id="deleteTask">
                   {{$t('delete')}}
                </b-button>

                <b-modal id="deleteTask"
                         ref="tsDelete"
                         @ok="deleteTask()"
                         variant="danger"
                         v-bind:title="$t('task_delete')">
                    {{$t('task_delete_conf')}}
                </b-modal>

                <b-form-select v-model="taskData.storyPoints" :options="storyPoints" size="sm"
                               class="mt-3"></b-form-select>

                <b-form-select v-model="taskData.sprintId" :options="sprints" size="sm" class="mt-3"></b-form-select>

            </div>

            <div id="saveDiv">
                <b-button type="submit" variant="primary" id="submitButton">
                    {{$t('save')}}
                </b-button>

                <b-button id="cancelButton" @click="cancel">{{$t('cancel')}}</b-button>

            </div>
        </b-form>

    </b-container>
</template>

<script>
    import Datepicker from "vuejs-datepicker";

    export default {
        props: ['isEdit','id'],
        name: "CreateEditTask",
        components: {
            Datepicker
        },
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                ctrLogin: '',
                ctrFound: false,
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
                ],
                sprints: [],

            }
        },
        beforeMount() {

            if (this.isEdit){
                this.$store.dispatch('findSprints',
                    {
                        projectId: this.getProjectId,
                        offset: 0
                    }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {

                        for (let i=0;i<this.getSprints.length;i++){
                            this.sprints.push({
                                value:this.getSprints[i].id,
                                text:this.getSprints[i].name
                            })
                        }
                    }
                });
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

                if (this.ctrFound) {
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

                    if (this.taskData.sprintId!==0){
                        this.$store.dispatch('updateTaskSprint', this.taskData).then(() => {
                            this.errorMessage = this.getError;
                            if (this.errorMessage.length) {
                                this.errorOccurred = true;
                            } else {
                                this.$emit('exit');
                            }
                        });
                    }
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
            deleteTask() {
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
            findContributor() {
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
            getProjectId() {
                return this.$store.getters.getProjectId;
            },
            getSprints() {
                return this.$store.getters.getSprints;
            },
            getContributor() {
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
