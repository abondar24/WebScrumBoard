<template>
    <div id="root">
        <b-navbar type="dark" variant="dark">
            <b-navbar-nav tabs>
                <b-nav-item v-on:click="routeBack">{{project.name}}</b-nav-item>
            </b-navbar-nav>
            <NavbarRight></NavbarRight>
        </b-navbar>
        <b-container></b-container>
    </div>
</template>

<script>
    import NavbarRight from "./NavbarRight";

    export default {
        name: "ProjectBoard",
        components: {NavbarRight},
        data() {
            return {
                project: {
                    id: 0,
                name: '',
                startDate: null,
                endDate: null,
                repository: '',
                description: '',
                active: false
            }
            }
        },
        beforeMount() {
           this.project = this.getProject;
        },
        methods: {
            routeBack() {
                this.$router.push({path: '/project/'+ this.$route.params.id});
            }
        },
        computed:{
            getProject() {
                return this.$store.getters.getProject;
            },
        }
    }
</script>

<style scoped>

</style>
