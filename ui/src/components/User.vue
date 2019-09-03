<template>
    <div id="root">
        <NavbarCommon/>
        <b-container>
            <b-row>
                <b-col>
                    <b-img v-attr="loadAvatar()" rounded="circle" alt="Circle image"></b-img>
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
        data(){
        },
        methods: {
            loadAvatar(){
               let image = '../../assets/emptyAvatar.png';
                if ('avatar' in  this.getUser){
                    image = this.getUser.avatar;
                }

                return image;
            }
        },
        computed:{
            getUser() {
                    return this.$store.getters.getUser;
            }
        }
    }
</script>

<style scoped>

</style>
