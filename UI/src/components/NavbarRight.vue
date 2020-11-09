<template>
    <b-navbar-nav class="ml-auto" right>
       <LangSelect></LangSelect>

        <b-button size="sm" class="my-2 my-sm-0" type="submit" v-on:click="logOut">{{ $t('log_out') }}</b-button>
    </b-navbar-nav>



</template>

<script>
    import LangSelect from "./LangSelect";
    export default {
        name: "NavbarRight",
        components: {LangSelect},
        methods:{
            logOut(){
                this.$store.dispatch('logOutUser').then(()=>{
                    this.$router.push('/');
                });

            },

        }
    }
</script>

<style scoped>

</style>
