<template>
    <b-navbar-nav class="ml-auto" right>
        <b-nav-item-dropdown text="Language">
            <b-dropdown-item href="#">EN(US)</b-dropdown-item>
            <b-dropdown-item href="#">DE</b-dropdown-item>
            <b-dropdown-item href="#">RU</b-dropdown-item>
            <b-dropdown-item href="#">ES</b-dropdown-item>
        </b-nav-item-dropdown>

        <b-button size="sm" class="my-2 my-sm-0" type="submit" v-on:click="logOut">Log out</b-button>
    </b-navbar-nav>



</template>

<script>
    export default {
        name: "NavbarRight",
        methods:{
            logOut(){
                this.$store.dispatch('logOutUser').then(()=>{
                    this.$router.push('/login');
                });

            },

        }
    }
</script>

<style scoped>

</style>
