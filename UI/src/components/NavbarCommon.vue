<template>
    <b-navbar type="dark" variant="dark">
        <b-navbar-nav tabs>
            <b-nav-item v-on:click="routeToUser">{{ $t('user') }}</b-nav-item>
            <b-nav-item v-on:click="routeToProject">{{ $t('projects') }}</b-nav-item>
        </b-navbar-nav>
        <NavbarRight></NavbarRight>
    </b-navbar>
</template>

<script>
    import NavbarRight from "./NavbarRight";
    export default {
        name: "NavbarCommon",
        components: {NavbarRight},
        methods:{
            routeToUser(){
                this.$router.push({path: '/user/' + this.getUser});
            },
            routeToProject(){
                this.$router.push({path: '/projects'});
            }
        },
        computed:{
            getUser() {
                return this.$store.getters.getUserId;
            }
        }
    }
</script>

<style scoped>

</style>
