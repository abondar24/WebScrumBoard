<template>
    <div id="root">
        <NavbarCommon/>
        <b-container>
            <b-alert
                    :show="errorOccurred"
                    dismissible
                    variant="danger">
                {{errorMessage}}
            </b-alert>
            <b-row>
                <b-col col lg="3">
                    <b-card>
                        <b-img :src="image" v-bind="imgProps" rounded="circle" alt="Circle image"></b-img>
                        <div>
                            <image-uploader
                                    :className="['fileinput']"
                                    capture="environment"
                                    :preview="false"
                                    :debug="1"
                                    :quality="1.00"
                                    max-width="500"
                                    max-height="500"
                                    doNotResize="gif"
                                    :autoRotate="true"
                                    outputFormat="verbose"
                                    @input="setImage"
                                    @onComplete="uploadAvatar">
                                <label for="fileInput" slot="upload-label">
                                    <figure>
                                        <path
                                                class="path1"
                                                d="M9.5 19c0 3.59 2.91 6.5 6.5 6.5s6.5-2.91 6.5-6.5-2.91-6.5-6.5-6.5-6.5 2.91-6.5 6.5zM30 8h-7c-0.5-2-1-4-3-4h-8c-2 0-2.5 2-3 4h-7c-1.1 0-2 0.9-2 2v18c0 1.1 0.9 2 2 2h28c1.1 0 2-0.9 2-2v-18c0-1.1-0.9-2-2-2zM16 27.875c-4.902 0-8.875-3.973-8.875-8.875s3.973-8.875 8.875-8.875c4.902 0 8.875 3.973 8.875 8.875s-3.973 8.875-8.875 8.875zM30 14h-4v-2h4v2z">

                                        </path>
                                    </figure>
                                    <span class="upload-caption">{{ $t('img_upload') }}</span>
                                </label>
                            </image-uploader>
                            <p>{{firstName}} {{lastName}}</p>
                            <p> {{email}}</p>
                            <ul id="example-2">
                                <li v-for="(role, index) in roles">
                                    {{ role }}
                                </li>
                            </ul>
                            <b-button-group v-if="isEditable">
                                <b-button variant="info" v-b-modal.editUser size="sm">{{ $t('user_edit') }}</b-button>
                                <b-button variant="warning" v-b-modal.editCreds size="sm">{{ $t('creds_edit') }}</b-button>
                            </b-button-group>
                            <b-modal
                                    id="editUser"
                                    ref="userEdit"
                                    v-bind:title="$t('user_edit_title')"
                                    hide-footer>
                                <EditUserForm @exit="hideEdit"></EditUserForm>
                            </b-modal>
                            <b-modal
                                    id="editCreds"
                                    ref="credsEdit"
                                    v-bind:title="$t('creds_edit_title')"
                                    hide-footer>
                                <EditCredentialsForm  @exit="hideCreds"></EditCredentialsForm>
                            </b-modal>

                        </div>
                    </b-card>
                </b-col>
                <b-col>
                    <div v-if="noTasks">
                        {{ $t('user_no_tasks') }}
                    </div>
                    <div v-if="!noTasks">
                        <b-table striped hover
                                 :items="tasks"
                                 :fields="fields"
                                 :per-page="perPage"
                                 :current-page="currentPage"/>
                        <b-pagination
                                v-model="currentPage"
                                :total-rows="totalRows"
                                v-on:click="loadNext(currentPage-1)"
                                :per-page="perPage"
                                aria-controls="my-table"/>
                    </div>

                </b-col>
            </b-row>

        </b-container>

    </div>
</template>

<script>
    import NavbarCommon from "./NavbarCommon";
    import EditUserForm from "./EditUserForm";
    import EditCredentialsForm from "./EditCredentialsForm";

    export default {
        name: "User",
        components: {EditCredentialsForm, EditUserForm, NavbarCommon},
        data() {
            return {
                image: null,
                imgProps: {width: 175, height: 175, class: 'm1'},
                errorMessage: '',
                errorOccurred: false,
                firstName: '',
                lastName: '',
                roles: [],
                email: '',
                tasks: [],
                fields: [
                    {key: 'name', label: this.$i18n.translate('task_name')},
                    {key: 'state', label: this.$i18n.translate('task_state')},
                    {key: 'storyPoints', label: this.$i18n.translate('story_points')},
                ],
                currentPage:1,
                perPage: 10,
                isEditable: true,
                totalRows:0,
                noTasks:false,
                userId:0,
                tsOffsets:[],

            }
        },
        beforeMount() {
            this.image = require('@/assets/emptyAvatar.png');

            if (this.$route.params.id!=this.getUser.id){
                this.isEditable = false;
                this.$store.dispatch('getUsersByIds',[this.$route.params.id]).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.fillUserData(this.getViewUser);
                        this.userId=this.getViewUser.id;
                    }
                });
            } else {
               this.fillUserData(this.getUser);
               this.userId=this.getUser.id;
            }

            this.fillUserTasks();
        },
        created() {
            this.$store.watch(
                (state,getters) => getters.getUser,
                (newVal,oldVal) =>{

                    this.firstName = newVal.firstName;
                    this.lastName = newVal.lastName;
                    this.email = newVal.email;
                    this.roles = newVal.roles.split(';').filter(function (role) {
                        return role !== '';
                    });
                }
            );

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
            fillUserData(user){
                if (user.avatar !== null) {
                    this.image = user.avatar;
                }

                this.firstName = user.firstName;
                this.lastName = user.lastName;
                this.email = user.email;
                this.roles = user.roles.split(';').filter(function (role) {
                    return role !== '';
                });
            },
            fillUserTasks(){
              this.findTotalTasks(this.userId);
                if (this.totalRows > 0) {
                    this.findTasks(0);
                } else {
                    this.noTasks = true;
                }
            },
            findTotalTasks(){
                this.$store.dispatch('countUserTasks', this.userId).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    }
                });
                this.totalRows=this.getTasksCount;
                this.errorMessage = '';
                this.errorOccurred = false;
            },
            findTasks(offset) {
                if (!this.tsOffsets.includes(offset)){
                    this.$store.dispatch('findUserTasks', {
                        userId: this.userId,
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
            setImage(output) {
                this.image = output.dataUrl;
            },
            uploadAvatar() {
                this.$store.dispatch('updateAvatar', this.image).then(() => {
                    this.errorMessage = this.getError;
                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                        this.image = require('@/assets/emptyAvatar.png');
                    }
                });
                this.errorMessage = '';
                this.errorOccurred = false;
            },
            loadNext(index){
                this.findTasks(index * this.perPage);
            },
            hideEdit(){
                this.$refs['userEdit'].hide();
            },
            hideCreds(){
                this.$refs['credsEdit'].hide();
            }
        },
        computed: {
            getUser() {
                return this.$store.getters.getUser;
            },
            getViewUser(){
               return this.$store.getters.getViewUser;
            },
            getError() {
                return this.$store.getters.getErrorMsg;
            },
            getTasksCount(){
                return this.$store.getters.getUserTasksNum;
            },
            getTasks() {
                return this.$store.getters.getUserTasks;
            },
        }
    }
</script>

<style>
    #fileInput {
        display: none;
    }
</style>
