<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>

        <b-row>
            <div v-if="noSprints">
                {{$t('project_no_sp')}}
            </div>
            <div v-if="!noSprints">

                <b-table
                        id="sprintTable"
                        hover
                        responsive
                        :items="sprints"
                        :fields="spFields"
                        :per-page="perPage"
                        :current-page="spCurrentPage">
                    <template v-slot:cell(sprintName)="data">
                        <b-button variant="link" @click="viewTasks(data.item.id)">{{data.item.sprintName}}</b-button>

                    </template>
                    <template v-slot:cell(sprintActions)="data" v-if="isEditable">
                        <b-button-group>
                            <b-button variant="warning" @click="handleEdit(data.item.id)">{{$t('edit')}}</b-button>
                            <b-button variant="danger" @click="handleDelete(data.item.id)">{{$t('delete')}}</b-button>


                        </b-button-group>
                    </template>


                </b-table>


                <b-modal id="editSprint"
                         ref="spEdit"
                         hide-footer
                         v-bind:title="$t('sprint_edit')">
                    <CreateEditSprint :isEdit="true" :id="editSprint" @exit="hideEdit"></CreateEditSprint>
                </b-modal>

                <b-modal id="deleteSprint"
                         ref="spDelete"
                         @ok="deleteSprint()"
                         variant="danger"
                         v-bind:title="$t('sprint_delete')">
                    {{$t('sprint_delete_conf')}}
                </b-modal>

                <b-pagination
                        v-model="spCurrentPage"
                        :total-rows="totalSprints"
                        @input="loadNextSprints(spCurrentPage-1)"
                        :per-page="perPage"
                        :current-page="spCurrentPage"
                        aria-controls="sprintTable"/>
            </div>

        </b-row>
        <b-row v-if="showTasks">
            <div v-if="noTasks">
                {{$t('sprint_no_tasks')}}
            </div>

            <div v-if="!noTasks">
                <b-table
                        id="taskTable"
                        hover
                        responsive
                        :items="tasks"
                        :fields="tsFields"
                        :per-page="perPage"
                        :current-page="tsCurrentPage">
                </b-table>
                <b-pagination
                        v-model="tsCurrentPage"
                        :total-rows="totalTasks"
                        @input="loadNextTasks(tsCurrentPage-1)"
                        :per-page="perPage"
                        aria-controls="taskTable"/>
            </div>

        </b-row>
    </b-container>
</template>

<script>
    import CreateEditSprint from "./CreateEditSprint";

    export default {
        name: "ViewSprints",
        components: {CreateEditSprint},
        props: ['prId'],
        data() {
            return {
                sprints: [],
                tasks: [],
                errorMessage: '',
                errorOccurred: false,
                spFields: [
                    {key: 'sprintName', label: this.$i18n.translate('sprint_name')},
                    {key: 'sprintStartDate', label:  this.$i18n.translate('start_date')},
                    {key: 'sprintEndDate', label: this.$i18n.translate('end_date')},
                    {key: 'sprintActions', label: ''},
                ],
                tsFields: [
                    {key: 'taskName', label: this.$i18n.translate('task_name')},
                    {key: 'taskStartDate', label: this.$i18n.translate('start_date')},
                    {key: 'taskEndDate', label: this.$i18n.translate('end_date')},
                    {key: 'taskState', label: this.$i18n.translate('task_state')},

                ],
                spCurrentPage: 1,
                tsCurrentPage: 1,
                perPage: 5,
                totalSprints: 0,
                totalTasks: 0,
                spId: 0,
                noSprints: false,
                noTasks: false,
                showTasks: false,
                isEditable: false,
                delSprint: 0,
                editSprint: 0,
                spOffsets: [],
                tsOffsets: []
            }
        },
        beforeMount() {
            this.countSprints();

            if (this.totalSprints > 0) {
                this.findSprints(0);
            } else {
                this.noSprints = true;
            }

            this.isEditable = this.getEditable;

        },
        created() {
            this.$store.watch(
                (state, getters) => getters.getSprintsCount,
                (newVal, oldVal) => {
                    this.totalSprints = newVal;
                    this.cleanSpOffsets();
                }
            );


            this.$store.watch(
                (state, getters) => getters.getTasksCount,
                (newVal, oldVal) => {
                    this.totalTasks = newVal;
                    this.cleanTsOffsets();
                }
            );
        },
        methods: {
            cleanSpOffsets() {
                if ((this.perPage * this.spOffsets.length) - this.totalSprints >= 5) {
                    this.spOffsets.pop();
                }
            },
            cleanTsOffsets() {
                if ((this.perPage * this.tsOffsets.length) - this.totalTasks >= 5) {
                    this.tsOffsets.pop();
                }
            },
            loadNextSprints(index) {
                this.findSprints(this.calcOffset(index));
            },
            loadNextTasks(index) {
                this.findTasks(index);

            },
            calcOffset(index) {
                return index * this.perPage;
            },
            viewTasks(spId) {
                this.spId = spId;
                this.showTasks = true;

                this.countTasks();
                if (this.totalTasks > 0) {
                    this.findTasks(0);
                } else {
                    this.noTasks = true;
                }

            },
            findTasks(offset) {
                if (!this.tsOffsets.includes(offset)) {
                    this.$store.dispatch('findSprintTasks', {
                        sprintId: this.spId,
                        offset: offset,
                        limit: this.perPage
                    }).then(() => {
                        this.errorMessage = this.getError;
                        if (this.errorMessage.length) {
                            this.errorOccurred = true;
                        } else {
                            for (let i = 0; i < this.getTasks.length; i++) {
                                this.tasks.push({
                                    taskName: this.getTasks[i].name,
                                    taskStartDate:
                                        this.formatDate(new Date(this.getTasks[i].startDate)),
                                    taskEndDate:
                                        this.formatDate(new Date(this.getTasks[i].endDate)),
                                    taskState: this.getTasks[i].taskState
                                });
                            }
                            this.tsOffsets.push(offset);
                        }
                    });
                }


                this.errorMessage = '';
                this.errorOccurred = false;
            },
            countTasks() {
                this.$store.dispatch('countSprintTasks', this.spId).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });

                this.totalTasks = this.getTasksCount;

                this.errorMessage = '';
                this.errorOccurred = false;
            },


            findSprints(offset) {

                if (!this.spOffsets.includes(offset)) {
                    this.$store.dispatch('findSprints', {
                        projectId: this.prId,
                        offset: offset,
                        limit: this.perPage
                    }).then(() => {
                        this.errorMessage = this.getError;
                        if (this.errorMessage.length) {
                            this.errorOccurred = true;
                        } else {
                            for (let i = 0; i < this.getSprints.length; i++) {
                                this.sprints.push({
                                    id: this.getSprints[i].id,
                                    sprintName: this.getSprints[i].name,
                                    sprintStartDate:
                                        this.formatDate(new Date(this.getSprints[i].startDate)),
                                    sprintEndDate:
                                        this.formatDate(new Date(this.getSprints[i].endDate)),
                                });

                                this.spOffsets.push(offset);
                            }

                        }
                    });

                }


                this.errorMessage = '';
                this.errorOccurred = false;
            },
            formatDate(rawDate) {
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

            countSprints() {
                this.$store.dispatch('countSprints', this.prId).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });

                this.totalSprints = this.getSprintsCount;
                this.errorMessage = '';
                this.errorOccurred = false;
            },
            handleDelete(spId) {
                this.delSprint = spId;
                this.$refs['spDelete'].show();
            },
            handleEdit(spId) {
                this.editSprint = spId;
                this.$refs['spEdit'].show();
            },

            deleteSprint() {
                this.$store.dispatch('deleteSprint', this.delSprint).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });
                this.errorMessage = '';
                this.errorOccurred = false;
            },
            hideEdit() {
                this.$refs['spEdit'].hide();
            },
        },
        computed: {
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getTasks() {
                return this.$store.getters.getSprintTasks;
            },
            getTasksCount() {
                return this.$store.getters.getSprintTasksNum;
            },
            getSprints() {
                return this.$store.getters.getSprints;
            },
            getSprintsCount() {
                return this.$store.getters.getSprintsCount;
            },
            getEditable() {
                return this.$store.getters.getProjectEditable;
            }
        }
    }
</script>

<style scoped>

</style>
