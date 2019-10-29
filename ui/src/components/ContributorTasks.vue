<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                variant="warning">
            {{errorMessage}}
        </b-alert>
        <p>Tasks of {{this.user.ctr_name}}</p>


        <b-button variant="danger" @click="close">Hide</b-button>
    </b-container>
</template>

<script>
    export default {
        name: "ContributorTasks",
        props: ['exit','user'],
        data(){
            return {
                errorMessage: '',
                errorOccurred: false,
                tasks:[],
                fields:[
                    {key: 'taskName', label: 'Task Name'},
                    {key: 'taskState', label: 'Task Name'},
                    {key: 'storyPoints', label: ''},
                ],
                currentPage: 1,
                perPage: 5,
                totalRows: 0,
                ctrId:0
            }
        },
       created(){
           this.fillTasks();
        },
        methods:{
            fillTasks(){
                this.$store.dispatch('findContributor', {
                    userId:this.userId,
                    projectId:this.getProject.id
                }).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });

                console.log(this.getContributorId)
            },
            close() {
                this.$emit('exit');
            }
        },
        computed:{
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getContributorId(){
                return this.$store.getters.getSelectedContributorId;
            },
            getProject(){
                return this.$store.getters.getProject;
            }
        }
    }
</script>

<style scoped>

</style>
