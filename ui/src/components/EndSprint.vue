<template>
    <b-container>
        <b-row>
            <b-form-select v-model="selectedSprintId" :options="sprints" size="sm" class="mt-3"></b-form-select>
        </b-row>
        <b-row id="buttonRow">
            <b-button variant="primary" id="endButton" @click="endSprint">
               End
            </b-button>

            <b-button id="cancelButton" @click="doExit">Cancel</b-button>

        </b-row>
    </b-container>
</template>

<script>
    export default {
        name: "EndSprint",
        props: ['exit'],
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                selectedSprintId: 0,
                sprints: [],
                tasks:[],
            }
        },
        beforeMount() {

            this.$store.dispatch('findSprints',
                {
                    projectId: this.getProjectId,
                    offset: 0
                }).then(() => {
                this.errorMessage = this.getError;
                if (this.errorMessage.length) {
                    this.errorOccurred = true;
                } else {

                    for (let i = 0; i < this.getSprints.length; i++) {
                        this.sprints.push({
                            value: this.getSprints[i].id,
                            text: this.getSprints[i].name
                        })
                    }
                }
            });

            this.tasks = this.getSprintTasks;
        },
        methods:{
            endSprint(){
               this.updateCurrentSprint();
            },
            updateCurrentSprint(){
                let sprintData = {};
                sprintData.id = this.getCurrentSprint.id;
                sprintData.name = '';
                sprintData.current = false;

                this.$store.dispatch('updateSprint',sprintData).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                      this.setNewCurrentSprint();
                    }
                });

                this.errorMessage = '';
                this.errorOccurred = false;

            },
            setNewCurrentSprint(){
                let sprintData = {};
                sprintData.id = this.selectedSprintId;
                sprintData.name = '';
                sprintData.current = true;

                this.$store.dispatch('updateSprint',sprintData).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        let newCurrentSprint = this.getSprints.find(sp => sp.id === this.selectedSprintId);
                        newCurrentSprint.isCurrent = true;

                        this.$store.commit('setCurrentSprint',newCurrentSprint);

                        if (this.tasks.length>0){
                            this.updateSprintTasks();
                        } else {
                            this.doExit();
                        }

                    }
                });

                this.errorMessage = '';
                this.errorOccurred = false;
            },
            updateSprintTasks(){

                let tsData = {};
                tsData.ids = [];
                tsData.sprintId=this.selectedSprintId;

                for (let i=0;i<this.tasks.length;i++){
                    tsData.ids.push(this.tasks[i].id);
                }

                this.$store.dispatch('updateTasksSprint',tsData).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                       this.doExit();
                    }
                });


                this.errorMessage = '';
                this.errorOccurred = false;
            },
            doExit() {
                this.$emit('exit');
            },
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
            getSprintTasks() {
                return this.$store.getters.getSprintTasks;
            },
            getCurrentSprint() {
                return this.$store.getters.getCurrentSprint;
            },
        }
    }
</script>

<style scoped>
  #buttonRow {
      margin-top: 20px;
  }
</style>
