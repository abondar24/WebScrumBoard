<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="danger">
            {{errorMessage}}
        </b-alert>

        <div v-if="tasks.length>0">
            <b-table
                    hover
                    responsive
                    stacked
                    :items="tasks"
                    :fields="fields">
                <template v-slot:cell(taskName)="data">
                    <b-button id="edit Task" @click="handleEdit(data.item.id)" variant="link">{{data.item.taskName}}</b-button>
                </template>
            </b-table>
            <b-modal id="taskEdit"
                     ref="tsEdit"
                     title="Edit Task"
                     hide-footer>
                <CreateEditTask :isEdit="true" :id="editTask" @exit="hideTaskEdit"></CreateEditTask>
            </b-modal>
        </div>

        <div v-if="tasks.length===0">
            All tasks are assigned to sprints
        </div>

    </b-container>
</template>

<script>
    import CreateEditTask from "./CreateEditTask";

    export default {
        name: "TasksBacklog",
        components: {CreateEditTask},
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                tasks: [],
                fields: [
                    {key: 'taskName', label: ''}
                ],
                editTask:0,
            }
        },
        beforeMount() {
            this.fetchTasks();
        },
        methods: {
            fetchTasks() {
                this.$store.dispatch('findProjectTasks', {
                    prId: this.getProjectId,
                    offset: 0,
                    limit: 5,
                    all: false,
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.tasks=this.getTasks;
                    }
                });

                this.errorMessage = '';
                this.errorOccurred = false;
            },
            handleEdit(tsId){
                this.editTask=tsId;
                this.$refs['tsEdit'].show();
            },
            hideTaskEdit() {
                this.$refs['tsEdit'].hide();
            },
        },
        computed: {
            getError(){
                return this.$store.getters.getErrorMsg;
            },
            getProjectId() {
                return this.$store.getters.getProjectId;
            },
            getTasks() {
                 return this.$store.getters.getProjectTasks;
            }
        }
    }
</script>

<style scoped>

</style>
