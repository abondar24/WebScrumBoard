<template>
    <b-container>
        <b-alert
                :show="errorOccurred"
                dismissible
                variant="warning">
            {{errorMessage}}
        </b-alert>
        <b-form @submit.prevent="submit">

            <b-form-group
                    id="fname"
                    v-bind:label="$t('first_name')"
                    label-for="fnameInput">
                <b-form-input
                        id="fnameInput"
                        v-model="updUser.firstName"
                        v-bind:placeholder="$t('fname_pl')">
                </b-form-input>
            </b-form-group>

            <b-form-group
                    id="lname"
                    v-bind:label="$t('last_name')"
                    label-for="lnameInput">
                <b-form-input
                        id="lnameInput"
                        v-model="updUser.lastName"
                        v-bind:placeholder="$t('lname_pl')">
                </b-form-input>
            </b-form-group>


            <b-form-group
                    id="email"
                    label="Email address"
                    label-for="emailInput">
                <b-form-input
                        id="emailInput"
                        v-model="updUser.email"
                        type="email"
                        placeholder="Enter email">
                </b-form-input>
            </b-form-group>

            <b-form-group
                    id="roles"
                    v-bind:label="$t('roles')"
                    label-for="roleInput">
                <b-form-select
                        id="roleInput"
                        v-model="selectedRoles"
                        aria-describedby="password-help-block"
                        :options="roles" multiple
                        :select-size="3"></b-form-select>
            </b-form-group>

            <b-alert :show="true" variant="danger">
                <h2>{{ $t('danger_zone') }}</h2>
                <b-button size="bg" v-b-modal.userDelete>{{ $t('delete_user') }}</b-button>

                <b-modal id="userDelete"
                         ref="userDelete"
                         v-bind:title="$t('delete_user_title')"
                         @ok="performDelete()"
                         v-bind:ok-title="$t('delete')"
                         ok-title=""
                         ok-variant="danger">
                    {{ $t('delete_confirm') }}
                </b-modal>
            </b-alert>

            <b-button type="submit" variant="primary" id="editButton">{{ $t('edit') }}</b-button>
            <b-button variant="danger" id="cancelButton" @click="cancel">{{ $t('cancel') }}</b-button>

        </b-form>
    </b-container>
</template>

<script>
    export default {
        name: "EditUserForm",
        props: ['exit'],
        data() {
            return {
                errorMessage: '',
                errorOccurred: false,
                updUser: {
                    firstName: '',
                    lastName: '',
                    email: '',
                    roles: ''
                },
                roles: [
                    {value: 'DEVELOPER', text: 'Developer'},
                    {value: 'QA', text: 'QA'},
                    {value: 'DEV_OPS', text: 'DevOps'}
                ],
                selectedRoles: [],
                dataUpdated: false
            }
        },
        methods: {
            submit() {
                if (this.selectedRoles.length !== 0) {
                    this.updUser.roles = this.selectedRoles.join(";");
                }

                this.$store.dispatch('updateUser', this.updUser).then(() => {
                    this.errorMessage = this.getError;

                    if (this.errorMessage.length) {
                        this.errorOccurred = true;

                    } else {
                        this.updUser = {
                            email: '',
                            firstName: '',
                            lastName: '',
                            roles: ''
                        };
                        this.$emit('exit');
                    }
                });
                this.errorMessage = '';
                this.errorOccurred = false;
            },
            cancel() {
                this.$emit('exit');
            },
            performDelete() {
                this.$store.dispatch('deleteUser').then(() => {
                    this.errorMessage = this.getError;

                    if (this.errorMessage.length) {
                        this.errorOccurred = true;
                    } else {
                        this.$router.push('/');
                    }
                });
                this.errorMessage = '';
                this.errorOccurred = false;
            }
        },
        computed: {
            getError() {
                return this.$store.getters.getErrorMsg;
            },
        }
    }
</script>

<style scoped>
    #cancelButton {
        margin-left: 10px;
    }
</style>
