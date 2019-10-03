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
                <b-col>
                    <b-img :src="image" v-bind="imgProps" rounded="circle" alt="Circle image"></b-img>
                    <div>
                        <image-uploader
                                :className="['fileinput']"
                                capture="environment"
                                :preview="false"
                                :debug="1"
                                :quality="0.7"
                                max-width="32"
                                max-height="32"
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
                                <span class="upload-caption">Click to upload</span>
                            </label>
                        </image-uploader>
                    </div>

                </b-col>
                <b-col>
                    TABLE WITH TASKS
                </b-col>
            </b-row>
        </b-container>

    </div>
</template>

<script>
    import NavbarCommon from "./NavbarCommon";

    export default {
        name: "User",
        components: {NavbarCommon},
        data() {
            return {
                image: null,
                imgProps: {width: 175, height: 175, class: 'm1'},
                errorMessage: '',
                errorOccurred: false,
            }
        },
        beforeMount() {

            if (this.getUser.avatar == null) {
                this.image = require('@/assets/emptyAvatar.png');
            } else {
                this.image = this.getUser.avatar;
            }

        },
        methods: {
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
            }
        },
        computed: {
            getUser() {
                return this.$store.getters.getUser;
            },
            getError() {
                return this.$store.getters.getErrorMsg;
            },
        }
    }
</script>

<style>
    #fileInput {
        display: none;
    }
</style>
