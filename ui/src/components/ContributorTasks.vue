<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                variant="warning">
            {{errorMessage}}
        </b-alert>

        <div v-if="noTasks">
            {{$t('ctr_no_tasks')}}
        </div>

        <div v-if="!noTasks">
            <b-table
                    id="ctrTaskTable"
                    hover
                    responsive
                    :items="tasks"
                    :fields="fields"
                    :per-page="perPage"
                    caption-top
                    :current-page="currentPage">
                <template v-slot:table-caption>Tasks of {{ctrName}}</template>
            </b-table>
            <b-pagination
                    v-model="currentPage"
                    :total-rows="totalRows"
                    @input="loadNext(currentPage-1)"
                    :per-page="perPage"
                    aria-controls="ctrTaskTable"/>
        </div>


        <b-button variant="danger" @click="close">{{$t('hide')}}</b-button>
    </b-container>
</template>

<script>
    export default {
        name: "ContributorTasks",
        props: ['exit', 'user'],
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                tasks: [],
                fields: [
                    {key: 'name', label: this.$i18n.translate('task_name')},
                    {key: 'state', label: this.$i18n.translate('task_state')},
                    {key: 'storyPoints', label:  this.$i18n.translate('story_points')},
                ],
                currentPage: 1,
                perPage: 5,
                totalRows: 0,
                noTasks: false,
                tsOffsets: [],
                ctrName:''
            }
        },
        beforeMount() {
            this.findContributorId();
            this.totalRows=this.getTasksCount;
            this.ctrName=this.user.ctr_name;

            if (this.totalRows > 0) {
                this.findTasks(0);
            } else {
                this.noTasks = true;
            }

        },
        created() {

            this.$store.watch(
                (state, getters) => getters.getTasksCount,
                (newVal, oldVal) => {
                    this.totalRows = newVal;
                    this.cleanTsOffsets();
                }
            );
        },
        methods: {
            cleanTsOffsets(){
                if ((this.perPage * this.tsOffsets.length)-this.totalRows>=5){
                    this.tsOffsets.pop();
                }
            },
            findContributorId() {
                this.$store.dispatch('findContributor', {
                    userId: this.user.id,
                    projectId: this.getProjectId
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.countTasks();
                    }
                });

                this.errorMessage = '';
                this.errorOccurred = false;
            },
            findTasks(offset) {
                if (!this.tsOffsets.includes(offset)){
                    this.$store.dispatch('findContributorTasks', {
                        contributorId: this.getContributorId,
                        offset: offset,
                        limit: this.perPage
                    }).then(() => {
                        this.errorMessage = this.getError;
                        if (this.errorMessage.length) {
                            this.errorOccurred = true;
                        } else {
                            for (let i = 0; i < this.getTasks.length; i++) {
                                this.tasks.push({
                                    name: this.getTasks[i].taskName,
                                    state: this.getTasks[i].taskState,
                                    storyPoints: this.getTasks[i].storyPoints
                                });
                            }
                            this.tsOffsets.push(offset);
                        }
                    });
                }

                this.errorMessage = '';
                this.errorOccurred = false;
            },
            countTasks(){
                this.$store.dispatch('countContributorTasks', this.getContributorId).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });


                this.errorMessage = '';
                this.errorOccurred = false;
            },
            loadNext(index) {
                this.findTasks(index * this.perPage);
            },
            close() {
                this.$emit('exit');
            }
        },
        computed: {
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getContributorId() {
                return this.$store.getters.getSelectedContributorId;
            },
            getProjectId() {
                return this.$store.getters.getProjectId;
            },
            getTasks() {
                return this.$store.getters.getContributorTasks;
            },
            getTasksCount(){
                return this.$store.getters.getContributorTasksNum;
            }
        }
    }
</script>

<style scoped>

</style>
